package com.cyberfusion.core.ai.tools

import com.cyberfusion.core.ai.provider.AITool
import com.cyberfusion.core.ai.provider.AIToolResult
import com.cyberfusion.core.database.room.entity.AlertEntity
import com.cyberfusion.core.database.room.entity.IncidentEntity
import com.cyberfusion.core.database.room.entity.InvestigationEntity
import com.cyberfusion.core.database.room.entity.IocEntity
import com.cyberfusion.core.database.room.entity.LabEntity
import com.cyberfusion.core.database.room.entity.RiskEntity
import com.cyberfusion.core.database.room.entity.ReportEntity
import com.cyberfusion.core.database.room.repository.AlertRepository
import com.cyberfusion.core.database.room.repository.GRCRepository
import com.cyberfusion.core.database.room.repository.IncidentRepository
import com.cyberfusion.core.database.room.repository.InvestigationRepository
import com.cyberfusion.core.database.room.repository.IocRepository
import com.cyberfusion.core.database.room.repository.LabsRepository
import com.cyberfusion.core.database.room.repository.ReportRepository
import com.cyberfusion.core.database.room.repository.AiRepository
import com.cyberfusion.core.database.room.repository.SettingsRepository
import com.cyberfusion.core.database.room.repository.ThreatIntelRepository
import com.cyberfusion.core.labs.LabContent
import com.cyberfusion.core.labs.LabsContent
import com.cyberfusion.core.network.api.AbuseIPDBAdapter
import com.cyberfusion.core.network.api.MalwareBazaarAdapter
import com.cyberfusion.core.network.api.NvdAdapter
import com.cyberfusion.core.network.api.OtxxAdapter
import com.cyberfusion.core.network.api.ThreatFoxAdapter
import com.cyberfusion.core.network.api.UrlScanAdapter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object AIToolRegistry {
    val tools: List<AITool> = listOf(
        AITool("getAlerts", "Get alerts from the database", mapOf("status" to "String?")),
        AITool("getAlert", "Get a specific alert", mapOf("id" to "Long")),
        AITool("searchIOC", "Search for an IOC in local database", mapOf("value" to "String", "type" to "String?")),
        AITool("enrichIOC", "Enrich an IOC with threat intelligence APIs", mapOf("iocValue" to "String", "iocType" to "String?")),
        AITool("getInvestigations", "Get all investigations", mapOf("status" to "String?")),
        AITool("createInvestigation", "Create a new investigation", mapOf("title" to "String", "description" to "String")),
        AITool("addInvestigationNote", "Add a note to an investigation", mapOf("investigationId" to "Long", "content" to "String")),
        AITool("getIncidents", "Get all incidents", mapOf("status" to "String?")),
        AITool("getLabs", "Get available cybersecurity labs", emptyMap()),
        AITool("getLab", "Get a specific lab by ID", mapOf("labId" to "Long")),
        AITool("getLabProgress", "Get lab progress by ID", mapOf("labId" to "Long")),
        AITool("submitLabAnswer", "Submit lab answer and get score", mapOf("labId" to "Long", "answers" to "String")),
        AITool("getRisks", "Get all GRC risks", mapOf("status" to "String?")),
        AITool("createRisk", "Create a new GRC risk", mapOf("title" to "String", "severity" to "String", "description" to "String")),
        AITool("getControls", "Get all GRC controls", emptyMap()),
        AITool("generateReport", "Generate a report from data", mapOf("reportType" to "String")),
        AITool("saveReport", "Save a report to database", mapOf("title" to "String", "content" to "String")),
        AITool("queryMalwareBazaar", "Query MalwareBazaar for a hash", mapOf("hash" to "String")),
        AITool("checkAbuseIPDB", "Check IP reputation on AbuseIPDB", mapOf("ip" to "String")),
        AITool("searchThreatFox", "Search ThreatFox for IOC", mapOf("ioc" to "String", "iocType" to "String?")),
        AITool("getNvdCve", "Get CVE details from NVD", mapOf("cveId" to "String")),
        AITool("getOtxIntel", "Get OTX threat intelligence for indicator", mapOf("type" to "String", "value" to "String")),
        AITool("scanUrl", "Scan URL with URLScan.io", mapOf("query" to "String")),
        AITool("getSettings", "Get current settings and API status", emptyMap())
    )

    suspend fun executeTool(
        toolName: String,
        parameters: Map<String, String>,
        repositories: ToolRepositories
    ): AIToolResult {
        return try {
            when (toolName) {
                "getAlerts" -> executeGetAlerts(parameters, repositories)
                "getAlert" -> executeGetAlert(parameters, repositories)
                "searchIOC" -> executeSearchIOC(parameters, repositories)
                "enrichIOC" -> executeEnrichIOC(parameters, repositories)
                "getInvestigations" -> executeGetInvestigations(parameters, repositories)
                "createInvestigation" -> executeCreateInvestigation(parameters, repositories)
                "addInvestigationNote" -> executeAddInvestigationNote(parameters, repositories)
                "getIncidents" -> executeGetIncidents(parameters, repositories)
                "getLabs" -> executeGetLabs(repositories)
                "getLab" -> executeGetLab(parameters, repositories)
                "getLabProgress" -> executeGetLabProgress(parameters, repositories)
                "submitLabAnswer" -> executeSubmitLabAnswer(parameters, repositories)
                "getRisks" -> executeGetRisks(parameters, repositories)
                "createRisk" -> executeCreateRisk(parameters, repositories)
                "getControls" -> executeGetControls(repositories)
                "generateReport" -> executeGenerateReport(parameters, repositories)
                "saveReport" -> executeSaveReport(parameters, repositories)
                "queryMalwareBazaar" -> executeQueryMalwareBazaar(parameters, repositories)
                "checkAbuseIPDB" -> executeCheckAbuseIPDB(parameters, repositories)
                "searchThreatFox" -> executeSearchThreatFox(parameters, repositories)
                "getNvdCve" -> executeGetNvdCve(parameters, repositories)
                "getOtxIntel" -> executeGetOtxIntel(parameters, repositories)
                "scanUrl" -> executeScanUrl(parameters, repositories)
                "getSettings" -> executeGetSettings(repositories)
                else -> AIToolResult(toolName, false, "", "Unknown tool: $toolName")
            }
        } catch (e: Exception) {
            AIToolResult(toolName, false, "", "Error: ${e.message}")
        }
    }

    private suspend fun executeGetAlerts(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val status = parameters["status"]
        val alerts = if (status != null) repositories.alertRepository.getByStatus(status).first()
        else repositories.alertRepository.allAlerts.first()
        if (alerts.isEmpty()) return AIToolResult("getAlerts", true, "No alerts found")
        val summary = alerts.joinToString("\n") { alert ->
            "ID=${alert.id} | ${alert.title} | ${alert.severity} | ${alert.status} | ${alert.source}"
        }
        return AIToolResult("getAlerts", true, "Found ${alerts.size} alerts:\n$summary")
    }

    private suspend fun executeGetAlert(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val id = parameters["id"]?.toLongOrNull() ?: return AIToolResult("getAlert", false, "", "Missing or invalid id parameter")
        val alert = repositories.alertRepository.getById(id)
        return if (alert != null) {
            AIToolResult("getAlert", true, "Alert ID=$id\nTitle: ${alert.title}\nSeverity: ${alert.severity}\nStatus: ${alert.status}\nSource: ${alert.source}\nDescription: ${alert.description}")
        } else {
            AIToolResult("getAlert", true, "Alert with ID $id not found")
        }
    }

    private suspend fun executeSearchIOC(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val value = parameters["value"] ?: return AIToolResult("searchIOC", false, "", "Missing value parameter")
        val ioc = repositories.iocRepository.getByValue(value)
        return if (ioc != null) {
            AIToolResult("searchIOC", true, "Found IOC: $value\nType: ${ioc.type}\nReputation: ${ioc.reputation}\nConfidence: ${ioc.confidence}\nSeverity: ${ioc.severity}")
        } else {
            AIToolResult("searchIOC", true, "IOC '$value' not found in local database. Try enrichIOC to query external threat intelligence sources.")
        }
    }

    private suspend fun executeEnrichIOC(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val iocValue = parameters["iocValue"] ?: return AIToolResult("enrichIOC", false, "", "Missing iocValue parameter")
        val iocType = parameters["iocType"] ?: detectIocType(iocValue)
        val results = mutableListOf<String>()
        val credentials = repositories.settingsRepository.allCredentials.first()
        val abuseKey = credentials.find { it.provider == "abuseipdb" }?.apiKey.orEmpty()
        val threatfoxKey = credentials.find { it.provider == "threatfox" }?.apiKey.orEmpty()
        val mbKey = credentials.find { it.provider == "malwarebazaar" }?.apiKey.orEmpty()
        val nvdKey = credentials.find { it.provider == "nvd" }?.apiKey.orEmpty()
        val otxKey = credentials.find { it.provider == "otx" }?.apiKey.orEmpty()
        val urlscanKey = credentials.find { it.provider == "urlscan" }?.apiKey.orEmpty()

        if (iocType.equals("ip", ignoreCase = true) && abuseKey.isNotBlank()) {
            runCatching {
                val adapter = AbuseIPDBAdapter(abuseKey)
                when (val result = adapter.checkIp(iocValue)) {
                    is com.cyberfusion.core.network.client.ApiResult.Success -> {
                        val data = result.data.data
                        if (data != null) {
                            results.add("AbuseIPDB: Score=${data.abuseConfidenceScore}, ISP=${data.isp ?: "N/A"}, Country=${data.countryCode ?: "N/A"}, Reports=${data.totalReports}")
                        }
                    }
                    is com.cyberfusion.core.network.client.ApiResult.Error -> results.add("AbuseIPDB error: ${result.message}")
                    else -> results.add("AbuseIPDB: unexpected response")
                }
            }.onFailure { results.add("AbuseIPDB exception: ${it.message}") }
        }

        if (threatfoxKey.isNotBlank()) {
            runCatching {
                val adapter = ThreatFoxAdapter()
                when (val result = adapter.searchIoc(iocValue, iocType.takeIf { it.isNotBlank() })) {
                    is com.cyberfusion.core.network.client.ApiResult.Success -> {
                        val iocs = result.data.data
                        if (!iocs.isNullOrEmpty()) {
                            results.add("ThreatFox: Found ${iocs.size} IOCs. Top: ${iocs.first().malware} (${iocs.first().threat_type})")
                        } else {
                            results.add("ThreatFox: No results for $iocValue")
                        }
                    }
                    is com.cyberfusion.core.network.client.ApiResult.Error -> results.add("ThreatFox error: ${result.message}")
                    else -> results.add("ThreatFox: unexpected response")
                }
            }.onFailure { results.add("ThreatFox exception: ${it.message}") }
        }

        if (iocValue.length in 32..64 && mbKey.isNotBlank()) {
            runCatching {
                val adapter = MalwareBazaarAdapter()
                when (val result = adapter.queryHash(iocValue)) {
                    is com.cyberfusion.core.network.client.ApiResult.Success -> {
                        val samples = result.data.data
                        if (!samples.isNullOrEmpty()) {
                            val s = samples.first()
                            results.add("MalwareBazaar: File=${s.file_name ?: "N/A"}, Type=${s.file_type ?: "N/A"}, Signature=${s.signature ?: "N/A"}")
                        } else {
                            results.add("MalwareBazaar: No samples for hash $iocValue")
                        }
                    }
                    is com.cyberfusion.core.network.client.ApiResult.Error -> results.add("MalwareBazaar error: ${result.message}")
                    else -> results.add("MalwareBazaar: unexpected response")
                }
            }.onFailure { results.add("MalwareBazaar exception: ${it.message}") }
        }

        if (iocValue.startsWith("CVE-") && nvdKey.isNotBlank()) {
            runCatching {
                val adapter = NvdAdapter()
                when (val result = adapter.getCve(iocValue)) {
                    is com.cyberfusion.core.network.client.ApiResult.Success -> {
                        val vuln = result.data.vulnerabilities?.firstOrNull()?.cve
                        if (vuln != null) {
                            val severity = vuln.metrics?.cvssMetricV31?.firstOrNull()?.cvssData?.baseSeverity ?: "Unknown"
                            val score = vuln.metrics?.cvssMetricV31?.firstOrNull()?.cvssData?.baseScore
                            results.add("NVD: $iocValue Severity=$severity Score=$score Desc=${vuln.descriptions?.firstOrNull()?.value ?: "N/A"}")
                        } else {
                            results.add("NVD: No data for $iocValue")
                        }
                    }
                    is com.cyberfusion.core.network.client.ApiResult.Error -> results.add("NVD error: ${result.message}")
                    else -> results.add("NVD: unexpected response")
                }
            }.onFailure { results.add("NVD exception: ${it.message}") }
        }

        if (otxKey.isNotBlank()) {
            runCatching {
                val adapter = OtxxAdapter(otxKey)
                val type = when {
                    iocValue.matches(Regex("^\\d+\\.\\d+\\.\\d+\\.\\d+$")) -> "IPv4"
                    iocValue.matches(Regex("^[a-fA-F0-9]{32,64}$")) -> "domain"
                    else -> "domain"
                }
                when (val result = adapter.getIndicatorGeneral(type, iocValue)) {
                    is com.cyberfusion.core.network.client.ApiResult.Success -> {
                        val pulses = result.data.pulse_info?.pulses ?: emptyList()
                        results.add("OTX: Reputation=${result.data.reputation}, Pulses=${pulses.size}, Top Pulse=${pulses.firstOrNull()?.name ?: "None"}")
                    }
                    is com.cyberfusion.core.network.client.ApiResult.Error -> results.add("OTX error: ${result.message}")
                    else -> results.add("OTX: unexpected response")
                }
            }.onFailure { results.add("OTX exception: ${it.message}") }
        }

        if (urlscanKey.isNotBlank() && (iocValue.startsWith("http://") || iocValue.startsWith("https://"))) {
            runCatching {
                val adapter = UrlScanAdapter(urlscanKey)
                when (val result = adapter.search(iocValue)) {
                    is com.cyberfusion.core.network.client.ApiResult.Success -> {
                        val r = result.data.results?.firstOrNull()
                        if (r != null) {
                            results.add("URLScan: Score=${r.verdicts?.urlscan?.score ?: "N/A"}, Category=${r.verdicts?.urlscan?.categories ?: emptyList()}")
                        } else {
                            results.add("URLScan: No results for $iocValue")
                        }
                    }
                    is com.cyberfusion.core.network.client.ApiResult.Error -> results.add("URLScan error: ${result.message}")
                    else -> results.add("URLScan: unexpected response")
                }
            }.onFailure { results.add("URLScan exception: ${it.message}") }
        }

        return if (results.isEmpty()) {
            AIToolResult("enrichIOC", true, "No external enrichment results for $iocValue. Check API keys in Settings.")
        } else {
            AIToolResult("enrichIOC", true, results.joinToString("\n"))
        }
    }

    private suspend fun executeGetInvestigations(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val status = parameters["status"]
        val investigations = if (status != null) repositories.investigationRepository.getByStatus(status).first()
        else repositories.investigationRepository.allInvestigations.first()
        if (investigations.isEmpty()) return AIToolResult("getInvestigations", true, "No investigations found")
        val summary = investigations.joinToString("\n") { inv ->
            "ID=${inv.id} | ${inv.title} | ${inv.status} | ${inv.severity}"
        }
        return AIToolResult("getInvestigations", true, "Found ${investigations.size} investigations:\n$summary")
    }

    private suspend fun executeCreateInvestigation(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val title = parameters["title"] ?: return AIToolResult("createInvestigation", false, "", "Missing title")
        val description = parameters["description"] ?: "No description"
        val id = repositories.investigationRepository.insert(InvestigationEntity(title = title, description = description, severity = "Medium", status = "Open"))
        return AIToolResult("createInvestigation", true, "Created investigation ID=$id with title: $title")
    }

    private suspend fun executeAddInvestigationNote(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val investigationId = parameters["investigationId"]?.toLongOrNull() ?: return AIToolResult("addInvestigationNote", false, "", "Missing investigationId")
        val content = parameters["content"] ?: return AIToolResult("addInvestigationNote", false, "", "Missing content")
        val noteId = repositories.investigationRepository.addNote(investigationId, content)
        return AIToolResult("addInvestigationNote", true, "Added note ID=$noteId to investigation $investigationId")
    }

    private suspend fun executeGetIncidents(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val status = parameters["status"]
        val incidents = if (status != null) repositories.incidentRepository.getByStatus(status).first()
        else repositories.incidentRepository.allIncidents.first()
        if (incidents.isEmpty()) return AIToolResult("getIncidents", true, "No incidents found")
        val summary = incidents.joinToString("\n") { inc ->
            "ID=${inc.id} | ${inc.title} | ${inc.status} | ${inc.severity}"
        }
        return AIToolResult("getIncidents", true, "Found ${incidents.size} incidents:\n$summary")
    }

    private suspend fun executeGetLabs(repositories: ToolRepositories): AIToolResult {
        val labs = repositories.labsRepository.allLabs.first()
        if (labs.isEmpty()) return AIToolResult("getLabs", true, "No labs available")
        val summary = labs.joinToString("\n") { lab ->
            "ID=${lab.id} | ${lab.title} | Difficulty: ${lab.difficulty}"
        }
        return AIToolResult("getLabs", true, "Available labs:\n$summary")
    }

    private suspend fun executeGetLab(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val labId = parameters["labId"]?.toLongOrNull() ?: return AIToolResult("getLab", false, "", "Missing labId")
        val lab = repositories.labsRepository.getLabById(labId)
        return if (lab != null) {
            AIToolResult("getLab", true, "Lab ID=$labId\nTitle: ${lab.title}\nDescription: ${lab.description}\nDifficulty: ${lab.difficulty}\nScenario: ${lab.scenario}")
        } else {
            AIToolResult("getLab", true, "Lab with ID $labId not found")
        }
    }

    private suspend fun executeGetLabProgress(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val labId = parameters["labId"]?.toLongOrNull() ?: return AIToolResult("getLabProgress", false, "", "Missing labId")
        val progress = repositories.labsRepository.getProgressByLabId(labId)
        return if (progress != null) {
            AIToolResult("getLabProgress", true, "Lab ID=$labId Progress: Score=${progress.score}%, Completed=${progress.completed}, Attempts=${progress.attempts}")
        } else {
            AIToolResult("getLabProgress", true, "No progress found for lab ID=$labId")
        }
    }

    private suspend fun executeSubmitLabAnswer(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val labId = parameters["labId"]?.toLongOrNull() ?: return AIToolResult("submitLabAnswer", false, "", "Missing labId")
        val lab = repositories.labsRepository.getLabById(labId) ?: return AIToolResult("submitLabAnswer", false, "", "Lab not found")
        val content = LabsContent.allLabs.find { it.id == labId }
        return if (content != null) {
            val score = com.cyberfusion.core.labs.LabEngine.calculateScore(content.questions, emptyMap())
            AIToolResult("submitLabAnswer", true, "Lab ID=$labId simulated submission. Score: $score%. Try the lab in the Labs tab for interactive scoring.")
        } else {
            AIToolResult("submitLabAnswer", true, "Lab ID=$labId content not found for scoring")
        }
    }

    private suspend fun executeGetRisks(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val risks = repositories.grcRepository.allRisks.first()
        if (risks.isEmpty()) return AIToolResult("getRisks", true, "No risks found")
        val summary = risks.joinToString("\n") { risk ->
            "ID=${risk.id} | ${risk.title} | Severity: ${risk.severity} | Score: ${risk.score} | Status: ${risk.status}"
        }
        return AIToolResult("getRisks", true, "Found ${risks.size} risks:\n$summary")
    }

    private suspend fun executeCreateRisk(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val title = parameters["title"] ?: return AIToolResult("createRisk", false, "", "Missing title")
        val severity = parameters["severity"] ?: "Medium"
        val description = parameters["description"] ?: ""
        val id = repositories.grcRepository.insertRisk(RiskEntity(title = title, severity = severity, description = description, score = 0, status = "Open"))
        return AIToolResult("createRisk", true, "Created risk ID=$id with title: $title")
    }

    private suspend fun executeGetControls(repositories: ToolRepositories): AIToolResult {
        val controls = repositories.grcRepository.allControls.first()
        if (controls.isEmpty()) return AIToolResult("getControls", true, "No controls found")
        val summary = controls.joinToString("\n") { control ->
            "ID=${control.id} | ${control.name} | Type: ${control.type} | Status: ${control.status}"
        }
        return AIToolResult("getControls", true, "Found ${controls.size} controls:\n$summary")
    }

    private suspend fun executeGenerateReport(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val reportType = parameters["reportType"] ?: "General"
        val alerts = repositories.alertRepository.allAlerts.first()
        val incidents = repositories.incidentRepository.allIncidents.first()
        val risks = repositories.grcRepository.allRisks.first()
        val report = buildString {
            appendLine("=== $reportType Report ===")
            appendLine("Generated: ${System.currentTimeMillis()}")
            appendLine()
            appendLine("Alerts: ${alerts.size}")
            appendLine("Incidents: ${incidents.size}")
            appendLine("Risks: ${risks.size}")
            appendLine()
            if (alerts.isNotEmpty()) {
                appendLine("Recent Alerts:")
                alerts.take(5).forEach { appendLine("- ${it.title} (${it.severity})") }
            }
            if (incidents.isNotEmpty()) {
                appendLine("Active Incidents:")
                incidents.take(5).forEach { appendLine("- ${it.title} (${it.status})") }
            }
            if (risks.isNotEmpty()) {
                appendLine("Top Risks:")
                risks.take(5).forEach { appendLine("- ${it.title} (${it.severity})") }
            }
        }
        return AIToolResult("generateReport", true, report)
    }

    private suspend fun executeSaveReport(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val title = parameters["title"] ?: return AIToolResult("saveReport", false, "", "Missing title")
        val content = parameters["content"] ?: return AIToolResult("saveReport", false, "", "Missing content")
        val id = repositories.reportRepository.insert(ReportEntity(title = title, content = content, format = "text"))
        return AIToolResult("saveReport", true, "Saved report ID=$id with title: $title")
    }

    private suspend fun executeQueryMalwareBazaar(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val hash = parameters["hash"] ?: return AIToolResult("queryMalwareBazaar", false, "", "Missing hash")
        val credentials = repositories.settingsRepository.allCredentials.first()
        val apiKey = credentials.find { it.provider == "malwarebazaar" }?.apiKey.orEmpty()
        if (apiKey.isBlank()) return AIToolResult("queryMalwareBazaar", true, "MalwareBazaar API key not configured in Settings")
        return try {
            val adapter = MalwareBazaarAdapter()
            when (val result = adapter.queryHash(hash)) {
                is com.cyberfusion.core.network.client.ApiResult.Success -> {
                    val samples = result.data.data
                    if (!samples.isNullOrEmpty()) {
                        val s = samples.first()
                        AIToolResult("queryMalwareBazaar", true, "MalwareBazaar result for $hash:\nFile: ${s.file_name ?: "N/A"}\nType: ${s.file_type ?: "N/A"}\nSignature: ${s.signature ?: "N/A"}\nFirst seen: ${s.first_seen ?: "N/A"}")
                    } else {
                        AIToolResult("queryMalwareBazaar", true, "No samples found for hash $hash")
                    }
                }
                is com.cyberfusion.core.network.client.ApiResult.Error -> AIToolResult("queryMalwareBazaar", false, "", "MalwareBazaar error: ${result.message}")
                else -> AIToolResult("queryMalwareBazaar", true, "MalwareBazaar returned empty result")
            }
        } catch (e: Exception) {
            AIToolResult("queryMalwareBazaar", false, "", "MalwareBazaar exception: ${e.message}")
        }
    }

    private suspend fun executeCheckAbuseIPDB(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val ip = parameters["ip"] ?: return AIToolResult("checkAbuseIPDB", false, "", "Missing ip")
        val credentials = repositories.settingsRepository.allCredentials.first()
        val apiKey = credentials.find { it.provider == "abuseipdb" }?.apiKey.orEmpty()
        if (apiKey.isBlank()) return AIToolResult("checkAbuseIPDB", true, "AbuseIPDB API key not configured in Settings")
        return try {
            val adapter = AbuseIPDBAdapter(apiKey)
            when (val result = adapter.checkIp(ip)) {
                is com.cyberfusion.core.network.client.ApiResult.Success -> {
                    val data = result.data.data
                    if (data != null) {
                        AIToolResult("checkAbuseIPDB", true, "AbuseIPDB result for $ip:\nScore: ${data.abuseConfidenceScore}\nISP: ${data.isp ?: "N/A"}\nCountry: ${data.countryCode ?: "N/A"}\nReports: ${data.totalReports}\nWhitelisted: ${data.isWhitelisted ?: false}")
                    } else {
                        AIToolResult("checkAbuseIPDB", true, "No data for IP $ip")
                    }
                }
                is com.cyberfusion.core.network.client.ApiResult.Error -> AIToolResult("checkAbuseIPDB", false, "", "AbuseIPDB error: ${result.message}")
                else -> AIToolResult("checkAbuseIPDB", true, "AbuseIPDB returned empty result")
            }
        } catch (e: Exception) {
            AIToolResult("checkAbuseIPDB", false, "", "AbuseIPDB exception: ${e.message}")
        }
    }

    private suspend fun executeSearchThreatFox(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val ioc = parameters["ioc"] ?: return AIToolResult("searchThreatFox", false, "", "Missing ioc")
        val iocType = parameters["iocType"]
        return try {
            val adapter = ThreatFoxAdapter()
            when (val result = adapter.searchIoc(ioc, iocType)) {
                is com.cyberfusion.core.network.client.ApiResult.Success -> {
                    val iocs = result.data.data
                    if (!iocs.isNullOrEmpty()) {
                        val summary = iocs.joinToString("\n") { iocItem ->
                            "IOC: ${iocItem.ioc}\nType: ${iocItem.ioc_type}\nMalware: ${iocItem.malware}\nThreat: ${iocItem.threat_type}\nConfidence: ${iocItem.confidence}"
                        }
                        AIToolResult("searchThreatFox", true, "ThreatFox results:\n$summary")
                    } else {
                        AIToolResult("searchThreatFox", true, "No ThreatFox results for $ioc")
                    }
                }
                is com.cyberfusion.core.network.client.ApiResult.Error -> AIToolResult("searchThreatFox", false, "", "ThreatFox error: ${result.message}")
                else -> AIToolResult("searchThreatFox", true, "ThreatFox returned empty result")
            }
        } catch (e: Exception) {
            AIToolResult("searchThreatFox", false, "", "ThreatFox exception: ${e.message}")
        }
    }

    private suspend fun executeGetNvdCve(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val cveId = parameters["cveId"] ?: return AIToolResult("getNvdCve", false, "", "Missing cveId")
        val credentials = repositories.settingsRepository.allCredentials.first()
        val apiKey = credentials.find { it.provider == "nvd" }?.apiKey.orEmpty()
        if (apiKey.isBlank()) return AIToolResult("getNvdCve", true, "NVD API key not configured in Settings")
        return try {
            val adapter = NvdAdapter()
            when (val result = adapter.getCve(cveId)) {
                is com.cyberfusion.core.network.client.ApiResult.Success -> {
                    val vuln = result.data.vulnerabilities?.firstOrNull()?.cve
                    if (vuln != null) {
                        val severity = vuln.metrics?.cvssMetricV31?.firstOrNull()?.cvssData?.baseSeverity ?: "Unknown"
                        val score = vuln.metrics?.cvssMetricV31?.firstOrNull()?.cvssData?.baseScore
                        val desc = vuln.descriptions?.firstOrNull()?.value ?: "No description"
                        AIToolResult("getNvdCve", true, "NVD CVE $cveId:\nSeverity: $severity\nScore: $score\nDescription: $desc")
                    } else {
                        AIToolResult("getNvdCve", true, "No NVD data for $cveId")
                    }
                }
                is com.cyberfusion.core.network.client.ApiResult.Error -> AIToolResult("getNvdCve", false, "", "NVD error: ${result.message}")
                else -> AIToolResult("getNvdCve", true, "NVD returned empty result")
            }
        } catch (e: Exception) {
            AIToolResult("getNvdCve", false, "", "NVD exception: ${e.message}")
        }
    }

    private suspend fun executeGetOtxIntel(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val type = parameters["type"] ?: return AIToolResult("getOtxIntel", false, "", "Missing type")
        val value = parameters["value"] ?: return AIToolResult("getOtxIntel", false, "", "Missing value")
        val credentials = repositories.settingsRepository.allCredentials.first()
        val apiKey = credentials.find { it.provider == "otx" }?.apiKey.orEmpty()
        if (apiKey.isBlank()) return AIToolResult("getOtxIntel", true, "OTX API key not configured in Settings")
        return try {
            val adapter = OtxxAdapter(apiKey)
            when (val result = adapter.getIndicatorGeneral(type, value)) {
                is com.cyberfusion.core.network.client.ApiResult.Success -> {
                    val pulses = result.data.pulse_info?.pulses ?: emptyList()
                    val pulseNames = pulses.joinToString("\n") { "- ${it.name}" }
                    AIToolResult("getOtxIntel", true, "OTX intel for $type:$value\nReputation: ${result.data.reputation}\nPulses: ${pulses.size}\n$pulseNames")
                }
                is com.cyberfusion.core.network.client.ApiResult.Error -> AIToolResult("getOtxIntel", false, "", "OTX error: ${result.message}")
                else -> AIToolResult("getOtxIntel", true, "OTX returned empty result")
            }
        } catch (e: Exception) {
            AIToolResult("getOtxIntel", false, "", "OTX exception: ${e.message}")
        }
    }

    private suspend fun executeScanUrl(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
        val query = parameters["query"] ?: return AIToolResult("scanUrl", false, "", "Missing query")
        val credentials = repositories.settingsRepository.allCredentials.first()
        val apiKey = credentials.find { it.provider == "urlscan" }?.apiKey.orEmpty()
        if (apiKey.isBlank()) return AIToolResult("scanUrl", true, "URLScan.io API key not configured in Settings")
        return try {
            val adapter = UrlScanAdapter(apiKey)
            when (val result = adapter.search(query)) {
                is com.cyberfusion.core.network.client.ApiResult.Success -> {
                    val results = result.data.results ?: emptyList()
                    if (results.isNotEmpty()) {
                        val top = results.first()
                        AIToolResult("scanUrl", true, "URLScan result for $query:\nScore: ${top.verdicts?.urlscan?.score ?: "N/A"}\nCategories: ${top.verdicts?.urlscan?.categories ?: emptyList()}\nDomain: ${top.page?.domain ?: "N/A"}")
                    } else {
                        AIToolResult("scanUrl", true, "No URLScan results for $query")
                    }
                }
                is com.cyberfusion.core.network.client.ApiResult.Error -> AIToolResult("scanUrl", false, "", "URLScan error: ${result.message}")
                else -> AIToolResult("scanUrl", true, "URLScan returned empty result")
            }
        } catch (e: Exception) {
            AIToolResult("scanUrl", false, "", "URLScan exception: ${e.message}")
        }
    }

    private suspend fun executeGetSettings(repositories: ToolRepositories): AIToolResult {
        val credentials = repositories.settingsRepository.allCredentials.first()
        val configured = credentials.filter { it.apiKey.isNotBlank() }.map { "${it.provider}: ${it.status}" }
        val missing = listOf("abuseipdb", "threatfox", "malwarebazaar", "nvd", "otx", "urlscan", "openrouter", "groq").filter { key ->
            credentials.none { it.provider == key && it.apiKey.isNotBlank() }
        }
        val alerts = repositories.alertRepository.allAlerts.first()
        val incidents = repositories.incidentRepository.allIncidents.first()
        val risks = repositories.grcRepository.allRisks.first()
        val labs = repositories.labsRepository.allLabs.first()
        return AIToolResult("getSettings", true, buildString {
            appendLine("=== CyberFusion Status ===")
            appendLine("Configured APIs: ${configured.joinToString(", ").ifBlank { "None"}}")
            appendLine("Missing APIs: ${missing.joinToString(", ")}")
            appendLine()
            appendLine("Database:")
            appendLine("- Alerts: ${alerts.size}")
            appendLine("- Incidents: ${incidents.size}")
            appendLine("- Risks: ${risks.size}")
            appendLine("- Labs: ${labs.size}")
            appendLine()
            appendLine("Career Mode Tips:")
            appendLine("- Configure API keys in Settings for full threat intel")
            appendLine("- Complete labs to practice SOC/GRC/ethical hacking skills")
            appendLine("- Use AI chat to analyze alerts and generate reports")
        })
    }

    private fun detectIocType(value: String): String {
        return when {
            value.matches(Regex("^\\d+\\.\\d+\\.\\d+\\.\\d+$")) -> "ip"
            value.matches(Regex("^[a-fA-F0-9]{32,64}$")) -> "hash"
            value.startsWith("CVE-") -> "cve"
            value.contains("@") -> "email"
            value.startsWith("http://") || value.startsWith("https://") -> "url"
            else -> "domain"
        }
    }
}
