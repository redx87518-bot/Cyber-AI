package com.cyberfusion.core.ai.engine

import com.cyberfusion.core.ai.provider.AIProvider
import com.cyberfusion.core.ai.provider.AIProviderConfig
import com.cyberfusion.core.ai.provider.AIProviderFactory
import com.cyberfusion.core.ai.tools.AIToolRegistry
import com.cyberfusion.core.ai.tools.ToolRepositories
import com.cyberfusion.core.database.room.entity.AiTaskEntity
import com.cyberfusion.core.database.room.entity.AiTaskHistoryEntity
import com.cyberfusion.core.database.room.entity.AiToolCallEntity
import com.cyberfusion.core.database.room.repository.AiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AITaskEngine(private val aiRepository: AiRepository) {
    suspend fun executeTask(
        prompt: String,
        provider: AIProvider?,
        repositories: ToolRepositories
    ): Flow<AIResult> = flow {
        emit(AIResult.Processing("Starting analysis..."))

        val taskId = aiRepository.insertTask(
            AiTaskEntity(prompt = prompt, provider = provider?.id, status = "processing")
        )

        emit(AIResult.Processing("Selecting security tools..."))

        try {
            if (provider == null) {
                emit(AIResult.Error("No AI provider configured. Add an API key in Settings."))
                aiRepository.updateTaskStatus(taskId, "failed", "No provider configured")
                return@flow
            }

            val config = provider as? AIProviderConfig
                ?: return@flow.also { emit(AIResult.Error("Invalid provider configuration")) }
            val adapter = AIProviderFactory().create(config)

            val toolCalls = selectTools(prompt)

            if (toolCalls.isEmpty()) {
                emit(AIResult.Processing("Running AI analysis..."))
                val aiResponse = adapter.chat(listOf(com.cyberfusion.core.ai.provider.Message(role = "user", content = buildCareerPrompt(prompt))))
                val resultText = aiResponse.getOrElse { "AI analysis failed: ${it.message}" }
                emit(AIResult.Success(resultText))
                aiRepository.updateTaskStatus(taskId, "completed", resultText.take(500))
                aiRepository.insertHistory(
                    AiTaskHistoryEntity(taskId = taskId, provider = provider.id, resultStatus = "success", summarizedResult = "AI analysis", timestamp = System.currentTimeMillis())
                )
                return@flow
            }

            emit(AIResult.Processing("Executing ${toolCalls.size} security tool(s)..."))

            val toolResults = mutableListOf<String>()
            for (toolCall in toolCalls) {
                emit(AIResult.Processing("Running tool: ${toolCall.toolName}..."))
                val result = AIToolRegistry.executeTool(toolCall.toolName, toolCall.parameters, repositories)
                aiRepository.insertToolCall(
                    AiToolCallEntity(taskId = taskId, toolName = toolCall.toolName, parameters = toolCall.parameters.entries.joinToString { "${it.key}=${it.value}" }, result = result.result, timestamp = System.currentTimeMillis())
                )
                if (result.success) {
                    toolResults.add("Tool: ${toolCall.toolName}\nResult: ${result.result}")
                } else {
                    toolResults.add("Tool: ${toolCall.toolName}\nError: ${result.error ?: result.result}")
                }
            }

            emit(AIResult.Processing("Synthesizing results..."))
            val combinedContext = toolResults.joinToString("\n\n")
            val summaryPrompt = buildString {
                appendLine("You are CyberFusion AI, a cybersecurity analyst assistant.")
                appendLine("The user asked: $prompt")
                appendLine("You executed the following security tools and got these results:")
                appendLine(combinedContext)
                appendLine()
                appendLine("Provide a concise, actionable cybersecurity analysis. Include:")
                appendLine("1. Executive summary")
                appendLine("2. Key findings")
                appendLine("3. Recommended actions")
                appendLine("4. Career learning notes (explain what a SOC analyst, GRC specialist, or ethical hacker should learn from this)")
            }

            val summaryResult = adapter.chat(listOf(com.cyberfusion.core.ai.provider.Message(role = "user", content = summaryPrompt)))
            val finalResponse = summaryResult.getOrElse { "Tool execution completed, but AI summarization failed: ${it.message}" }

            emit(AIResult.Success(finalResponse))
            aiRepository.updateTaskStatus(taskId, "completed", finalResponse.take(500))
            aiRepository.insertHistory(
                AiTaskHistoryEntity(taskId = taskId, provider = provider.id, resultStatus = "success", summarizedResult = "Tools executed + AI summary", timestamp = System.currentTimeMillis())
            )
        } catch (e: Exception) {
            emit(AIResult.Error(e.message ?: "Unknown error during task execution"))
            aiRepository.updateTaskStatus(taskId, "failed", e.message)
        }
    }

    suspend fun testConnection(config: AIProviderConfig): Boolean {
        return try {
            val adapter = AIProviderFactory().create(config)
            adapter.validateKey()
        } catch (e: Exception) {
            false
        }
    }

    private data class ToolCall(val toolName: String, val parameters: Map<String, String>)

    private fun selectTools(prompt: String): List<ToolCall> {
        val lower = prompt.lowercase()
        val calls = mutableListOf<ToolCall>()

        if (lower.contains("alert") || lower.contains("ticket") || lower.contains("siem")) {
            calls.add(ToolCall("getAlerts", emptyMap()))
        }
        if (lower.contains("investigation") || lower.contains("case")) {
            calls.add(ToolCall("getInvestigations", emptyMap()))
        }
        if (lower.contains("incident") || lower.contains("ir")) {
            calls.add(ToolCall("getIncidents", emptyMap()))
        }
        if (lower.contains("risk") || lower.contains("grc") || lower.contains("compliance")) {
            calls.add(ToolCall("getRisks", emptyMap()))
            calls.add(ToolCall("getControls", emptyMap()))
        }
        if (lower.contains("lab") || lower.contains("practice") || lower.contains("training")) {
            calls.add(ToolCall("getLabs", emptyMap()))
        }
        if (lower.contains("report")) {
            calls.add(ToolCall("generateReport", mapOf("reportType" to "Incident Analysis")))
        }
        if (lower.contains("enrich") || lower.contains("lookup") || lower.contains("reputation")) {
            val iocValue = extractIocFromPrompt(lower)
            if (iocValue.isNotBlank()) {
                val iocType = detectIocType(iocValue)
                calls.add(ToolCall("enrichIOC", mapOf("iocValue" to iocValue, "iocType" to iocType)))
            }
        }
        if (lower.contains("hash") || lower.contains("malware") || lower.contains("sample")) {
            val hash = extractHashFromPrompt(lower)
            if (hash.isNotBlank()) {
                calls.add(ToolCall("queryMalwareBazaar", mapOf("hash" to hash)))
            }
        }
        if (lower.contains("ip") && (lower.contains("check") || lower.contains("abuse") || lower.contains("reputation"))) {
            val ip = extractIpFromPrompt(lower)
            if (ip.isNotBlank()) {
                calls.add(ToolCall("checkAbuseIPDB", mapOf("ip" to ip)))
            }
        }
        if (lower.contains("cve") || lower.contains("vulnerability")) {
            val cve = extractCveFromPrompt(lower)
            if (cve.isNotBlank()) {
                calls.add(ToolCall("getNvdCve", mapOf("cveId" to cve)))
            }
        }
        if (lower.contains("urlscan") || lower.contains("scan url") || lower.contains("website")) {
            val url = extractUrlFromPrompt(lower)
            if (url.isNotBlank()) {
                calls.add(ToolCall("scanUrl", mapOf("query" to url)))
            }
        }
        if (lower.contains("otx") || lower.contains("alienvault")) {
            val iocValue = extractIocFromPrompt(lower)
            if (iocValue.isNotBlank()) {
                val iocType = detectIocType(iocValue)
                calls.add(ToolCall("getOtxIntel", mapOf("type" to iocType, "value" to iocValue)))
            }
        }
        if (lower.contains("threatfox") || lower.contains("ioc search")) {
            val iocValue = extractIocFromPrompt(lower)
            if (iocValue.isNotBlank()) {
                val iocType = detectIocType(iocValue)
                calls.add(ToolCall("searchThreatFox", mapOf("ioc" to iocValue, "iocType" to iocType)))
            }
        }
        if (lower.contains("status") || lower.contains("configure") || lower.contains("setting")) {
            calls.add(ToolCall("getSettings", emptyMap()))
        }

        return calls.distinctBy { it.toolName }
    }

    private fun buildCareerPrompt(prompt: String): String {
        return buildString {
            appendLine("You are CyberFusion AI, a cybersecurity career assistant specialized in:")
            appendLine("- SOC Analysis: alert triage, log analysis, incident response, threat hunting")
            appendLine("- GRC Security: risk assessment, compliance (NIST, ISO 27001, GDPR, HIPAA), control frameworks")
            appendLine("- Ethical Hacking: penetration testing, vulnerability assessment, exploitation, reporting")
            appendLine()
            appendLine("User query: $prompt")
            appendLine()
            appendLine("Provide a professional, actionable response. Include career guidance where relevant.")
        }
    }

    private fun extractIocFromPrompt(prompt: String): String {
        val patterns = listOf(
            Regex("""\b(?:\d{1,3}\.){3}\d{1,3}\b"""),
            Regex("""\b[a-fA-F0-9]{32,64}\b"""),
            Regex("""\bCVE-\d{4}-\d+\b"""),
            Regex("""\b[\w.-]+\.[a-zA-Z]{2,}\b""")
        )
        for (pattern in patterns) {
            val match = pattern.find(prompt)
            if (match != null) return match.value
        }
        return ""
    }

    private fun extractHashFromPrompt(prompt: String): String {
        val match = Regex("""\b[a-fA-F0-9]{32,64}\b""").find(prompt)
        return match?.value ?: ""
    }

    private fun extractIpFromPrompt(prompt: String): String {
        val match = Regex("""\b(?:\d{1,3}\.){3}\d{1,3}\b""").find(prompt)
        return match?.value ?: ""
    }

    private fun extractCveFromPrompt(prompt: String): String {
        val match = Regex("""\bCVE-\d{4}-\d+\b""").find(prompt)
        return match?.value ?: ""
    }

    private fun extractUrlFromPrompt(prompt: String): String {
        val match = Regex("""https?://[^\s]+""").find(prompt)
        return match?.value ?: ""
    }

    private fun detectIocType(value: String): String {
        return when {
            value.matches(Regex("^\\d+\\.\\d+\\.\\d+\\.\\d+$")) -> "ip"
            value.matches(Regex("^[a-fA-F0-9]{32,64}$")) -> "hash"
            value.startsWith("CVE-") -> "cve"
            value.contains("@") -> "email"
            value.startsWith("http://") || value.startsWith("https://") -> "url"
            else -> "domain"
        }
    }
}

sealed interface AIResult {
    data class Processing(val message: String) : AIResult
    data class Success(val result: String) : AIResult
    data class Error(val message: String) : AIResult
}
