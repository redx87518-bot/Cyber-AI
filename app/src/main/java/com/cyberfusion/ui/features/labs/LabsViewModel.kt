package com.cyberfusion.ui.features.labs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberfusion.core.database.room.entity.LabAttemptEntity
import com.cyberfusion.core.database.room.entity.LabEntity
import com.cyberfusion.core.database.room.entity.LabProgressEntity
import com.cyberfusion.core.database.room.repository.LabsRepository
import com.cyberfusion.core.labs.LabContent
import com.cyberfusion.core.labs.LabsContent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class LabUiItem(
    val id: Long,
    val title: String,
    val description: String,
    val difficulty: String,
    val category: String,
    val progress: LabProgressEntity? = null
)

data class LabsUiState(
    val labs: List<LabUiItem> = emptyList(),
    val completed: Int = 0,
    val total: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val progressFraction: Float
        get() = if (total > 0) completed.toFloat() / total else 0f
}

class LabsViewModel(private val labsRepository: LabsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(LabsUiState())
    val uiState: StateFlow<LabsUiState> = _uiState.asStateFlow()

    init {
        loadLabs()
    }

    private fun loadLabs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                if (labsRepository.allLabs.first().isEmpty()) {
                    seedLabs()
                }
                val labs = labsRepository.allLabs.first()
                val items = labs.map { lab ->
                    LabUiItem(
                        id = lab.id,
                        title = lab.title,
                        description = lab.description,
                        difficulty = lab.difficulty,
                        category = lab.category,
                        progress = labsRepository.getProgressByLabId(lab.id)
                    )
                }
                _uiState.value = LabsUiState(
                    labs = items,
                    completed = items.count { it.progress?.completed == true },
                    total = items.size,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    private suspend fun seedLabs() {
        LabsContent.allLabs.forEach { content ->
            labsRepository.insertLab(
                LabEntity(
                    id = content.id,
                    title = content.title,
                    description = content.description,
                    category = content.category,
                    difficulty = content.difficulty,
                    scenario = content.scenario,
                    evidence = content.evidence ?: "",
                    questions = "",
                    hints = ""
                )
            )
        }
    }

    fun getLabContent(id: Long): LabContent? = LabsContent.allLabs.find { it.id == id }

    /** Re-reads labs + progress (called when the list screen resumes). */
    fun refresh() {
        loadLabs()
    }

    /** Persists a submission: attempt record + best-score progress. */
    fun saveAttempt(labId: Long, answers: Map<Int, Int>, score: Int) {
        viewModelScope.launch {
            runCatching {
                labsRepository.insertAttempt(
                    LabAttemptEntity(
                        labId = labId,
                        answers = answers.toString(),
                        score = score,
                        completedAt = if (score >= 70) System.currentTimeMillis() else null
                    )
                )
                val existing = labsRepository.getProgressByLabId(labId)
                if (existing == null) {
                    labsRepository.insertProgress(
                        LabProgressEntity(
                            labId = labId,
                            completed = score >= 70,
                            score = score,
                            attempts = 1,
                            lastAttemptAt = System.currentTimeMillis()
                        )
                    )
                } else {
                    labsRepository.updateProgress(
                        labId = labId,
                        completed = existing.completed || score >= 70,
                        score = maxOf(existing.score, score)
                    )
                }
            }
        }
    }
}
