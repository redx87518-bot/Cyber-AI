package com.cyberfusion.core.network.api

import com.cyberfusion.core.network.client.ApiResult
import com.cyberfusion.core.network.client.CyberFusionHttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AbuseIPDBAdapter(private val apiKey: String) {
    private val client = CyberFusionHttpClient.client
    private val baseUrl = "https://api.abuseipdb.com/api/v2"

    suspend fun checkIp(ip: String): ApiResult<AbuseIPDBResponse> {
        return try {
            val response = client.get("$baseUrl/check") {
                parameter("ipAddress", ip)
                parameter("maxAgeInDays", "90")
                parameter("verbose", "")
                header("Key", apiKey)
                header("Accept", "application/json")
            }.body<AbuseIPDBResponse>()
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error("AbuseIPDB request failed: ${e.message}", e)
        }
    }
}

data class AbuseIPDBResponse(
    val data: AbuseIPDBData?
)

data class AbuseIPDBData(
    val ipAddress: String,
    val abuseConfidenceScore: Int,
    val countryCode: String?,
    val usageType: String?,
    val isp: String?,
    val domain: String?,
    val totalReports: Int,
    val isPublic: Boolean,
    val isWhitelisted: Boolean?,
    val lastReportedAt: String?
)