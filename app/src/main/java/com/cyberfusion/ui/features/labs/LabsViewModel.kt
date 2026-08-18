package com.cyberfusion.ui.features.labs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberfusion.core.database.room.entity.LabEntity
import com.cyberfusion.core.database.room.entity.LabProgressEntity
import com.cyberfusion.core.database.room.repository.LabsRepository
import com.cyberfusion.core.labs.LabContent
import com.cyberfusion.core.labs.LabsContent
import com.cyberfusion.core.labs.LabEngine
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
    val isLoading: Boolean = false,
    val error: String? = null
)

class LabsViewModel(
    private val labsRepository: LabsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LabsUiState())
    val uiState: StateFlow<LabsUiState> = _uiState.asStateFlow()
    
    init {
        loadLabs()
    }
    
    private fun loadLabs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val existingLabs = labsRepository.allLabs.first()
                if (existingLabs.isEmpty()) {
                    seedLabs()
                }
                val labs = labsRepository.allLabs.first()
                val uiItems = labs.map { lab ->
                    val progress = labsRepository.getProgressByLabId(lab.id)
                    LabUiItem(
                        id = lab.id,
                        title = lab.title,
                        description = lab.description,
                        difficulty = lab.difficulty,
                        category = lab.category,
                        progress = progress
                    )
                }
                _uiState.value = _uiState.value.copy(labs = uiItems, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
    
    private suspend fun seedLabs() {
        LabsContent.allLabs.forEach { labContent ->
            val labEntity = LabEntity(
                id = labContent.id,
                title = labContent.title,
                description = labContent.description,
                category = labContent.category,
                difficulty = labContent.difficulty,
                scenario = labContent.scenario,
                evidence = labContent.evidence,
                questions = kotlinx.serialization.json.Json.encodeToString(labContent.questions),
                hints = kotlinx.serialization.json.Json.encodeToString(labContent.hints),
                createdAt = System.currentTimeMillis()
            )
            val labId = labsRepository.insertLab(labEntity)
            labContent.questions.forEach { question ->
                val questionEntity = com.cyberfusion.core.database.room.entity.LabQuestionEntity(
                    labId = labId,
                    question = question.question,
                    options = kotlinx.serialization.json.Json.encodeToString(question.options),
                    correctAnswer = question.correctAnswer.toString(),
                    explanation = question.explanation
                )
                labsRepository.insertQuestion(questionEntity)
            }
        }
    }
    
    fun getLabContent(id: Long): LabContent? {
        return LabsContent.allLabs.find { it.id == id }
    }
}
