package com.cyberfusion.ui.features.threatintel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberfusion.core.database.room.entity.ApiCredentialEntity
import com.cyberfusion.core.database.room.repository.SettingsRepository
import com.cyberfusion.core.network.api.AbuseIPDBAdapter
import com.cyberfusion.core.network.api.MalwareBazaarAdapter
import com.cyberfusion.core.network.api.NvdAdapter
import com.cyberfusion.core.network.api.ThreatFoxAdapter
import com.cyberfusion.core.network.client.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ThreatIntelResult(
    val ioc: String,
    val type: String,
    val reputation: String,
    val confidence: Int,
    val source: String,
    val details: String? = null
)

data class ThreatIntelUiState(
    val query: String = "",
    val results: List<ThreatIntelResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ThreatIntelViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ThreatIntelUiState())
    val uiState: StateFlow<ThreatIntelUiState> = _uiState.asStateFlow()

    private var abuseIpdbKey: String = ""
    private var threatfoxKey: String = ""
    private var malwarebazaarKey: String = ""
    private var nvdKey: String = ""

    init {
        loadCredentials()
    }

    private fun loadCredentials() {
        viewModelScope.launch {
            try {
                val credentials = settingsRepository.allCredentials.first()
                abuseIpdbKey = credentials.find { it.provider == "abuseipdb" }?.apiKey ?: ""
                threatfoxKey = credentials.find { it.provider == "threatfox" }?.apiKey ?: ""
                malwarebazaarKey = credentials.find { it.provider == "malwarebazaar" }?.apiKey ?: ""
                nvdKey = credentials.find { it.provider == "nvd" }?.apiKey ?: ""
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun search() {
        val query = _uiState.value.query.trim()
        if (query.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val results = mutableListOf<ThreatIntelResult>()

            try {
                if (isIpAddress(query) && abuseIpdbKey.isNotBlank()) {
                    val adapter = AbuseIPDBAdapter(abuseIpdbKey)
                    when (val result = adapter.checkIp(query)) {
                        is ApiResult.Success -> {
                            val data = result.data.data
                            if (data != null) {
                                results.add(
                                    ThreatIntelResult(
                                        ioc = query,
                                        type = "IP",
                                        reputation = if (data.abuseConfidenceScore > 50) "Malicious" else "Clean",
                                        confidence = data.abuseConfidenceScore,
                                        source = "AbuseIPDB",
                                        details = "ISP: ${data.isp ?: "N/A"}, Country: ${data.countryCode ?: "N/A"}, Reports: ${data.totalReports}"
                                    )
                                )
                            }
                        }
                        is ApiResult.Error -> {
                            _uiState.value = _uiState.value.copy(error = "AbuseIPDB: ${result.message}")
                        }
                        else -> {
                            _uiState.value = _uiState.value.copy(error = "AbuseIPDB: Unexpected response")
                        }
                    }
                }

                if (threatfoxKey.isNotBlank()) {
                    val adapter = ThreatFoxAdapter()
                    when (val result = adapter.searchIoc(query)) {
                        is ApiResult.Success -> {
                            val iocs = result.data.data
                            if (!iocs.isNullOrEmpty()) {
                                iocs.take(5).forEach { ioc ->
                                    results.add(
                                        ThreatIntelResult(
                                            ioc = ioc.ioc,
                                            type = ioc.ioc_type,
                                            reputation = "Malicious",
                                            confidence = ioc.confidence,
                                            source = "ThreatFox",
                                            details = "Malware: ${ioc.malware}, Threat: ${ioc.threat_type}"
                                        )
                                    )
                                }
                            }
                        }
                        is ApiResult.Error -> {
                            _uiState.value = _uiState.value.copy(error = "ThreatFox: ${result.message}")
                        }
                        else -> {
                            _uiState.value = _uiState.value.copy(error = "ThreatFox: Unexpected response")
                        }
                    }
                }

                if (malwarebazaarKey.isNotBlank() && query.length in 32..64) {
                    val adapter = MalwareBazaarAdapter()
                    when (val result = adapter.queryHash(query)) {
                        is ApiResult.Success -> {
                            val samples = result.data.data
                            if (!samples.isNullOrEmpty()) {
                                samples.take(3).forEach { sample ->
                                    results.add(
                                        ThreatIntelResult(
                                            ioc = sample.sha256_hash,
                                            type = "Hash",
                                            reputation = "Malicious",
                                            confidence = 100,
                                            source = "MalwareBazaar",
                                            details = "File: ${sample.file_name ?: "N/A"}, Type: ${sample.file_type ?: "N/A"}, Signature: ${sample.signature ?: "N/A"}"
                                        )
                                    )
                                }
                            }
                        }
                        is ApiResult.Error -> {
                            _uiState.value = _uiState.value.copy(error = "MalwareBazaar: ${result.message}")
                        }
                        else -> {
                            _uiState.value = _uiState.value.copy(error = "MalwareBazaar: Unexpected response")
                        }
                    }
                }

                if (query.startsWith("CVE-") && nvdKey.isNotBlank()) {
                    val adapter = NvdAdapter()
                    when (val result = adapter.getCve(query)) {
                        is ApiResult.Success -> {
                            val vuln = result.data.vulnerabilities?.firstOrNull()?.cve
                            if (vuln != null) {
                                val severity = vuln.metrics?.cvssMetricV31?.firstOrNull()?.cvssData?.baseSeverity
                                val score = vuln.metrics?.cvssMetricV31?.firstOrNull()?.cvssData?.baseScore
                                results.add(
                                    ThreatIntelResult(
                                        ioc = vuln.id,
                                        type = "CVE",
                                        reputation = severity ?: "Unknown",
                                        confidence = (score?.times(10)?.toInt() ?: 0).coerceIn(0, 100),
                                        source = "NVD",
                                        details = vuln.descriptions?.firstOrNull()?.value ?: "No description"
                                    )
                                )
                            }
                        }
                        is ApiResult.Error -> {
                            _uiState.value = _uiState.value.copy(error = "NVD: ${result.message}")
                        }
                        else -> {
                            _uiState.value = _uiState.value.copy(error = "NVD: Unexpected response")
                        }
                    }
                }

                if (results.isEmpty() && _uiState.value.error == null) {
                    results.add(
                        ThreatIntelResult(
                            ioc = query,
                            type = "Unknown",
                            reputation = "No results",
                            confidence = 0,
                            source = "Local"
                        )
                    )
                }

                _uiState.value = _uiState.value.copy(results = results, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Search failed: ${e.message}")
            }
        }
    }

    private fun isIpAddress(input: String): Boolean {
        val ipRegex = """^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$""".toRegex()
        return ipRegex.matches(input)
    }
}
