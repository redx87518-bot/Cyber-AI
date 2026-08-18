package com.cyberfusion.ui.features.grc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberfusion.core.database.room.entity.RiskEntity
import com.cyberfusion.core.database.room.repository.GRCRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GRCUiState(
    val risks: List<RiskEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class GRCViewModel(private val grcRepository: GRCRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(GRCUiState())
    val uiState: StateFlow<GRCUiState> = _uiState.asStateFlow()

    init {
        loadRisks()
    }

    fun loadRisks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                grcRepository.allRisks.collect { risks ->
                    _uiState.value = _uiState.value.copy(risks = risks, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
