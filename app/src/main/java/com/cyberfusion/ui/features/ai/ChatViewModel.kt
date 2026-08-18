package com.cyberfusion.ui.features.ai

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberfusion.core.ai.provider.AIProviderConfig
import com.cyberfusion.core.ai.provider.AIProviderFactory
import com.cyberfusion.core.ai.provider.AITool
import com.cyberfusion.core.ai.provider.AIToolResult
import com.cyberfusion.core.ai.provider.Message
import com.cyberfusion.core.ai.provider.ToolCall
import com.cyberfusion.core.ai.provider.ToolCallFunction
import com.cyberfusion.core.ai.tools.AIToolRegistry
import com.cyberfusion.core.ai.tools.ToolRepositories
import com.cyberfusion.core.database.room.entity.ConversationEntity
import com.cyberfusion.core.database.room.entity.MessageEntity
import com.cyberfusion.core.database.room.repository.ConversationRepository
import com.cyberfusion.core.database.room.repository.SettingsRepository
import com.cyberfusion.core.utils.PdfReportGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatMessage(val role: String, val content: String, val timestamp: Long = System.currentTimeMillis())

data class ChatUiState(
    val messages: List<ChatMessage> = listOf(ChatMessage("ai", "Hello, Analyst. I am CyberFusion AI, your autonomous cybersecurity agent. I can analyze threats, run investigations, assess GRC risks, and guide your ethical hacking career. Just tell me what you need and I will select the right tools, execute them, and report back with actionable results.")),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentConversationId: Long? = null
)

