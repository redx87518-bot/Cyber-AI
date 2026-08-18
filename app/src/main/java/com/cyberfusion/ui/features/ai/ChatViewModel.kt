package com.cyberfusion.ui.features.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberfusion.core.ai.engine.AITaskEngine
import com.cyberfusion.core.ai.provider.AIProviderConfig
import com.cyberfusion.core.ai.provider.AIProviderFactory
import com.cyberfusion.core.ai.tools.ToolRepositories
import com.cyberfusion.core.database.room.entity.ApiCredentialEntity
import com.cyberfusion.core.database.room.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ChatMessage(val role: String, val content: String)

data class ChatUiState(
    val messages: List<ChatMessage> = listOf(ChatMessage("ai", "Hello, Analyst. I am CyberFusion AI, your cybersecurity career assistant. I can help with SOC analysis, GRC security, and ethical hacking. How can I assist you today?")),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatViewModel(
    private val settingsRepository: SettingsRepository,
    private val repositories: ToolRepositories
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var primaryProvider: AIProviderConfig? = null
    private var fallbackProvider: AIProviderConfig? = null
    private val taskEngine = AITaskEngine(repositories.aiRepository)

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
                val noKeyMessage = "No AI provider configured. Please add an API key in Settings to enable AI-powered security analysis, SOC triage, GRC assessment, and ethical hacking guidance."
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + ChatMessage("ai", noKeyMessage),
                    isLoading = false
                )
                return@launch
            }

            try {
                taskEngine.executeTask(userMessage, provider, repositories).collect { result ->
                    when (result) {
                        is com.cyberfusion.core.ai.engine.AIResult.Processing -> {
                            _uiState.value = _uiState.value.copy(
                                messages = _uiState.value.messages + ChatMessage("ai", result.message),
                                isLoading = true,
                                error = null
                            )
                        }
                        is com.cyberfusion.core.ai.engine.AIResult.Success -> {
                            _uiState.value = _uiState.value.copy(
                                messages = _uiState.value.messages + ChatMessage("ai", result.result),
                                isLoading = false,
                                error = null
                            )
                        }
                        is com.cyberfusion.core.ai.engine.AIResult.Error -> {
                            _uiState.value = _uiState.value.copy(
                                messages = _uiState.value.messages + ChatMessage("ai", "Error: ${result.message}"),
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
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
}
