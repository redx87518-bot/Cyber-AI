package com.cyberfusion.ui.features.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberfusion.core.database.room.entity.AlertEntity
import com.cyberfusion.core.database.room.repository.AlertRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AlertsUiState(
    val alerts: List<AlertEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class AlertsViewModel(private val alertRepository: AlertRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AlertsUiState())
    val uiState: StateFlow<AlertsUiState> = _uiState.asStateFlow()

    init {
        loadAlerts()
    }

    fun loadAlerts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                alertRepository.allAlerts.collect { alerts ->
                    _uiState.value = _uiState.value.copy(alerts = alerts, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
