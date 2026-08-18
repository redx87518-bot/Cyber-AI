package com.cyberfusion.core.report

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
    val generatedAt: Long = System.currentTimeMillis(),
    val filePath: String? = null
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
