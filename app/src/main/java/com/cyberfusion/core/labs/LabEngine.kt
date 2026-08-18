package com.cyberfusion.core.labs

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class LabContent(
    val id: Long,
    val title: String,
    val description: String,
    val category: String,
    val difficulty: String,
    val scenario: String,
    val evidence: String? = null,
    val questions: List<LabQuestion>,
    val hints: Map<Int, String> = emptyMap(),
    val debrief: String? = null
)

@Serializable
data class LabQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswer: Int,
    val explanation: String
)

object LabEngine {
    private val json = Json { ignoreUnknownKeys = true }

    fun calculateScore(questions: List<LabQuestion>, answers: Map<Int, Int>): Int {
        if (questions.isEmpty()) return 0
        var correct = 0
        questions.forEach { question ->
            val userAnswer = answers[question.id]
            if (userAnswer == question.correctAnswer) {
                correct++
            }
        }
        return (correct * 100) / questions.size
    }

    fun getFeedback(score: Int): String {
        return when {
            score >= 90 -> "Excellent work! You demonstrate strong cybersecurity analysis skills."
            score >= 70 -> "Good job! You have a solid understanding of the concepts."
            score >= 50 -> "Fair attempt. Review the material and try again to improve."
            else -> "Keep learning. Review the scenario and hints, then retry the lab."
        }
    }

    fun parseLabContent(entity: com.cyberfusion.core.database.room.entity.LabEntity): LabContent {
        val questions = if (entity.questions.isNotBlank()) {
            json.decodeFromString<List<LabQuestion>>(entity.questions)
        } else {
            emptyList()
        }
        val hints = if (entity.hints.isNotBlank()) {
            json.decodeFromString<Map<Int, String>>(entity.hints)
        } else {
            emptyMap()
        }
        return LabContent(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            category = entity.category,
            difficulty = entity.difficulty,
            scenario = entity.scenario,
            evidence = entity.evidence,
            questions = questions,
            hints = hints,
            debrief = entity.evidence
        )
    }

    fun getLabById(labs: List<com.cyberfusion.core.database.room.entity.LabEntity>, id: Long): LabContent? {
        return labs.find { it.id == id }?.let { parseLabContent(it) }
    }
}
