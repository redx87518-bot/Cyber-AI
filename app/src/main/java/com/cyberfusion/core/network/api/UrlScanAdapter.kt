package com.cyberfusion.core.network.api

import com.cyberfusion.core.network.client.ApiResult
import com.cyberfusion.core.network.client.CyberFusionHttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class UrlScanAdapter(private val apiKey: String) {
    private val client = CyberFusionHttpClient.client
    private val baseUrl = "https://urlscan.io/api/v1"

    suspend fun search(query: String): ApiResult<UrlScanSearchResponse> {
        return try {
            val response = client.get("$baseUrl/search/") {
                parameter("q", query)
                header("API-Key", apiKey)
            }.body<UrlScanSearchResponse>()
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error("URLScan search failed: ${e.message}", e)
        }
    }

    suspend fun getResult(scanId: String): ApiResult<UrlScanResult> {
        return try {
            val response = client.get("$baseUrl/result/$scanId/") {
                header("API-Key", apiKey)
            }.body<UrlScanResult>()
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error("URLScan result fetch failed: ${e.message}", e)
        }
    }
}

data class UrlScanSearchResponse(
    val results: List<UrlScanResult>?
)

data class UrlScanResult(
    val task: UrlScanTask?,
    val page: UrlScanPage?,
    val verdicts: UrlScanVerdicts?
)

data class UrlScanTask(
    val uuid: String?,
    val url: String?,
    val time: String?,
    val score: Int?
)

data class UrlScanPage(
    val url: String?,
    val domain: String?,
    val country: String?
)

data class UrlScanVerdicts(
    val urlscan: UrlScanVerdict?,
    val engines: Int?
)

data class UrlScanVerdict(
    val score: Int?,
    val categories: List<String>?
)
