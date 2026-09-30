package com.cyberfusion.ui.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberfusion.core.database.room.entity.AlertEntity
import com.cyberfusion.core.database.room.repository.AlertRepository
import com.cyberfusion.core.database.room.repository.GRCRepository
import com.cyberfusion.core.database.room.repository.IncidentRepository
import com.cyberfusion.core.database.room.repository.LabsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DashboardState(
    val alertsCount: Int = 0,
    val criticalAlerts: Int = 0,
    val activeIncidents: Int = 0,
    val openRisks: Int = 0,
    val labsCount: Int = 0,
    val recentAlerts: List<AlertEntity> = emptyList(),
    val isLoading: Boolean = false
)

class DashboardViewModel(
    alertRepository: AlertRepository,
    incidentRepository: IncidentRepository,
    grcRepository: GRCRepository,
    labsRepository: LabsRepository
) : ViewModel() {

    val state: StateFlow<DashboardState> = combine(
        alertRepository.allAlerts,
        incidentRepository.allIncidents,
        grcRepository.allRisks,
        labsRepository.allLabs
    ) { alerts, incidents, risks, labs ->
        DashboardState(
            alertsCount = alerts.size,
            criticalAlerts = alerts.count {
                it.severity.equals("Critical", true) || it.severity.equals("High", true)
            },
            activeIncidents = incidents.count { it.status.equals("Active", true) },
            openRisks = risks.count { it.status.equals("Open", true) },
            labsCount = labs.size,
            recentAlerts = alerts.sortedByDescending { it.createdAt }.take(3)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardState())
}
