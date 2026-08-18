package com.cyberfusion.core.network.api

import com.cyberfusion.core.network.client.ApiResult
import com.cyberfusion.core.network.client.CyberFusionHttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class OtxxAdapter(private val apiKey: String) {
    private val client = CyberFusionHttpClient.client
    private val baseUrl = "https://otx.alienvault.com/api/v1"

    suspend fun getIndicatorGeneral(type: String, value: String): ApiResult<OtxxGeneralResponse> {
        return try {
            val response = client.get("$baseUrl/indicators/$type/$value/general") {
                header("X-OTX-API-Key", apiKey)
            }.body<OtxxGeneralResponse>()
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error("OTX request failed: ${e.message}", e)
        }
    }

    suspend fun getIndicatorMalware(type: String, value: String): ApiResult<OtxxMalwareResponse> {
        return try {
            val response = client.get("$baseUrl/indicators/$type/$value/malware") {
                header("X-OTX-API-Key", apiKey)
            }.body<OtxxMalwareResponse>()
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error("OTX malware request failed: ${e.message}", e)
        }
    }

    suspend fun getPulses(type: String, value: String): ApiResult<OtxxPulsesResponse> {
        return try {
            val response = client.get("$baseUrl/indicators/$type/$value/pulses") {
                header("X-OTX-API-Key", apiKey)
            }.body<OtxxPulsesResponse>()
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error("OTX pulses request failed: ${e.message}", e)
        }
    }
}

data class OtxxGeneralResponse(
    val indicator: String,
    val type: String,
    val id: String,
    val reputation: Int,
    val pulse_info: OtxxPulseInfo?
)

data class OtxxPulseInfo(
    val count: Int,
    val pulses: List<OtxxPulse>?
)

data class OtxxPulse(
    val id: String,
    val name: String,
    val description: String?,
    val modified: String?
)

data class OtxxMalwareResponse(
    val count: Int,
    val data: List<OtxxMalwareSample>?
)

data class OtxxMalwareSample(
    val hash: String?,
    val datetime: String?
)

data class OtxxPulsesResponse(
    val count: Int,
    val pulses: List<OtxxPulse>?
)
