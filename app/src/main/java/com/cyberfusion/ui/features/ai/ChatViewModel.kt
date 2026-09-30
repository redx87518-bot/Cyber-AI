package com.cyberfusion.ui.features.ai

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberfusion.core.agent.AgentMemoryStore
import com.cyberfusion.core.agent.AgentRequest
import com.cyberfusion.core.agent.AgentService
import com.cyberfusion.core.agent.AgentStatus
import com.cyberfusion.core.report.AgentReport
import com.cyberfusion.core.database.room.entity.MessageEntity
import com.cyberfusion.core.database.room.repository.ConversationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: Long,
    val role: String,
    val content: String,
    val timestamp: Long
)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val conversationTitle: String = "New Chat",
    val lastReport: AgentReport? = null,
    val reportFilePath: String? = null,
    val memoryCount: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModel(
    private val agentService: AgentService,
    private val conversationRepository: ConversationRepository,
    private val memoryStore: AgentMemoryStore,
    @Suppress("unused") private val appContext: Context
) : ViewModel() {

    private val conversationId = MutableStateFlow<Long?>(null)
    private val isLoading = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)
    private val lastReport = MutableStateFlow<AgentReport?>(null)
    private val memoryCount = MutableStateFlow(0)

    init {
        viewModelScope.launch {
            conversationId.value = conversationRepository.getActiveConversation()?.id
            refreshMemoryCount()
        }
    }

    private suspend fun refreshMemoryCount() {
        runCatching { memoryStore.count() }
            .onSuccess { memoryCount.value = it }
    }

    private val messagesFlow = conversationId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else conversationRepository.getMessagesForConversation(id)
        }
        .map { entities ->
            entities.map { ChatMessage(it.id, it.role, it.content, it.timestamp) }
        }

    private val titleFlow = conversationRepository.getAllConversations()
        .map { conversations ->
            val active = conversations.firstOrNull { it.isActive } ?: conversations.firstOrNull()
            active?.title ?: "New Chat"
        }

    val uiState: StateFlow<ChatUiState> = combine(
        messagesFlow, titleFlow, isLoading,
        combine(error, lastReport, memoryCount) { e, r, m -> Triple(e, r, m) }
    ) { messages, title, loading, (err, report, memories) ->
        ChatUiState(
            messages = messages,
            isLoading = loading,
            error = err,
            conversationTitle = title,
            lastReport = report,
            reportFilePath = report?.filePath,
            memoryCount = memories
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ChatUiState())

    fun sendMessage(text: String) {
        val content = text.trim()
        if (content.isEmpty() || isLoading.value) return
        isLoading.value = true
        error.value = null

        viewModelScope.launch {
            val convId = getOrCreateConversation(content)
            conversationRepository.insertMessage(
                MessageEntity(conversationId = convId, role = "user", content = content)
            )
            try {
                val wantsReport = content.contains("report", true) || content.contains("pdf", true)
                val response = agentService.execute(
                    AgentRequest(
                        taskId = "task_${System.currentTimeMillis()}",
                        prompt = content,
                        requireReport = wantsReport
                    )
                )
                val reply = when (response.status) {
                    AgentStatus.COMPLETED -> response.result ?: "Task completed without output."
                    else -> "⚠ ${response.error ?: "Unknown error"}"
                }
                conversationRepository.insertMessage(
                    MessageEntity(conversationId = convId, role = "ai", content = reply)
                )
                response.report?.let { lastReport.value = it }
            } catch (e: Exception) {
                val msg = "⚠ ${e.message ?: "Unexpected error"}"
                conversationRepository.insertMessage(
                    MessageEntity(conversationId = convId, role = "ai", content = msg)
                )
                error.value = e.message
            } finally {
                isLoading.value = false
                refreshMemoryCount()
            }
        }
    }

    /** Starts a fresh conversation; the old one stays in history. */
    fun startNewChat() {
        viewModelScope.launch {
            lastReport.value = null
            error.value = null
            val title = java.text.SimpleDateFormat("MMM d · HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date())
            conversationId.value = conversationRepository.createConversation(title)
        }
    }

    private suspend fun getOrCreateConversation(firstMessage: String): Long {
        conversationId.value?.let { return it }
        val active = conversationRepository.getActiveConversation()
        if (active != null) {
            conversationId.value = active.id
            return active.id
        }
        val id = conversationRepository.createConversation(firstMessage.take(40))
        conversationId.value = id
        return id
    }

    companion object {
        val suggestions = listOf(
            "Triage my current alerts",
            "Investigate IP 45.33.32.156",
            "Check CVE-2024-3400",
            "Assess GRC risks",
            "Generate a PDF report"
        )
    }
}
