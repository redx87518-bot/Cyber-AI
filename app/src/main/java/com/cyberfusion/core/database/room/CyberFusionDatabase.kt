package com.cyberfusion.core.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cyberfusion.core.database.room.converter.Converters
import com.cyberfusion.core.database.room.dao.*
import com.cyberfusion.core.database.room.entity.*

@Database(
    entities = [
        AlertEntity::class,
        InvestigationEntity::class,
        InvestigationNoteEntity::class,
        InvestigationTimelineEntity::class,
        EvidenceEntity::class,
        IncidentEntity::class,
        IocEntity::class,
        IocEnrichmentEntity::class,
        ThreatIntelligenceEntity::class,
        RiskEntity::class,
        ControlEntity::class,
        FrameworkEntity::class,
        RemediationTaskEntity::class,
        LabEntity::class,
        LabQuestionEntity::class,
        LabAttemptEntity::class,
        LabProgressEntity::class,
        ReportEntity::class,
        AiTaskEntity::class,
        AiTaskHistoryEntity::class,
        AiToolCallEntity::class,
        SettingEntity::class,
        ApiCredentialEntity::class,
        ConversationEntity::class,
        MessageEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CyberFusionDatabase : RoomDatabase() {
    abstract fun alertsDao(): AlertsDao
    abstract fun investigationsDao(): InvestigationsDao
    abstract fun investigationNotesDao(): InvestigationNotesDao
    abstract fun investigationTimelineDao(): InvestigationTimelineDao
    abstract fun evidenceDao(): EvidenceDao
    abstract fun incidentsDao(): IncidentsDao
    abstract fun iocDao(): IocDao
    abstract fun iocEnrichmentDao(): IocEnrichmentDao
    abstract fun threatIntelDao(): ThreatIntelDao
    abstract fun grcDao(): GRCDao
    abstract fun labsDao(): LabsDao
    abstract fun reportsDao(): ReportsDao
    abstract fun aiDao(): AiDao
    abstract fun settingsDao(): SettingsDao
    abstract fun conversationDao(): ConversationDao

    companion object {
        @Volatile
        private var INSTANCE: CyberFusionDatabase? = null

        fun getInstance(context: Context): CyberFusionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CyberFusionDatabase::class.java,
                    "cyberfusion.db"
                ).fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
