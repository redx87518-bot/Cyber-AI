package com.cyberfusion.core.evidence

data class EvidenceItem(
    val id: String,
    val taskId: String,
    val type: String,
    val source: String,
    val content: String,
    val confidence: Double,
    val verified: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class EvidenceCorrelation(
    val id: String,
    val taskId: String,
    val sources: List<String>,
    val correlationType: String,
    val result: String,
    val confidence: Double
)

object EvidenceManager {
    private val evidenceStore = mutableMapOf<String, MutableList<EvidenceItem>>()
    private val correlationStore = mutableMapOf<String, MutableList<EvidenceCorrelation>>()

    fun addEvidence(taskId: String, item: EvidenceItem) {
        evidenceStore.getOrPut(taskId) { mutableListOf() }.add(item)
    }

    fun getEvidence(taskId: String): List<EvidenceItem> {
        return evidenceStore[taskId] ?: emptyList()
    }

    fun addCorrelation(taskId: String, correlation: EvidenceCorrelation) {
        correlationStore.getOrPut(taskId) { mutableListOf() }.add(correlation)
    }

    fun getCorrelations(taskId: String): List<EvidenceCorrelation> {
        return correlationStore[taskId] ?: emptyList()
    }

    fun clearTask(taskId: String) {
        evidenceStore.remove(taskId)
        correlationStore.remove(taskId)
    }

    fun calculateTaskConfidence(taskId: String): Double {
        val evidence = getEvidence(taskId)
        if (evidence.isEmpty()) return 0.0
        val verifiedEvidence = evidence.filter { it.verified }
        val totalConfidence = evidence.map { it.confidence }.average()
        val verificationBonus = if (evidence.isNotEmpty()) verifiedEvidence.size.toDouble() / evidence.size else 0.0
        return (totalConfidence * 0.7 + verificationBonus * 30).coerceIn(0.0, 100.0)
    }
}
