package com.cyberfusion.core.agent

interface AgentService {
    suspend fun execute(request: AgentRequest): AgentResponse
    suspend fun cancel(taskId: String): Boolean
    fun getHistory(taskId: String): List<AgentEvent>
}

data class AgentRequest(
    val taskId: String,
    val prompt: String,
    val context: Map<String, String> = emptyMap(),
    val preferredAgent: String? = null,
    val requireReport: Boolean = false,
    val requirePdf: Boolean = false
)

data class AgentResponse(
    val taskId: String,
    val status: AgentStatus,
    val result: String? = null,
    val plan: AgentPlan? = null,
    val report: AgentReport? = null,
    val error: String? = null,
    val events: List<AgentEvent> = emptyList()
)

enum class AgentStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED
}

data class AgentTask(
    val taskId: String,
    val prompt: String,
    val status: AgentStatus = AgentStatus.PENDING,
    val plan: AgentPlan? = null,
    val result: String? = null,
    val error: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class AgentPlan(
    val steps: List<AgentPlanStep>,
    val summary: String
)

data class AgentPlanStep(
    val stepId: String,
    val description: String,
    val agent: String,
    val tool: String? = null,
    val input: Map<String, String> = emptyMap(),
    val status: AgentStepStatus = AgentStepStatus.PENDING,
    val result: String? = null,
    val error: String? = null,
    val evidence: String? = null,
    val startTime: Long? = null,
    val endTime: Long? = null
)

enum class AgentStepStatus {
    PENDING,
    RUNNING,
    SUCCESS,
    FAILED,
    SKIPPED,
    WAITING_FOR_APPROVAL
}

data class AgentEvent(
    val taskId: String,
    val eventType: AgentEventType,
    val agent: String,
    val tool: String? = null,
    val status: AgentStepStatus,
    val timestamp: Long = System.currentTimeMillis(),
    val durationMs: Long? = null,
    val details: Map<String, String> = emptyMap()
)

enum class AgentEventType {
    TASK_CREATED,
    PLAN_GENERATED,
    TOOL_EXECUTION,
    STEP_COMPLETED,
    STEP_FAILED,
    ADAPTATION,
    VERIFICATION,
    REPORT_GENERATED,
    TASK_COMPLETED,
    TASK_FAILED
}

data class AgentReport(
    val reportId: String,
    val title: String,
    val summary: String,
    val findings: List<AgentFinding>,
    val evidence: List<AgentEvidence>,
    val recommendations: List<String>,
    val severity: String? = null,
    val confidence: Int? = null,
    val methodology: String? = null,
    val limitations: List<String> = emptyList(),
    val mitreAttack: List<String> = emptyList(),
    val iso27001Controls: List<String> = emptyList(),
    val metadata: Map<String, String> = emptyMap(),
    val generatedAt: Long = System.currentTimeMillis()
)

data class AgentFinding(
    val id: String,
    val title: String,
    val description: String,
    val severity: String,
    val confidence: Int,
    val evidenceRefs: List<String> = emptyList()
)

data class AgentEvidence(
    val id: String,
    val type: String,
    val source: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
