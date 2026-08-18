package com.cyberfusion.core.ai.tools

import com.cyberfusion.ai.provider.AITool
import com.cyberfusion.ai.provider.AIToolResult
import com.cyberfusion.core.database.room.repository.AlertRepository
import com.cyberfusion.core.database.room.repository.IncidentRepository
import com.cyberfusion.core.database.room.repository.InvestigationRepository
import com.cyberfusion.core.database.room.repository.IocRepository
import com.cyberfusion.core.database.room.repository.LabsRepository
import com.cyberfusion.core.database.room.repository.GRCRepository
import com.cyberfusion.core.database.room.repository.ReportRepository
import com.cyberfusion.core.database.room.repository.AiRepository
import com.cyberfusion.core.database.room.repository.SettingsRepository
import com.cyberfusion.core.database.room.repository.ThreatIntelRepository
import kotlinx.coroutines.flow.first

object AIToolRegistry {
    val tools: List<AITool> = listOf(
        AITool("getAlerts", "Get alerts from the database", mapOf("status" to "String?")),
        AITool("getAlert", "Get a specific alert", mapOf("id" to "Long")),
        AITool("searchIOC", "Search for an IOC", mapOf("value" to "String", "type" to "String?")),
        AITool("enrichIOC", "Enrich an IOC with threat intelligence", mapOf("iocId" to "Long")),
        AITool("getInvestigation", "Get an investigation", mapOf("id" to "Long")),
        AITool("createInvestigation", "Create an investigation", mapOf("title" to "String", "description" to "String")),
        AITool("addInvestigationNote", "Add a note to an investigation", mapOf("investigationId" to "Long", "content" to "String")),
        AITool("getIncident", "Get an incident", mapOf("id" to "Long")),
        AITool("getLabs", "Get available labs", emptyMap()),
        AITool("getLab", "Get a specific lab", mapOf("id" to "Long")),
        AITool("getLabProgress", "Get lab progress", mapOf("labId" to "Long")),
        AITool("submitLabAnswer", "Submit lab answer", mapOf("labId" to "Long", "answers" to "String")),
        AITool("getGRCItems", "Get GRC items", emptyMap()),
        AITool("getRisks", "Get risks", emptyMap()),
        AITool("generateReport", "Generate a report", mapOf("type" to "String")),
        AITool("saveReport", "Save a report", mapOf("title" to "String", "content" to "String"))
    )

    suspend fun executeTool(
        toolName: String,
        parameters: Map<String, String>,
        repositories: ToolRepositories
    ): AIToolResult {
        return try {
            when (toolName) {
                "getAlerts" -> {
                    val status = parameters["status"]
                    val result = if (status != null) repositories.alertRepository.getByStatus(status) else repositories.alertRepository.allAlerts
                    val count = result.first().size
                    AIToolResult(toolName, true, "Retrieved $count alerts")
                }
                "searchIOC" -> {
                    val value = parameters["value"] ?: return AIToolResult(toolName, false, "", "Missing value parameter")
                    repositories.iocRepository.getByValue(value)
                    AIToolResult(toolName, true, "Found IOC: $value")
                }
                else -> AIToolResult(toolName, false, "", "Unknown tool: $toolName")
            }
        } catch (e: Exception) {
            AIToolResult(toolName, false, "", e.message ?: "Unknown error")
        }
    }
}

data class ToolRepositories(
    val alertRepository: AlertRepository,
    val investigationRepository: InvestigationRepository,
    val iocRepository: IocRepository,
    val threatIntelRepository: ThreatIntelRepository,
    val incidentRepository: IncidentRepository,
    val labsRepository: LabsRepository,
    val grcRepository: GRCRepository,
    val reportRepository: ReportRepository,
    val aiRepository: AiRepository,
    val settingsRepository: SettingsRepository
)