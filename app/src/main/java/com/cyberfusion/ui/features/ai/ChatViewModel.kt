package com.cyberfusion.ui.features.ai

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberfusion.core.agent.AgentRequest
import com.cyberfusion.core.agent.AgentResponse
import com.cyberfusion.core.agent.AgentService
import com.cyberfusion.core.report.AgentReport
import com.cyberfusion.core.database.room.entity.ConversationEntity
import com.cyberfusion.core.database.room.entity.MessageEntity
import com.cyberfusion.core.database.room.repository.ConversationRepository
import com.cyberfusion.core.database.room.repository.SettingsRepository
import com.cyberfusion.core.utils.PdfUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatMessage(val role: String, val content: String, val timestamp: Long = System.currentTimeMillis())

data class ChatUiState(
    val messages: List<ChatMessage> = listOf(ChatMessage("ai", "Hello, Analyst. I am CyberFusion AI, your autonomous cybersecurity agent. Tell me what you want investigated, and I will plan, execute, and report back with actionable results.")),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentConversationId: Long? = null,
    val lastReport: AgentReport? = null
)

class ChatViewModel(
    private val settingsRepository: SettingsRepository,
    private val agentService: AgentService,
    private val conversationRepository: ConversationRepository,
    private val appContext: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    init {
        loadOrCreateConversation()
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

            try {
                val request = AgentRequest(
                    taskId = "task_${System.currentTimeMillis()}",
                    prompt = userMessage,
                    requireReport = userMessage.contains("report", ignoreCase = true) || userMessage.contains("pdf", ignoreCase = true)
                )
                
                val response = agentService.execute(request)
                
                val aiMessage = if (response.status == com.cyberfusion.core.agent.AgentStatus.COMPLETED) {
                    response.result ?: "Task completed but no result was returned."
                } else {
                    "Error: ${response.error ?: "Unknown error"}"
                }
                
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + ChatMessage("ai", aiMessage),
                    isLoading = false,
                    error = null,
                    lastReport = response.report
                )
                conversationRepository.insertMessage(MessageEntity(conversationId = conversationId, role = "ai", content = aiMessage))
                
                if (response.report != null) {
                    conversationRepository.insertMessage(MessageEntity(
                        conversationId = conversationId,
                        role = "ai",
                        content = "PDF report generated: ${response.report.reportId}\n\n${response.report.summary}"
                    ))
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
}
