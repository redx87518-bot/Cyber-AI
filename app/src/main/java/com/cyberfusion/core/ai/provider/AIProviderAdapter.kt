package com.cyberfusion.core.ai.provider

import com.cyberfusion.core.network.client.CyberFusionHttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

interface AIProviderAdapter {
    suspend fun chat(messages: List<Message>, tools: List<AITool>? = null): Result<String>
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
    val content: String? = null,
    @SerialName("tool_calls")
    val toolCalls: List<ToolCall>? = null,
    @SerialName("tool_call_id")
    val toolCallId: String? = null,
    val name: String? = null
)

@Serializable
data class Choice(
    val index: Int? = null,
    val message: Message? = null,
    @SerialName("finish_reason")
    val finishReason: String? = null
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
    val parameters: JsonObject
)

@Serializable
data class ToolCall(
    val id: String,
    val type: String = "function",
    val function: ToolCallFunction
)

@Serializable
data class ToolCallFunction(
    val name: String,
    val arguments: String
)

class OpenRouterAdapter(private val config: AIProviderConfig) : AIProviderAdapter {
    private val client = CyberFusionHttpClient.client
    private val baseUrl = config.baseUrl ?: "https://openrouter.ai/api/v1"
    
    override suspend fun chat(messages: List<Message>, tools: List<AITool>?): Result<String> {
        return try {
            val toolObjects = tools?.map { 
                Tool(
                    function = ToolFunction(
                        name = it.name,
                        description = it.description,
                        parameters = JsonObject(
                            mapOf(
                                "type" to JsonPrimitive("object"),
                                "properties" to JsonObject(
                                    it.parameters.mapValues { (_, v) -> JsonObject(mapOf("type" to JsonPrimitive("string"), "description" to JsonPrimitive(v))) }
                                ),
                                "required" to JsonArray(it.parameters.keys.map { JsonPrimitive(it) })
                            )
                        )
                    )
                )
            }
            
            val request = OpenRouterRequest(
                model = config.model,
                messages = messages,
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
                val choice = response.choices?.firstOrNull()
                val message = choice?.message
                val finishReason = choice?.finishReason
                
                if (message == null) {
                    return Result.failure(Exception("Empty response from OpenRouter"))
                }
                
                if (message.toolCalls != null && finishReason == "tool_calls") {
                    val toolResults = message.toolCalls.joinToString("\n") { toolCall ->
                        val args = try {
                            Json.parseToJsonElement(toolCall.function.arguments).jsonObject
                        } catch (e: Exception) {
                            JsonObject(emptyMap())
                        }
                        val argsStr = args.entries.joinToString(", ") { "${it.key}=${it.value.jsonPrimitive.contentOrNull ?: ""}" }
                        "Tool: ${toolCall.function.name}($argsStr)"
                    }
                    Result.success("[TOOL_CALLS:$toolResults]")
                } else {
                    val content = message.content
                    if (content != null) {
                        Result.success(content)
                    } else {
                        Result.failure(Exception("Empty response from OpenRouter"))
                    }
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
    
    override suspend fun chat(messages: List<Message>, tools: List<AITool>?): Result<String> {
        return try {
            val request = GroqRequest(
                model = config.model,
                messages = messages
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
    
    override suspend fun chat(messages: List<Message>, tools: List<AITool>?): Result<String> {
        return try {
            val request = OpenAIRequest(
                model = config.model,
                messages = messages
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
    
    override suspend fun chat(messages: List<Message>, tools: List<AITool>?): Result<String> {
        return try {
            val request = GeminiRequest(
                contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = messages.lastOrNull()?.content ?: "")))),
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
