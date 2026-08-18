package com.cyberfusion.ui.compose

import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import com.cyberfusion.core.agent.AgentService
import com.cyberfusion.core.agent.DefaultAgentService
import com.cyberfusion.core.ai.tools.ToolRepositories
import com.cyberfusion.core.database.room.CyberFusionDatabase
import com.cyberfusion.core.database.room.repository.ConversationRepository

val LocalViewModelFactory = compositionLocalOf<ViewModelFactory> {
    error("No ViewModelFactory provided")
}

class ViewModelFactory(
    private val database: CyberFusionDatabase,
    private val appContext: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repositories = Repositories(database, appContext)
        val toolRepositories = ToolRepositories(
            alertRepository = repositories.alertRepository,
            investigationRepository = repositories.investigationRepository,
            iocRepository = repositories.iocRepository,
            threatIntelRepository = repositories.threatIntelRepository,
            incidentRepository = repositories.incidentRepository,
            labsRepository = repositories.labsRepository,
            grcRepository = repositories.grcRepository,
            reportRepository = repositories.reportRepository,
            aiRepository = repositories.aiRepository,
            settingsRepository = repositories.settingsRepository
        )
        val conversationRepository = ConversationRepository(database.conversationDao())
        val agentService = DefaultAgentService(
            repositories.settingsRepository,
            toolRepositories,
            conversationRepository,
            appContext
        )
        return when {
            modelClass.isAssignableFrom(com.cyberfusion.ui.features.ai.ChatViewModel::class.java) ->
                @Suppress("UNCHECKED_CAST")
                com.cyberfusion.ui.features.ai.ChatViewModel(repositories.settingsRepository, agentService, conversationRepository, appContext) as T
            modelClass.isAssignableFrom(com.cyberfusion.ui.features.threatintel.ThreatIntelViewModel::class.java) ->
                @Suppress("UNCHECKED_CAST")
                com.cyberfusion.ui.features.threatintel.ThreatIntelViewModel(repositories.settingsRepository) as T
            modelClass.isAssignableFrom(com.cyberfusion.ui.features.labs.LabsViewModel::class.java) ->
                @Suppress("UNCHECKED_CAST")
                com.cyberfusion.ui.features.labs.LabsViewModel(repositories.labsRepository) as T
            modelClass.isAssignableFrom(com.cyberfusion.ui.features.settings.SettingsViewModel::class.java) ->
                @Suppress("UNCHECKED_CAST")
                com.cyberfusion.ui.features.settings.SettingsViewModel(repositories.settingsRepository, repositories.secureStorage) as T
            modelClass.isAssignableFrom(com.cyberfusion.ui.features.dashboard.DashboardViewModel::class.java) ->
                @Suppress("UNCHECKED_CAST")
                com.cyberfusion.ui.features.dashboard.DashboardViewModel(repositories.alertRepository, repositories.incidentRepository, repositories.grcRepository, repositories.labsRepository) as T
            modelClass.isAssignableFrom(com.cyberfusion.ui.features.alerts.AlertsViewModel::class.java) ->
                @Suppress("UNCHECKED_CAST")
                com.cyberfusion.ui.features.alerts.AlertsViewModel(repositories.alertRepository) as T
            modelClass.isAssignableFrom(com.cyberfusion.ui.features.incidents.IncidentsViewModel::class.java) ->
                @Suppress("UNCHECKED_CAST")
                com.cyberfusion.ui.features.incidents.IncidentsViewModel(repositories.incidentRepository) as T
            modelClass.isAssignableFrom(com.cyberfusion.ui.features.grc.GRCViewModel::class.java) ->
                @Suppress("UNCHECKED_CAST")
                com.cyberfusion.ui.features.grc.GRCViewModel(repositories.grcRepository) as T
            modelClass.isAssignableFrom(com.cyberfusion.ui.features.reports.ReportsViewModel::class.java) ->
                @Suppress("UNCHECKED_CAST")
                com.cyberfusion.ui.features.reports.ReportsViewModel(repositories.reportRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

class Repositories(private val database: CyberFusionDatabase, private val appContext: Context) {
    val settingsRepository: com.cyberfusion.core.database.room.repository.SettingsRepository by lazy {
        com.cyberfusion.core.database.room.repository.SettingsRepository(database.settingsDao())
    }
    val secureStorage: com.cyberfusion.core.security.SecureStorage by lazy {
        com.cyberfusion.core.security.SecureStorage(appContext, settingsRepository)
    }
    val labsRepository: com.cyberfusion.core.database.room.repository.LabsRepository by lazy {
        com.cyberfusion.core.database.room.repository.LabsRepository(database.labsDao())
    }
    val alertRepository: com.cyberfusion.core.database.room.repository.AlertRepository by lazy {
        com.cyberfusion.core.database.room.repository.AlertRepository(database.alertsDao())
    }
    val investigationRepository: com.cyberfusion.core.database.room.repository.InvestigationRepository by lazy {
        com.cyberfusion.core.database.room.repository.InvestigationRepository(
            database.investigationsDao(),
            database.investigationNotesDao(),
            database.investigationTimelineDao(),
            database.evidenceDao()
        )
    }
    val iocRepository: com.cyberfusion.core.database.room.repository.IocRepository by lazy {
        com.cyberfusion.core.database.room.repository.IocRepository(database.iocDao(), database.iocEnrichmentDao())
    }
    val threatIntelRepository: com.cyberfusion.core.database.room.repository.ThreatIntelRepository by lazy {
        com.cyberfusion.core.database.room.repository.ThreatIntelRepository(database.threatIntelDao())
    }
    val incidentRepository: com.cyberfusion.core.database.room.repository.IncidentRepository by lazy {
        com.cyberfusion.core.database.room.repository.IncidentRepository(database.incidentsDao())
    }
    val grcRepository: com.cyberfusion.core.database.room.repository.GRCRepository by lazy {
        com.cyberfusion.core.database.room.repository.GRCRepository(database.grcDao())
    }
    val reportRepository: com.cyberfusion.core.database.room.repository.ReportRepository by lazy {
        com.cyberfusion.core.database.room.repository.ReportRepository(database.reportsDao())
    }
    val aiRepository: com.cyberfusion.core.database.room.repository.AiRepository by lazy {
        com.cyberfusion.core.database.room.repository.AiRepository(database.aiDao())
    }
}
