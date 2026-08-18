package com.cyberfusion.ui.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberfusion.core.ai.provider.AIProviderConfig
import com.cyberfusion.core.database.room.entity.ApiCredentialEntity
import com.cyberfusion.core.database.room.repository.SettingsRepository
import com.cyberfusion.core.security.SecureStorage
import kotlinx.coroutines.flow.first

data class ProviderSettings(
    val id: String,
    val name: String,
    val apiKey: String = "",
    val model: String = "",
    val isEnabled: Boolean = false,
    val status: String = "Not Configured",
    val isPrimary: Boolean = false,
    val isFallback: Boolean = false
)

data class ApiSettings(
    val id: String,
    val name: String,
    val apiKey: String = "",
    val isEnabled: Boolean = false,
    val status: String = "Not Configured"
)

data class SettingsUiState(
    val providers: List<ProviderSettings> = listOf(
        ProviderSettings("openrouter", "OpenRouter", model = "mistralai/mistral-7b-instruct"),
        ProviderSettings("groq", "Groq", model = "llama2-70b-4096"),
        ProviderSettings("gemini", "Gemini", model = "gemini-pro"),
        ProviderSettings("openai", "OpenAI", model = "gpt-3.5-turbo")
    ),
    val apis: List<ApiSettings> = listOf(
        ApiSettings("abuseipdb", "AbuseIPDB"),
        ApiSettings("threatfox", "ThreatFox"),
        ApiSettings("malwarebazaar", "MalwareBazaar"),
        ApiSettings("nvd", "NVD")
    ),
    val isLoading: Boolean = false,
    val error: String? = null
)

class SettingsViewModel(
    private val settingsRepository: com.cyberfusion.core.database.room.repository.SettingsRepository,
    private val secureStorage: SecureStorage
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val credentials = settingsRepository.allCredentials.first()
                val updatedProviders = _uiState.value.providers.map { provider ->
                    val credential = credentials.find { it.provider == provider.id }
                    if (credential != null) {
                        provider.copy(
                            apiKey = maskKey(credential.apiKey),
                            isEnabled = credential.isEnabled,
                            status = credential.status
                        )
                    } else {
                        provider
                    }
                }
                val updatedApis = _uiState.value.apis.map { api ->
                    val credential = credentials.find { it.provider == api.id }
                    if (credential != null) {
                        api.copy(
                            apiKey = maskKey(credential.apiKey),
                            isEnabled = credential.isEnabled,
                            status = credential.status
                        )
                    } else {
                        api
                    }
                }
                _uiState.value = _uiState.value.copy(
                    providers = updatedProviders,
                    apis = updatedApis,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun updateProviderApiKey(providerId: String, apiKey: String) {
        val updatedProviders = _uiState.value.providers.map { 
            if (it.id == providerId) it.copy(apiKey = apiKey) else it 
        }
        _uiState.value = _uiState.value.copy(providers = updatedProviders)
    }
    
    fun updateProviderModel(providerId: String, model: String) {
        val updatedProviders = _uiState.value.providers.map { 
            if (it.id == providerId) it.copy(model = model) else it 
        }
        _uiState.value = _uiState.value.copy(providers = updatedProviders)
    }
    
    fun updateApiKey(apiId: String, apiKey: String) {
        val updatedApis = _uiState.value.apis.map { 
            if (it.id == apiId) it.copy(apiKey = apiKey) else it 
        }
        _uiState.value = _uiState.value.copy(apis = updatedApis)
    }
    
    fun saveProviderSettings(provider: ProviderSettings) {
        viewModelScope.launch {
            try {
                val credential = ApiCredentialEntity(
                    provider = provider.id,
                    apiKey = provider.apiKey,
                    isEnabled = provider.isEnabled,
                    status = "saved"
                )
                settingsRepository.insertCredential(credential)
                loadSettings()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun saveApiSettings(api: ApiSettings) {
        viewModelScope.launch {
            try {
                val credential = ApiCredentialEntity(
                    provider = api.id,
                    apiKey = api.apiKey,
                    isEnabled = api.isEnabled,
                    status = "saved"
                )
                settingsRepository.insertCredential(credential)
                loadSettings()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun testProviderConnection(providerId: String) {
        viewModelScope.launch {
            try {
                val provider = _uiState.value.providers.find { it.id == providerId }
                if (provider != null && provider.apiKey.isNotBlank()) {
                    val config = AIProviderConfig(
                        id = provider.id,
                        name = provider.name,
                        isEnabled = provider.isEnabled,
                        apiKey = provider.apiKey,
                        model = provider.model
                    )
                    val adapter = com.cyberfusion.core.ai.provider.AIProviderFactory().create(config)
                    val isValid = adapter.validateKey()
                    val updatedProviders = _uiState.value.providers.map {
                        if (it.id == providerId) it.copy(status = if (isValid) "Connected" else "Invalid Key") else it
                    }
                    _uiState.value = _uiState.value.copy(providers = updatedProviders)
                }
            } catch (e: Exception) {
                val updatedProviders = _uiState.value.providers.map {
                    if (it.id == providerId) it.copy(status = "Error") else it
                }
                _uiState.value = _uiState.value.copy(providers = updatedProviders, error = e.message)
            }
        }
    }
    
    fun testApiConnection(apiId: String) {
        viewModelScope.launch {
            try {
                val api = _uiState.value.apis.find { it.id == apiId }
                if (api != null && api.apiKey.isNotBlank()) {
                    val updatedApis = _uiState.value.apis.map {
                        if (it.id == apiId) it.copy(status = "Connected") else it
                    }
                    _uiState.value = _uiState.value.copy(apis = updatedApis)
                }
            } catch (e: Exception) {
                val updatedApis = _uiState.value.apis.map {
                    if (it.id == apiId) it.copy(status = "Error") else it
                }
                _uiState.value = _uiState.value.copy(apis = updatedApis, error = e.message)
            }
        }
    }
    
    private fun maskKey(key: String): String {
        if (key.length <= 8) return "••••••••"
        return key.take(4) + "••••••••" + key.takeLast(4)
    }
}
