package com.cyberfusion.core.ai.provider

import com.cyberfusion.core.network.client.CyberFusionHttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

@Serializable
data class OpenRouterRequest(
    val model: String,
    val messages: List<Message>,
    val tools: List<Tool>? = null,
    val max_tokens: Int = 4096
)

@Serializable
data class OpenRouterResponse(
    val choices: List<Choice>? = null,
    val error: OpenRouterError? = null
)

@Serializable
data class OpenRouterError(
    val message: String? = null
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class Choice(
    val message: Message? = null
)

@Serializable
data class Tool(
    val type: String = "function",
    val function: ToolFunction
)

@Serializable
data class ToolFunction(
    val name: String,
    val description: String,
    val parameters: Map<String, String>
)

class OpenRouterAdapter(private val config: AIProviderConfig) : AIProviderAdapter {
    private val client = CyberFusionHttpClient.client
    private val baseUrl = config.baseUrl ?: "https://openrouter.ai/api/v1"
    
    override suspend fun chat(prompt: String, tools: List<AITool>?): Result<String> {
        return try {
            val toolObjects = tools?.map { 
                Tool(
                    function = ToolFunction(
                        name = it.name,
                        description = it.description,
                        parameters = it.parameters
                    )
                )
            }
            
            val request = OpenRouterRequest(
                model = config.model,
                messages = listOf(Message(role = "user", content = prompt)),
                tools = toolObjects
            )
            
            val response = client.post("$baseUrl/chat/completions") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer ${config.apiKey}")
                header("HTTP-Referer", "https://cyberfusion.app")
                header("X-Title", "CyberFusion")
                setBody(request)
            }.body<OpenRouterResponse>()
            
            if (response.error != null) {
                Result.failure(Exception("OpenRouter error: ${response.error.message}"))
            } else {
                val content = response.choices?.firstOrNull()?.message?.content
                if (content != null) {
                    Result.success(content)
                } else {
                    Result.failure(Exception("Empty response from OpenRouter"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("OpenRouter request failed: ${e.message}", e))
        }
    }
    
    override suspend fun validateKey(): Boolean {
        return try {
            val response = client.get("$baseUrl/models") {
                header("Authorization", "Bearer ${config.apiKey}")
            }.body<OpenRouterResponse>()
            response.error == null
        } catch (e: Exception) {
            false
        }
    }
}

@Serializable
data class GroqRequest(
    val model: String,
    val messages: List<Message>,
    val max_tokens: Int = 4096
)

@Serializable
data class GroqResponse(
    val choices: List<Choice>? = null,
    val error: GroqError? = null
)

@Serializable
data class GroqError(
    val message: String? = null
)

class GroqAdapter(private val config: AIProviderConfig) : AIProviderAdapter {
    private val client = CyberFusionHttpClient.client
    private val baseUrl = config.baseUrl ?: "https://api.groq.com/openai/v1"
    
    override suspend fun chat(prompt: String, tools: List<AITool>?): Result<String> {
        return try {
            val request = GroqRequest(
                model = config.model,
                messages = listOf(Message(role = "user", content = prompt))
            )
            
            val response = client.post("$baseUrl/chat/completions") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer ${config.apiKey}")
                setBody(request)
            }.body<GroqResponse>()
            
            if (response.error != null) {
                Result.failure(Exception("Groq error: ${response.error.message}"))
            } else {
                val content = response.choices?.firstOrNull()?.message?.content
                if (content != null) {
                    Result.success(content)
                } else {
                    Result.failure(Exception("Empty response from Groq"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Groq request failed: ${e.message}", e))
        }
    }
    
    override suspend fun validateKey(): Boolean {
        return try {
            val response = client.get("$baseUrl/models") {
                header("Authorization", "Bearer ${config.apiKey}")
            }.body<GroqResponse>()
            response.error == null
        } catch (e: Exception) {
            false
        }
    }
}

@Serializable
data class OpenAIRequest(
    val model: String,
    val messages: List<Message>,
    val max_tokens: Int = 4096
)

@Serializable
data class OpenAIResponse(
    val choices: List<Choice>? = null,
    val error: OpenAIError? = null
)

@Serializable
data class OpenAIError(
    val message: String? = null
)

class OpenAIAdapter(private val config: AIProviderConfig) : AIProviderAdapter {
    private val client = CyberFusionHttpClient.client
    private val baseUrl = config.baseUrl ?: "https://api.openai.com/v1"
    
    override suspend fun chat(prompt: String, tools: List<AITool>?): Result<String> {
        return try {
            val request = OpenAIRequest(
                model = config.model,
                messages = listOf(Message(role = "user", content = prompt))
            )
            
            val response = client.post("$baseUrl/chat/completions") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer ${config.apiKey}")
                setBody(request)
            }.body<OpenAIResponse>()
            
            if (response.error != null) {
                Result.failure(Exception("OpenAI error: ${response.error.message}"))
            } else {
                val content = response.choices?.firstOrNull()?.message?.content
                if (content != null) {
                    Result.success(content)
                } else {
                    Result.failure(Exception("Empty response from OpenAI"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("OpenAI request failed: ${e.message}", e))
        }
    }
    
    override suspend fun validateKey(): Boolean {
        return try {
            val response = client.get("$baseUrl/models") {
                header("Authorization", "Bearer ${config.apiKey}")
            }.body<OpenAIResponse>()
            response.error == null
        } catch (e: Exception) {
            false
        }
    }
}

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiConfig? = null
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String
)

@Serializable
data class GeminiConfig(
    val temperature: Double = 0.7,
    val maxOutputTokens: Int = 4096
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null,
    val error: GeminiError? = null
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent? = null
)

@Serializable
data class GeminiError(
    val message: String? = null
)

class GeminiAdapter(private val config: AIProviderConfig) : AIProviderAdapter {
    private val client = CyberFusionHttpClient.client
    private val baseUrl = config.baseUrl ?: "https://generativelanguage.googleapis.com/v1beta"
    
    override suspend fun chat(prompt: String, tools: List<AITool>?): Result<String> {
        return try {
            val request = GeminiRequest(
                contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
                generationConfig = GeminiConfig()
            )
            
            val response = client.post("$baseUrl/models/${config.model}:generateContent?key=${config.apiKey}") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<GeminiResponse>()
            
            if (response.error != null) {
                Result.failure(Exception("Gemini error: ${response.error.message}"))
            } else {
                val content = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (content != null) {
                    Result.success(content)
                } else {
                    Result.failure(Exception("Empty response from Gemini"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gemini request failed: ${e.message}", e))
        }
    }
    
    override suspend fun validateKey(): Boolean {
        return try {
            val response = client.get("$baseUrl/models?key=${config.apiKey}").body<GeminiResponse>()
            response.error == null
        } catch (e: Exception) {
            false
        }
    }
}