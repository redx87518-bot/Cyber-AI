package com.cyberfusion.ui.features.incidents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberfusion.core.database.room.entity.IncidentEntity
import com.cyberfusion.core.database.room.repository.IncidentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class IncidentsUiState(
    val incidents: List<IncidentEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class IncidentsViewModel(private val incidentRepository: IncidentRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(IncidentsUiState())
    val uiState: StateFlow<IncidentsUiState> = _uiState.asStateFlow()

    init {
        loadIncidents()
    }

    fun loadIncidents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                incidentRepository.allIncidents.collect { incidents ->
                    _uiState.value = _uiState.value.copy(incidents = incidents, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
