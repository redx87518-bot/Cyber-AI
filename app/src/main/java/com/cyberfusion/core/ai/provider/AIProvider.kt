package com.cyberfusion.core.ai.provider

sealed interface AIProvider {
    val id: String
    val name: String
    val isEnabled: Boolean
}

data class AIProviderConfig(
    override val id: String,
    override val name: String,
    override val isEnabled: Boolean,
    val apiKey: String,
    val model: String,
    val baseUrl: String? = null,
    val isPrimary: Boolean = false,
    val isFallback: Boolean = false
) : AIProvider

data class AIModel(
    val id: String,
    val name: String,
    val providerId: String,
    val maxTokens: Int = 4096,
    val supportsTools: Boolean = true
)

data class AITask(
    val id: Long,
    val prompt: String,
    val provider: AIProvider?,
    val model: AIModel?,
    val status: String,
    val result: String? = null,
    val error: String? = null
)

data class AITool(
    val name: String,
    val description: String,
    val parameters: Map<String, String>
)

data class AIToolResult(
    val toolName: String,
    val success: Boolean,
    val result: String,
    val error: String? = null
)