package com.cyberfusion.ui.features.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberfusion.core.ai.provider.AIProviderConfig
import com.cyberfusion.core.ai.provider.AIProviderFactory
import com.cyberfusion.core.database.room.entity.ApiCredentialEntity
import com.cyberfusion.core.database.room.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ChatMessage(val role: String, val content: String)

data class ChatUiState(
    val messages: List<ChatMessage> = listOf(ChatMessage("ai", "Hello, Analyst. How can I assist you today?")),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var primaryProvider: AIProviderConfig? = null
    private var fallbackProvider: AIProviderConfig? = null

    init {
        loadProviders()
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
        _uiState.value = _uiState.value.copy(
            messages = currentMessages + ChatMessage("user", userMessage),
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val provider = primaryProvider ?: fallbackProvider
            if (provider == null || provider.apiKey.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + ChatMessage("ai", "No AI provider configured. Please add an API key in Settings."),
                    isLoading = false
                )
                return@launch
            }

            try {
                val factory = AIProviderFactory()
                val adapter = factory.create(provider)
                val prompt = buildPrompt(_uiState.value.messages, userMessage)
                val result = adapter.chat(prompt)

                result.onSuccess { response ->
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + ChatMessage("ai", response),
                        isLoading = false
                    )
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + ChatMessage("ai", "Error: ${error.message}"),
                        isLoading = false,
                        error = error.message
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + ChatMessage("ai", "Error: ${e.message}"),
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private fun buildPrompt(messages: List<ChatMessage>, newUserMessage: String): String {
        val context = messages.takeLast(10).joinToString("\n") { msg ->
            "${if (msg.role == "user") "User" else "Assistant"}: ${msg.content}"
        }
        return "$context\nUser: $newUserMessage\nAssistant:"
    }
}
