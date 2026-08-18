package com.cyberfusion.core.network.api

import com.cyberfusion.core.network.client.CyberFusionHttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class NvdAdapter {
    private val client = CyberFusionHttpClient.client
    private val baseUrl = "https://services.nvd.nist.gov/rest/json/cves/2.0"

    suspend fun getCve(cveId: String): ApiResult<NvdResponse> {
        return try {
            val response = client.get(baseUrl) {
                parameter("cveId", cveId)
            }.body<NvdResponse>()
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error("NVD request failed: ${e.message}", e)
        }
    }
}

data class NvdResponse(
    val resultsPerPage: Int,
    val startIndex: Int,
    val totalResults: Int,
    val vulnerabilities: List<NvdVulnerability>?
)

data class NvdVulnerability(
    val cve: NvdCve?
)

data class NvdCve(
    val id: String,
    val sourceIdentifier: String?,
    val published: String?,
    val lastModified: String?,
    val descriptions: List<NvdDescription>?,
    val metrics: NvdMetrics?
)

data class NvdDescription(
    val lang: String,
    val value: String
)

data class NvdMetrics(
    val cvssMetricV31: List<NvdCvss>?
)

data class NvdCvss(
    val cvssData: NvdCvssData?
)

data class NvdCvssData(
    val baseScore: Double?,
    val baseSeverity: String?
)