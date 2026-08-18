package com.cyberfusion.ui.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberfusion.core.database.room.repository.AlertRepository
import com.cyberfusion.core.database.room.repository.IncidentRepository
import com.cyberfusion.core.database.room.repository.GRCRepository
import com.cyberfusion.core.database.room.repository.LabsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class DashboardState(
    val alertsCount: Int = 0,
    val investigationsCount: Int = 0,
    val incidentsCount: Int = 0,
    val labsCount: Int = 0,
    val criticalAlerts: Int = 0,
    val activeIncidents: Int = 0,
    val openRisks: Int = 0,
    val isLoading: Boolean = false
)

class DashboardViewModel(
    private val alertRepository: AlertRepository,
    private val incidentRepository: IncidentRepository,
    private val grcRepository: GRCRepository,
    private val labsRepository: LabsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            combine(
                alertRepository.allAlerts,
                incidentRepository.allIncidents,
                grcRepository.allRisks,
                labsRepository.allLabs
            ) { alerts, incidents, risks, labs ->
                val criticalAlerts = alerts.count { it.severity.equals("Critical", ignoreCase = true) || it.severity.equals("High", ignoreCase = true) }
                val activeIncidents = incidents.count { it.status.equals("Active", ignoreCase = true) }
                val openRisks = risks.count { it.status.equals("Open", ignoreCase = true) }
                DashboardState(
                    alertsCount = alerts.size,
                    incidentsCount = incidents.size,
                    labsCount = labs.size,
                    criticalAlerts = criticalAlerts,
                    activeIncidents = activeIncidents,
                    openRisks = openRisks,
                    isLoading = false
                )
            }.collect { _state.value = it }
        }
    }
}
