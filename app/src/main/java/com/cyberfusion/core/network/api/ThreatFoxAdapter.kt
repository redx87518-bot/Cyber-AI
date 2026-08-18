package com.cyberfusion.core.network.api

import com.cyberfusion.core.network.client.ApiResult
import com.cyberfusion.core.network.client.CyberFusionHttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.put

class ThreatFoxAdapter {
    private val client = CyberFusionHttpClient.client
    private val baseUrl = "https://threatfox.abuse.ch/api/v1"

    suspend fun searchIoc(ioc: String, iocType: String? = null): ApiResult<ThreatFoxResponse> {
        return try {
            val payload = buildJsonPayload(ioc, iocType)
            val response = client.post("$baseUrl/") {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }.body<ThreatFoxResponse>()
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error("ThreatFox request failed: ${e.message}", e)
        }
    }

    private fun buildJsonPayload(ioc: String, iocType: String?): JsonObject {
        return JsonObject(mapOf(
            "query" to JsonPrimitive("search_ioc"),
            "ioc" to JsonPrimitive(ioc),
            "ioc_type" to JsonPrimitive(iocType ?: "")
        ))
    }
}

data class ThreatFoxResponse(
    val query_status: String,
    val data: List<ThreatFoxIoc>?
)

data class ThreatFoxIoc(
    val id: String,
    val ioc: String,
    val ioc_type: String,
    val threat_type: String,
    val malware: String,
    val malware_alias: String?,
    val malware_printable: String?,
    val first_seen: String?,
    val last_seen: String?,
    val confidence: Int,
    val tags: List<String>?,
    val reference: String?
)