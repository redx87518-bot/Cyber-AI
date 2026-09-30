package com.cyberfusion.core.ai.provider

import com.cyberfusion.core.network.client.CyberFusionHttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

/**
 * Rax AI adapter (https://www.ai.raxcore.dev).
 *
 * Rax AI exposes a fully OpenAI-compatible REST API:
 *   Base URL: https://ai.raxcore.dev/api/v1
 *   Auth:     Authorization: Bearer rax_your_api_key
 *   Models:   rax-4.0 (fast workhorse), rax-4.5 (262K context deep thinker)
 *
 * When [AIProviderConfig.baseUrl] is set (advanced/self-host deployments) it is
 * used verbatim; otherwise the official endpoint above applies.
 */
class RaxAIAdapter(private val config: AIProviderConfig) : AIProviderAdapter {
    private val client = CyberFusionHttpClient.client
    private val baseUrl = config.baseUrl?.trimEnd('/') ?: DEFAULT_BASE_URL

    override suspend fun chat(messages: List<Message>, tools: List<AITool>?): Result<String> {
        return try {
            if (config.apiKey.isBlank()) {
                return Result.failure(
                    Exception("Rax AI API key not configured. Add a rax_... key in Settings → Rax AI.")
                )
            }

            val request = RaxChatRequest(
                model = config.model.ifBlank { MODEL_RAX_40 },
                messages = messages,
                max_tokens = 4096,
                temperature = 0.4
            )

            val response = client.post("$baseUrl/chat/completions") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer ${config.apiKey}")
                setBody(request)
            }.body<RaxChatResponse>()

            val error = response.error
            if (error != null) {
                return Result.failure(Exception(error.message ?: "Unknown Rax AI error"))
            }

            val content = response.choices?.firstOrNull()?.message?.content
            if (content.isNullOrBlank()) {
                Result.failure(Exception("Empty response from Rax AI"))
            } else {
                Result.success(content)
            }
        } catch (e: Exception) {
            val msg = e.message ?: "Unknown error"
            Result.failure(
                when {
                    msg.contains("401", true) || msg.contains("Unauthorized", true) || msg.contains("invalid_api_key", true) ->
                        Exception("Rax AI authentication failed. Check your rax_ API key in Settings.")
                    msg.contains("429", true) || msg.contains("rate limit", true) ->
                        Exception("Rax AI rate limit reached (free tier: 60 req/min). Wait a moment and retry.")
                    msg.contains("Unable to resolve host", true) || msg.contains("Network is unreachable", true) ->
                        Exception("Network unavailable. Check your internet connection.")
                    msg.contains("timeout", true) || msg.contains("timed out", true) ->
                        Exception("Rax AI request timed out. Try again.")
                    else -> Exception("Rax AI request failed: $msg")
                }
            )
        }
    }

    override suspend fun validateKey(): Boolean {
        return try {
            val response = client.get("$baseUrl/models") {
                header("Authorization", "Bearer ${config.apiKey}")
            }.body<RaxChatResponse>()
            response.error == null
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        const val DEFAULT_BASE_URL = "https://ai.raxcore.dev/api/v1"
        const val MODEL_RAX_40 = "rax-4.0"
        const val MODEL_RAX_45 = "rax-4.5"
    }
}

@Serializable
data class RaxChatRequest(
    val model: String,
    val messages: List<Message>,
    val max_tokens: Int = 4096,
    val temperature: Double = 0.4
)

@Serializable
data class RaxChatResponse(
    val choices: List<Choice>? = null,
    val error: RaxError? = null
)

@Serializable
data class RaxError(
    val message: String? = null,
    val code: String? = null
)
