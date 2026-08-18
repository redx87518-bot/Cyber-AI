package com.cyberfusion.ui.features.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberfusion.core.database.room.entity.ReportEntity
import com.cyberfusion.core.database.room.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReportsUiState(
    val reports: List<ReportEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ReportsViewModel(private val reportRepository: ReportRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        loadReports()
    }

    fun loadReports() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                reportRepository.allReports.collect { reports ->
                    _uiState.value = _uiState.value.copy(reports = reports, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
