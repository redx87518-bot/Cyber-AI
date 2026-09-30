package com.cyberfusion.core.agent
import com.cyberfusion.core.agent.ToolExecutionResult

import com.cyberfusion.core.ai.provider.AITool
import com.cyberfusion.core.ai.provider.AIToolResult
import com.cyberfusion.core.ai.provider.AIProviderConfig
import com.cyberfusion.core.ai.provider.AIProviderFactory
import com.cyberfusion.core.ai.provider.Message
import com.cyberfusion.core.ai.tools.AIToolRegistry
import com.cyberfusion.core.ai.tools.ToolRepositories
import com.cyberfusion.core.database.room.entity.ConversationEntity
import com.cyberfusion.core.database.room.entity.MessageEntity
import com.cyberfusion.core.database.room.repository.ConversationRepository
import com.cyberfusion.core.database.room.repository.SettingsRepository
import com.cyberfusion.core.evidence.EvidenceManager
import com.cyberfusion.core.evidence.EvidenceItem
import com.cyberfusion.core.utils.PdfReportGenerator
import com.cyberfusion.core.utils.PdfUtils
import com.cyberfusion.core.report.AgentReport
import com.cyberfusion.core.report.AgentFinding
import com.cyberfusion.core.report.AgentEvidence
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class DefaultAgentService(
    private val settingsRepository: SettingsRepository,
    private val repositories: ToolRepositories,
    private val conversationRepository: ConversationRepository,
    private val memoryStore: AgentMemoryStore,
    private val appContext: android.content.Context
) : AgentService {
    private val _events = MutableSharedFlow<AgentEvent>()
    val events: SharedFlow<AgentEvent> = _events.asSharedFlow()
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val taskStore = mutableMapOf<String, AgentTask>()
    
    override suspend fun execute(request: AgentRequest): AgentResponse {
        val taskId = request.taskId.ifBlank { UUID.randomUUID().toString() }
        val task = AgentTask(
            taskId = taskId,
            prompt = request.prompt,
            status = AgentStatus.RUNNING
        )
        taskStore[taskId] = task
        EvidenceManager.clearTask(taskId)
        
        emitEvent(AgentEvent(taskId = taskId, eventType = AgentEventType.TASK_CREATED, agent = "Orchestrator", tool = null, status = AgentStepStatus.RUNNING))
        
        return try {
            val provider = loadProvider()
            if (provider == null) {
                return AgentResponse(taskId, AgentStatus.FAILED, error = "No AI provider configured")
            }
            
            val plan = buildPlan(request.prompt)
            emitEvent(AgentEvent(taskId = taskId, eventType = AgentEventType.PLAN_GENERATED, agent = "Orchestrator", tool = null, status = AgentStepStatus.RUNNING, details = mapOf("steps" to plan.steps.size.toString())))
            
            val toolResults = mutableListOf<String>()
            val timeline = mutableListOf<String>()
            val evidenceItems = mutableListOf<EvidenceItem>()
            val toolsUsed = mutableListOf<String>()
            val mitreMappings = mutableListOf<String>()
            val isoControls = mutableListOf<String>()
            var stepStartTime = System.currentTimeMillis()
            
            for (step in plan.steps) {
                emitEvent(AgentEvent(taskId = taskId, eventType = AgentEventType.TOOL_EXECUTION, agent = step.agent, tool = step.tool, status = AgentStepStatus.RUNNING))
                stepStartTime = System.currentTimeMillis()
                
                val result = executeStep(step, provider)
                val duration = System.currentTimeMillis() - stepStartTime
                
                if (result.success) {
                    toolResults.add("${step.tool ?: "analysis"}: ${result.result}")
                    toolsUsed.add(step.tool ?: "analysis")
                    timeline.add("${dateFormat.format(Date(stepStartTime))} - ${step.description} (SUCCESS, ${duration}ms)")
                    
                    val evidence = EvidenceItem(
                        id = UUID.randomUUID().toString(),
                        taskId = taskId,
                        type = "tool_output",
                        source = step.tool ?: "analysis",
                        content = result.result ?: "",
                        confidence = if (result.error == null) 80.0 else 40.0,
                        verified = result.error == null
                    )
                    evidenceItems.add(evidence)
                    EvidenceManager.addEvidence(taskId, evidence)
                    
                    if (step.tool == "mitreLookup") {
                        result.result?.let { mitreMappings.add(it) }
                    }
                    if (step.tool == "iso27001Lookup") {
                        result.result?.let { isoControls.add(it) }
                    }
                    
                    emitEvent(AgentEvent(taskId = taskId, eventType = AgentEventType.STEP_COMPLETED, agent = step.agent, tool = step.tool, status = AgentStepStatus.SUCCESS, durationMs = duration, details = mapOf("result" to (result.result?.take(200) ?: ""))))
                } else {
                    timeline.add("${dateFormat.format(Date(stepStartTime))} - ${step.description} (FAILED, ${duration}ms)")
                    emitEvent(AgentEvent(taskId = taskId, eventType = AgentEventType.STEP_FAILED, agent = step.agent, tool = step.tool, status = AgentStepStatus.FAILED, durationMs = duration, details = mapOf("error" to (result.error ?: "Unknown"))))
                }
            }
            
            val confidence = EvidenceManager.calculateTaskConfidence(taskId)

            // Agentic tool-calling loop: Rax AI reasons over the full security tool
            // registry with long-term memory injected, calling tools as needed.
            val iocHint = detectIocHint(request.prompt)
            val memoryBlock = runCatching { memoryStore.recall(iocHint) }.getOrDefault("")
            val finalResult = runAgenticLoop(
                taskId = taskId,
                provider = provider,
                prompt = request.prompt,
                priorToolResults = toolResults,
                toolsUsed = toolsUsed,
                confidence = confidence,
                memoryBlock = memoryBlock
            )

            // Persist durable facts the model learned (```memory``` blocks) and
            // strip them from the user-facing answer.
            runCatching { memoryStore.remember(finalResult, iocHint) }
            val userFacingResult = stripMemoryBlocks(finalResult)
            
            val report = if (request.requireReport || request.requirePdf) {
                generateReport(taskId, request.prompt, userFacingResult, toolResults, plan, timeline, toolsUsed, evidenceItems, confidence, mitreMappings, isoControls)
            } else null
            
            val response = AgentResponse(
                taskId = taskId,
                status = AgentStatus.COMPLETED,
                result = userFacingResult,
                plan = plan,
                report = report
            )
            
            taskStore[taskId] = task.copy(status = AgentStatus.COMPLETED, result = userFacingResult)
            emitEvent(AgentEvent(taskId = taskId, eventType = AgentEventType.TASK_COMPLETED, agent = "Orchestrator", tool = null, status = AgentStepStatus.SUCCESS))
            
            response
        } catch (e: Exception) {
            taskStore[taskId] = task.copy(status = AgentStatus.FAILED, error = e.message)
            AgentResponse(taskId, AgentStatus.FAILED, error = e.message)
        }
    }
    
    override suspend fun cancel(taskId: String): Boolean {
        val task = taskStore[taskId] ?: return false
        taskStore[taskId] = task.copy(status = AgentStatus.CANCELLED)
        return true
    }
    
    override fun getHistory(taskId: String): List<AgentEvent> {
        return emptyList()
    }
    
    private suspend fun loadProvider(): AIProviderConfig? {
        return try {
            val credentials = settingsRepository.allCredentials.first()
            val providerCreds = credentials.filter { it.provider in listOf("rax", "openrouter", "groq", "gemini", "openai") && it.isEnabled }
            if (providerCreds.isNotEmpty()) {
                // Rax AI is the platform's primary engine; prefer it, then the
                // user's explicit primary, then the first enabled provider.
                val cred = providerCreds.firstOrNull { it.provider == "rax" }
                    ?: providerCreds.firstOrNull { it.isPrimary }
                    ?: providerCreds.first()
                val model = cred.model.ifBlank { getDefaultModel(cred.provider) }
                return AIProviderConfig(
                    id = cred.provider,
                    name = cred.provider,
                    apiKey = cred.apiKey,
                    model = model,
                    isEnabled = cred.isEnabled
                )
            }
            val localCreds = credentials.filter { it.provider == "local" && it.isEnabled }
            if (localCreds.isNotEmpty()) {
                val cred = localCreds.first()
                return AIProviderConfig(
                    id = "local",
                    name = "Local AI",
                    apiKey = "",
                    model = cred.model.ifBlank { "local" },
                    isEnabled = true,
                    baseUrl = cred.model.ifBlank { "http://localhost:8080" }
                )
            }
            null
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getDefaultModel(providerId: String): String {
        return when (providerId) {
            "rax" -> "rax-4.5"
            "openrouter" -> "mistralai/mistral-7b-instruct"
            "groq" -> "llama2-70b-4096"
            "gemini" -> "gemini-pro"
            "openai" -> "gpt-3.5-turbo"
            else -> "rax-4.5"
        }
    }
    
    internal fun buildPlan(prompt: String): AgentPlan {
        val lower = prompt.lowercase()
        val steps = mutableListOf<AgentPlanStep>()
        
        if (lower.contains("alert") || lower.contains("siem")) {
            steps.add(AgentPlanStep("1", "Retrieve alerts", "SOC Agent", "getAlerts"))
        }
        if (lower.contains("investigate") || lower.contains("incident")) {
            steps.add(AgentPlanStep("2", "Get investigations", "SOC Agent", "getInvestigations"))
        }
        if (lower.contains("ip") || lower.contains("domain") || lower.contains("hash") || lower.contains("url")) {
            steps.add(AgentPlanStep("3", "Enrich IOC", "Threat Intelligence Agent", "enrichIOC"))
        }
        if (lower.contains("risk") || lower.contains("grc") || lower.contains("compliance")) {
            steps.add(AgentPlanStep("4", "Assess GRC risks", "GRC Agent", "getRisks"))
        }
        if (lower.contains("cve") || lower.contains("vulnerability")) {
            steps.add(AgentPlanStep("5", "Lookup CVE", "Vulnerability Agent", "getNvdCve"))
        }
        if (lower.contains("malware") || lower.contains("hash")) {
            steps.add(AgentPlanStep("6", "Query MalwareBazaar", "Threat Intelligence Agent", "queryMalwareBazaar"))
        }
        if (lower.contains("mitre") || lower.contains("attack") || lower.contains("technique")) {
            steps.add(AgentPlanStep("7", "Map MITRE ATT&CK", "Threat Intelligence Agent", "mitreLookup"))
        }
        if (lower.contains("iso") || lower.contains("27001") || lower.contains("control")) {
            steps.add(AgentPlanStep("8", "Map ISO 27001", "GRC Agent", "iso27001Lookup"))
        }
        if (lower.contains("report") || lower.contains("pdf")) {
            steps.add(AgentPlanStep("9", "Generate report", "Report Agent", "generateReport"))
        }
        
        if (steps.isEmpty()) {
            steps.add(AgentPlanStep("1", "Analyze request", "Orchestrator", null))
        }
        
        return AgentPlan(steps, "Execute ${steps.size} steps for: $prompt")
    }
    
    private suspend fun executeStep(step: AgentPlanStep, provider: AIProviderConfig): ToolExecutionResult {
        return try {
            val result = AIToolRegistry.executeTool(step.tool ?: return ToolExecutionResult(false, error = "No tool specified"), emptyMap(), repositories)
            ToolExecutionResult(result.success, result.result, result.error)
        } catch (e: Exception) {
            ToolExecutionResult(false, error = e.message)
        }
    }
    
    /**
     * ReAct-style agentic loop: the model sees the full security tool registry
     * (with real parameter schemas) and its long-term memory, and autonomously
     * calls tools with concrete arguments until it produces a final answer.
     * Tool results are appended to the conversation and fed back for the next
     * reasoning step. Bounded by [MAX_ITERATIONS].
     */
    private suspend fun runAgenticLoop(
        taskId: String,
        provider: AIProviderConfig,
        prompt: String,
        priorToolResults: List<String>,
        toolsUsed: MutableList<String>,
        confidence: Double,
        memoryBlock: String
    ): String {
        val adapter = AIProviderFactory().create(provider)
        val registry = AIToolRegistry.tools
        
        val toolCatalog = registry.joinToString("\n") { tool ->
            val params = if (tool.parameters.isEmpty()) "(no parameters)"
            else tool.parameters.entries.joinToString(", ") { "${it.key}: ${it.value}" }
            "- ${tool.name}($params): ${tool.description}"
        }
        
        val systemPrompt = buildString {
            appendLine("You are CyberFusion AI, an autonomous senior security analyst operating inside an Android SOC platform.")
            appendLine("You have direct access to the following security tools. Call them whenever they help answer the request:")
            appendLine()
            appendLine(toolCatalog)
            appendLine()
            appendLine("Tool-calling rules:")
            appendLine("1. Plan silently, then call the tools you need with concrete, correct arguments (real IPs, hashes, CVE IDs from the user's message or prior results).")
            appendLine("2. You may call several tools in one turn. Prefer enrichIOC for broad indicator lookups; use specific tools for focused checks.")
            appendLine("3. After each tool round, analyze the results and either call more tools or produce the final answer.")
            appendLine("4. Final answers must be actionable: executive summary, key findings, severity, recommended actions, and short career-learning notes for the analyst.")
            appendLine("5. Never invent tool output. If a tool returns nothing useful, say so plainly.")
            if (memoryBlock.isNotBlank()) {
                appendLine()
                appendLine(memoryBlock)
            }
            appendLine()
            appendLine(AgentMemoryStore.MEMORY_PROTOCOL)
        }
        
        val messages = mutableListOf<Message>(
            Message(role = "system", content = systemPrompt),
            Message(role = "user", content = prompt)
        )
        
        // Deterministic plan steps already ran before the loop; expose their
        // results so the model can build on them instead of re-running blindly.
        if (priorToolResults.isNotEmpty()) {
            messages.add(
                Message(
                    role = "user",
                    content = "Pre-executed deterministic tool results:\n" + priorToolResults.joinToString("\n\n")
                )
            )
        }
        
        var finalAnswer: String? = null
        var iteration = 0
        while (iteration < MAX_ITERATIONS && finalAnswer == null) {
            iteration++
            val completion = try {
                adapter.chat(messages, registry).getOrElse { return fallbackSynthesis(prompt, priorToolResults, it.message) }
            } catch (e: Exception) {
                return fallbackSynthesis(prompt, priorToolResults, e.message)
            }
            
            val toolCalls = parseToolCalls(completion)
            if (toolCalls.isEmpty()) {
                finalAnswer = completion
            } else {
                messages.add(Message(role = "assistant", content = completion))
                val resultsBlock = buildString {
                    toolCalls.forEachIndexed { callIndex, call ->
                        toolsUsed.add(call.name)
                        val result = try {
                            AIToolRegistry.executeTool(call.name, call.args, repositories)
                        } catch (e: Exception) {
                            AIToolResult(call.name, false, "", "Exception: ${e.message}")
                        }
                        emitEvent(
                            AgentEvent(
                                taskId = taskId,
                                eventType = AgentEventType.TOOL_EXECUTION,
                                agent = "Rax AI",
                                tool = call.name,
                                status = if (result.success) AgentStepStatus.SUCCESS else AgentStepStatus.FAILED,
                                details = mapOf("args" to call.args.toString().take(200))
                            )
                        )
                        if (callIndex > 0) append("\n\n")
                        append("Tool ${call.name}(${call.args.entries.joinToString(", ") { "${it.key}=${it.value}" }}) ->\n")
                        append(result.result ?: result.error ?: "no output")
                    }
                }
                messages.add(Message(role = "user", content = "TOOL RESULTS:\n$resultsBlock\n\nContinue: call more tools if needed, or give the final answer now."))
            }
        }
        
        return finalAnswer ?: fallbackSynthesis(prompt, priorToolResults, "Iteration limit reached")
    }
    
    /**
     * The model expresses tool calls as fenced blocks; this keeps the loop
     * provider-agnostic (no native function-calling required):
     *
     * ```tool
     * [{"tool":"checkAbuseIPDB","args":{"ip":"1.2.3.4"}}]
     * ```
     */
    private fun parseToolCalls(output: String): List<ToolCall> {
        val block = TOOL_BLOCK_REGEX.find(output)?.groupValues?.get(1) ?: return emptyList()
        return try {
            val array = org.json.JSONArray(block.trim())
            (0 until array.length()).mapNotNull { i ->
                val obj = array.optJSONObject(i) ?: return@mapNotNull null
                val name = obj.optString("tool").takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val argsObj = obj.optJSONObject("args") ?: org.json.JSONObject()
                val args = mutableMapOf<String, String>()
                argsObj.keys().forEach { key -> args[key] = argsObj.optString(key, "") }
                ToolCall(name, args)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun stripMemoryBlocks(text: String): String =
        MEMORY_BLOCK_REGEXCompat.replace(text, "").trim()
    
    private suspend fun fallbackSynthesis(prompt: String, priorToolResults: List<String>, reason: String?): String {
        if (priorToolResults.isEmpty()) {
            return "Agent could not complete reasoning: ${reason ?: "unknown error"}. Check the AI provider key in Settings and retry."
        }
        val combined = priorToolResults.joinToString("\n\n")
        return try {
            val provider = loadProvider() ?: return "Tool results collected, but no AI provider is available for synthesis.\n\n$combined"
            val adapter = AIProviderFactory().create(provider)
            val result = adapter.chat(
                listOf(
                    Message(role = "system", content = "You are CyberFusion AI. Summarize the following raw tool results into an actionable security analysis."),
                    Message(role = "user", content = "Request: $prompt\n\n$combined")
                )
            )
            result.getOrElse { "Tool results (AI synthesis unavailable):\n\n$combined" }
        } catch (e: Exception) {
            "Tool results (AI synthesis failed: ${e.message}):\n\n$combined"
        }
    }
    
    private fun detectIocHint(prompt: String): String? {
        val ip = Regex("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b").find(prompt)?.value
        if (ip != null) return ip
        val hash = Regex("\\b[a-fA-F0-9]{32,64}\\b").find(prompt)?.value
        if (hash != null) return hash
        val cve = Regex("\\bCVE-\\d{4}-\\d{4,7}\\b", RegexOption.IGNORE_CASE).find(prompt)?.value
        if (cve != null) return cve
        val domain = Regex("\\b(?:[a-z0-9-]+\\.)+[a-z]{2,}\\b", RegexOption.IGNORE_CASE).find(prompt)?.value
        return domain
    }
    
    private data class ToolCall(val name: String, val args: Map<String, String>)
    
    private companion object {
        val TOOL_BLOCK_REGEX = Regex("```tool\\s*([\\s\\S]*?)```", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))
        val MEMORY_BLOCK_REGEXCompat = Regex("```memory\\s*([\\s\\S]*?)```", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))
        const val MAX_ITERATIONS = 8
    }
    
    private suspend fun synthesizeResult(prompt: String, toolResults: List<String>, evidenceItems: List<EvidenceItem>, confidence: Double, provider: AIProviderConfig): String {
        if (toolResults.isEmpty()) {
            return try {
                val adapter = AIProviderFactory().create(provider)
                val result = adapter.chat(listOf(com.cyberfusion.core.ai.provider.Message(role = "user", content = prompt)))
                result.getOrElse { "Analysis failed: ${it.message}" }
            } catch (e: Exception) {
                "I processed your request but could not generate an AI summary. Please try again or check your AI provider settings."
            }
        }
        
        val combined = toolResults.joinToString("\n\n")
        val evidenceSummary = evidenceItems.joinToString("\n") { "- [${it.source}] Confidence: ${it.confidence}%, Verified: ${it.verified}" }
        val summaryPrompt = buildString {
            appendLine("You are CyberFusion AI, an autonomous cybersecurity agent.")
            appendLine("User asked: $prompt")
            appendLine("Tools executed and results:")
            appendLine(combined)
            appendLine()
            appendLine("Evidence collected:")
            appendLine(evidenceSummary)
            appendLine()
            appendLine("Overall confidence: ${confidence.toInt()}%")
            appendLine()
            appendLine("Provide a concise, actionable cybersecurity analysis with:")
            appendLine("1. Executive summary")
            appendLine("2. Key findings")
            appendLine("3. Recommended actions")
            appendLine("4. Career learning notes")
        }
        
        return try {
            val adapter = AIProviderFactory().create(provider)
            val result = adapter.chat(listOf(com.cyberfusion.core.ai.provider.Message(role = "user", content = summaryPrompt)))
            result.getOrElse { 
                "Tool execution completed successfully, but AI summarization failed. Here are the raw results:\n\n$combined"
            }
        } catch (e: Exception) {
            "Tool execution completed, but AI summarization encountered an error: ${e.message}. Here are the raw results:\n\n$combined"
        }
    }
    
    private suspend fun generateReport(taskId: String, prompt: String, result: String, toolResults: List<String>, plan: AgentPlan, timeline: List<String>, toolsUsed: List<String>, evidenceItems: List<EvidenceItem>, confidence: Double, mitreMappings: List<String>, isoControls: List<String>): AgentReport {
        val reportId = "RPT-$taskId-${System.currentTimeMillis()}"
        val findings = toolResults.mapIndexed { index, result ->
            AgentFinding(
                id = "F$index",
                title = "Finding $index",
                description = result.take(500),
                severity = "MEDIUM",
                confidence = confidence.toInt()
            )
        }
        
        val evidence = evidenceItems.map { item ->
            AgentEvidence(
                id = item.id,
                type = item.type,
                source = item.source,
                content = item.content.take(1000)
            )
        }
        
        val report = AgentReport(
            reportId = reportId,
            title = "CyberFusion Investigation Report",
            summary = result.take(500),
            findings = findings,
            evidence = evidence,
            recommendations = listOf("Review findings", "Apply recommended actions"),
            severity = "MEDIUM",
            confidence = confidence.toInt(),
            methodology = "Automated agent investigation",
            mitreAttack = mitreMappings,
            iso27001Controls = isoControls,
            metadata = mapOf(
                "userRequest" to prompt,
                "scope" to "Investigation as requested",
                "plan" to plan.steps.joinToString("\n") { "${it.stepId}. ${it.description} (${it.agent})" },
                "toolsUsed" to toolsUsed.joinToString(", "),
                "timeline" to timeline.joinToString("\n"),
                "evidenceCount" to evidenceItems.size.toString(),
                "verifiedEvidenceCount" to evidenceItems.count { it.verified }.toString()
            )
        )
        
        val filePath = generatePdfReport(report)
        val reportWithPath = report.copy(filePath = filePath)
        emitEvent(AgentEvent(taskId = taskId, eventType = AgentEventType.REPORT_GENERATED, agent = "Report Agent", tool = null, status = AgentStepStatus.SUCCESS, details = mapOf("reportId" to reportId)))
        return reportWithPath
    }
    
    private suspend fun generatePdfReport(report: AgentReport): String {
        return try {
            val fileName = "cyberfusion_report_${report.reportId}.pdf"
            val file = File(appContext.getExternalFilesDir(null), fileName)
            val pdfSuccess = PdfReportGenerator.generateReport(appContext, report, file)
            if (pdfSuccess) file.absolutePath else ""
        } catch (e: Exception) {
            ""
        }
    }
    
    private suspend fun emitEvent(event: AgentEvent) {
        _events.emit(event)
    }
    
}
 
