package com.cyberfusion.ui.compose

import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

val LocalViewModelFactory = compositionLocalOf<ViewModelFactory> {
    error("No ViewModelFactory provided")
}

class ViewModelFactory(
    private val database: CyberFusionDatabase,
    private val appContext: android.content.Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repositories = Repositories(database, appContext)
        return when {
            modelClass.isAssignableFrom(com.cyberfusion.ui.features.ai.ChatViewModel::class.java) ->
                @Suppress("UNCHECKED_CAST")
                com.cyberfusion.ui.features.ai.ChatViewModel(repositories.settingsRepository) as T
            modelClass.isAssignableFrom(com.cyberfusion.ui.features.threatintel.ThreatIntelViewModel::class.java) ->
                @Suppress("UNCHECKED_CAST")
                com.cyberfusion.ui.features.threatintel.ThreatIntelViewModel(repositories.settingsRepository) as T
            modelClass.isAssignableFrom(com.cyberfusion.ui.features.labs.LabsViewModel::class.java) ->
                @Suppress("UNCHECKED_CAST")
                com.cyberfusion.ui.features.labs.LabsViewModel(repositories.labsRepository) as T
            modelClass.isAssignableFrom(com.cyberfusion.ui.features.settings.SettingsViewModel::class.java) ->
                @Suppress("UNCHECKED_CAST")
                com.cyberfusion.ui.features.settings.SettingsViewModel(repositories.settingsRepository, repositories.secureStorage) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

class Repositories(private val database: CyberFusionDatabase, private val appContext: android.content.Context) {
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
