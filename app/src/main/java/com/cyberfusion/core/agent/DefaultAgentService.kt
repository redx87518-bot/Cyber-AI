package com.cyberfusion.core.agent

import com.cyberfusion.core.ai.provider.AIProviderConfig
import com.cyberfusion.core.ai.provider.AIProviderFactory
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class DefaultAgentService(
    private val settingsRepository: SettingsRepository,
    private val repositories: ToolRepositories,
    private val conversationRepository: ConversationRepository,
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
            finalResult = synthesizeResult(request.prompt, toolResults, evidenceItems, confidence, provider)
            
            val report = if (request.requireReport || request.requirePdf) {
                generateReport(taskId, request.prompt, finalResult, toolResults, plan, timeline, toolsUsed, evidenceItems, confidence, mitreMappings, isoControls)
            } else null
            
            val response = AgentResponse(
                taskId = taskId,
                status = AgentStatus.COMPLETED,
                result = finalResult,
                plan = plan,
                report = report
            )
            
            taskStore[taskId] = task.copy(status = AgentStatus.COMPLETED, result = finalResult)
            emitEvent(AgentEvent(taskId = taskId, eventType = AgentEventType.TASK_COMPLETED, agent = "Orchestrator", tool = null, status = AgentStepStatus.SUCCESS))
            
            response
        } catch (e: Exception) {
            taskStore[taskId] = task.copy(status = AgentStatus.FAILED, error = e.message)
            AgentResponse(taskId, AgentStatus.FAILED, error = e.message)
        }
    }
    
    private lateinit var finalResult: String
    
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
            val providerCreds = credentials.filter { it.provider in listOf("openrouter", "groq", "gemini", "openai") }
            val cred = providerCreds.firstOrNull { true } ?: return null
            AIProviderConfig(
                id = cred.provider,
                name = cred.provider,
                apiKey = cred.apiKey,
                model = getDefaultModel(cred.provider),
                isEnabled = cred.isEnabled
            )
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getDefaultModel(providerId: String): String {
        return when (providerId) {
            "openrouter" -> "mistralai/mistral-7b-instruct"
            "groq" -> "llama2-70b-4096"
            "gemini" -> "gemini-pro"
            "openai" -> "gpt-3.5-turbo"
            else -> "gpt-3.5-turbo"
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
    
    private suspend fun synthesizeResult(prompt: String, toolResults: List<String>, evidenceItems: List<EvidenceItem>, confidence: Double, provider: AIProviderConfig): String {
        if (toolResults.isEmpty()) {
            val adapter = AIProviderFactory().create(provider)
            val result = adapter.chat(listOf(com.cyberfusion.core.ai.provider.Message(role = "user", content = prompt)))
            return result.getOrElse { "Analysis failed: ${it.message}" }
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
        
        val adapter = AIProviderFactory().create(provider)
        val result = adapter.chat(listOf(com.cyberfusion.core.ai.provider.Message(role = "user", content = summaryPrompt)))
        return result.getOrElse { "Tool execution completed, but AI summarization failed: ${it.message}" }
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
            PdfReportGenerator.generateReport(appContext, report, file)
            file.absolutePath
        } catch (e: Exception) {
            ""
        }
    }
    
    private suspend fun emitEvent(event: AgentEvent) {
        _events.emit(event)
    }
    
    private data class ToolExecutionResult(
        val success: Boolean,
        val result: String? = null,
        val error: String? = null
    )
}