class ChatViewModel(
    private val settingsRepository: SettingsRepository,
    private val repositories: ToolRepositories,
    private val conversationRepository: ConversationRepository,
    private val appContext: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var primaryProvider: AIProviderConfig? = null
    private var fallbackProvider: AIProviderConfig? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    init {
        loadProviders()
        loadOrCreateConversation()
    }

    private fun loadProviders() {
        viewModelScope.launch {
            try {
                val credentials = settingsRepository.allCredentials.first()
                val providerCreds = credentials.filter { it.provider in listOf("openrouter", "groq", "gemini", "openai") }

                primaryProvider = providerCreds.firstOrNull()?.let { cred ->
                    AIProviderConfig(
                        id = cred.provider,
                        name = cred.provider,
                        apiKey = cred.apiKey,
                        model = getDefaultModel(cred.provider),
                        isEnabled = cred.isEnabled
                    )
                }

                fallbackProvider = providerCreds.getOrNull(1)?.let { cred ->
                    AIProviderConfig(
                        id = cred.provider,
                        name = cred.provider,
                        apiKey = cred.apiKey,
                        model = getDefaultModel(cred.provider),
                        isEnabled = cred.isEnabled
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    private fun loadOrCreateConversation() {
        viewModelScope.launch {
            try {
                val conversations = conversationRepository.getAllConversations().first()
                val activeConversation = conversations.firstOrNull { it.isActive }
                if (activeConversation != null) {
                    val messages = conversationRepository.getMessagesForConversation(activeConversation.id).first()
                    _uiState.value = _uiState.value.copy(
                        currentConversationId = activeConversation.id,
                        messages = messages.map { ChatMessage(it.role, it.content, it.timestamp) }
                    )
                } else {
                    val conversationId = conversationRepository.createConversation("Chat ${dateFormat.format(Date())}")
                    _uiState.value = _uiState.value.copy(currentConversationId = conversationId)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
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

    fun sendMessage(userMessage: String) {
        val currentMessages = _uiState.value.messages
        val userMsg = ChatMessage("user", userMessage)
        _uiState.value = _uiState.value.copy(
            messages = currentMessages + userMsg,
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val conversationId = _uiState.value.currentConversationId ?: return@launch
            conversationRepository.insertMessage(MessageEntity(conversationId = conversationId, role = "user", content = userMessage))

            val provider = primaryProvider ?: fallbackProvider
            if (provider == null || provider.apiKey.isBlank()) {
                val noKeyMessage = "No AI provider configured. Please add an API key in Settings to enable autonomous cybersecurity analysis."
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + ChatMessage("ai", noKeyMessage),
                    isLoading = false
                )
                conversationRepository.insertMessage(MessageEntity(conversationId = conversationId, role = "ai", content = noKeyMessage))
                return@launch
            }

            try {
                val factory = AIProviderFactory()
                val tools = AIToolRegistry.tools
                val systemPrompt = buildSystemPrompt()
                val conversationHistory = buildConversationHistory(systemPrompt)
                
                var finalResponse = ""
                var maxIterations = 10
                var iteration = 0
                var currentMessages = conversationHistory
                
                while (iteration < maxIterations) {
                    iteration++
                    val adapter = factory.create(provider)
                    val result = adapter.chat(currentMessages, tools)
                    
                    result.onSuccess { response ->
                        if (response.startsWith("[TOOL_CALLS:")) {
                            val toolCallsStr = response.removePrefix("[TOOL_CALLS:").removeSuffix("]")
                            val toolCallList = toolCallsStr.split("\n").filter { it.isNotBlank() }
                            
                            val assistantMessage = Message(
                                role = "assistant",
                                content = null,
                                toolCalls = toolCallList.map { callStr ->
                                    val name = callStr.substringBefore("(").substringBefore(" ").trim()
                                    val argsStr = callStr.substringAfter("(").substringBeforeLast(")")
                                    ToolCall(
                                        id = "call_${System.currentTimeMillis()}_${iteration}",
                                        function = ToolCallFunction(name, "{${argsStr}}")
                                    )
                                }
                            )
                            currentMessages = currentMessages + assistantMessage
                            
                            val toolResults = toolCallList.map { callStr ->
                                val toolName = callStr.substringBefore("(").substringBefore(" ").trim()
                                val argsStr = callStr.substringAfter("(").substringBeforeLast(")")
                                val parameters = argsStr.split(", ").associate { 
                                    val parts = it.split("=")
                                    parts.getOrElse(0) { "" }.trim() to parts.getOrElse(1) { "" }.trim()
                                }
                                
                                val toolResult = AIToolRegistry.executeTool(toolName, parameters, repositories)
                                Message(
                                    role = "tool",
                                    content = if (toolResult.success) toolResult.result else "Error: ${toolResult.error}",
                                    toolCallId = "call_${System.currentTimeMillis()}_${iteration}"
                                )
                            }
                            currentMessages = currentMessages + toolResults
                        } else {
                            finalResponse = response
                            break
                        }
                    }.onFailure { error ->
                        finalResponse = "Error: ${error.message}"
                        break
                    }
                }
                
                if (finalResponse.isBlank()) {
                    finalResponse = "I executed the requested security tools but could not synthesize a final response. Please try again with a different query."
                }
                
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + ChatMessage("ai", finalResponse),
                    isLoading = false,
                    error = null
                )
                conversationRepository.insertMessage(MessageEntity(conversationId = conversationId, role = "ai", content = finalResponse))
                
                viewModelScope.launch {
                    generatePdfReport(conversationId, userMessage, finalResponse)
                }
            } catch (e: Exception) {
                val errorMessage = "Error: ${e.message}"
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + ChatMessage("ai", errorMessage),
                    isLoading = false,
                    error = e.message
                )
                conversationRepository.insertMessage(MessageEntity(conversationId = conversationId, role = "ai", content = errorMessage))
            }
        }
    }

    private fun buildSystemPrompt(): String {
        return buildString {
            appendLine("You are CyberFusion AI, an autonomous cybersecurity agent specialized in:")
            appendLine("- SOC Analysis: alert triage, log analysis, incident response, threat hunting")
            appendLine("- GRC Security: risk assessment, compliance (NIST, ISO 27001, GDPR, HIPAA), control frameworks")
            appendLine("- Ethical Hacking: penetration testing, vulnerability assessment, exploitation, reporting")
            appendLine()
            appendLine("You have access to security tools that you can invoke when needed. When you need to use a tool, I will execute it and return the results to you.")
            appendLine("Always provide a concise, actionable response. Include career learning notes.")
            appendLine()
            appendLine("If the user asks for a report, PDF, or summary, generate a comprehensive analysis.")
            appendLine("If the user mentions an image or file, tell them you currently only support text input and ask them to describe it.")
        }
    }

    private fun buildConversationHistory(systemPrompt: String): List<Message> {
        val messages = mutableListOf<Message>()
        messages.add(Message(role = "system", content = systemPrompt))
        _uiState.value.messages.takeLast(20).forEach { msg ->
            messages.add(Message(role = msg.role, content = msg.content))
        }
        return messages
    }

    private suspend fun generatePdfReport(conversationId: Long, query: String, response: String) {
        try {
            val messages = conversationRepository.getMessagesForConversation(conversationId).first()
            val reportContent = buildString {
                appendLine("=== CyberFusion AI Report ===")
                appendLine("Generated: ${dateFormat.format(Date())}")
                appendLine("Conversation ID: $conversationId")
                appendLine()
                appendLine("--- User Query ---")
                appendLine(query)
                appendLine()
                appendLine("--- AI Response ---")
                appendLine(response)
                appendLine()
                appendLine("--- Full Conversation ---")
                messages.forEach { msg ->
                    appendLine("[${dateFormat.format(Date(msg.timestamp))}] ${msg.role.uppercase()}: ${msg.content}")
                }
                appendLine()
                appendLine("--- Career Learning Notes ---")
                appendLine("This analysis was performed by CyberFusion AI, your cybersecurity career assistant.")
                appendLine("Use these results to improve your skills in SOC analysis, GRC security, or ethical hacking.")
            }
            
            val fileName = "cyberfusion_report_${conversationId}_${System.currentTimeMillis()}.pdf"
            val file = File(appContext.getExternalFilesDir(null), fileName)
            PdfReportGenerator.generateReport(appContext, reportContent, file)
        } catch (e: Exception) {
            // PDF generation failed, not critical
        }
    }
}
