package com.cyberfusion.core.ai.provider

interface AIProviderAdapter {
    suspend fun chat(prompt: String, tools: List<AITool>? = null): Result<String>
    suspend fun validateKey(): Boolean
}

class AIProviderFactory {
    fun create(config: AIProviderConfig): AIProviderAdapter {
        return when (config.id) {
            "openrouter" -> OpenRouterAdapter(config)
            "groq" -> GroqAdapter(config)
            "gemini" -> GeminiAdapter(config)
            "openai" -> OpenAIAdapter(config)
            else -> throw IllegalArgumentException("Unknown provider: ${config.id}")
        }
    }
}

class OpenRouterAdapter(private val config: AIProviderConfig) : AIProviderAdapter {
    override suspend fun chat(prompt: String, tools: List<AITool>?): Result<String> {
        return Result.failure(Exception("OpenRouter adapter not implemented"))
    }
    override suspend fun validateKey(): Boolean = false
}

class GroqAdapter(private val config: AIProviderConfig) : AIProviderAdapter {
    override suspend fun chat(prompt: String, tools: List<AITool>?): Result<String> {
        return Result.failure(Exception("Groq adapter not implemented"))
    }
    override suspend fun validateKey(): Boolean = false
}

class GeminiAdapter(private val config: AIProviderConfig) : AIProviderAdapter {
    override suspend fun chat(prompt: String, tools: List<AITool>?): Result<String> {
        return Result.failure(Exception("Gemini adapter not implemented"))
    }
    override suspend fun validateKey(): Boolean = false
}

class OpenAIAdapter(private val config: AIProviderConfig) : AIProviderAdapter {
    override suspend fun chat(prompt: String, tools: List<AITool>?): Result<String> {
        return Result.failure(Exception("OpenAI adapter not implemented"))
    }
    override suspend fun validateKey(): Boolean = false
}