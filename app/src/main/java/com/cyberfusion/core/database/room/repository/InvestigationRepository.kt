package com.cyberfusion.core.database.room.repository

import com.cyberfusion.core.database.room.dao.EvidenceDao
import com.cyberfusion.core.database.room.dao.InvestigationNotesDao
import com.cyberfusion.core.database.room.dao.InvestigationTimelineDao
import com.cyberfusion.core.database.room.dao.InvestigationsDao
import com.cyberfusion.core.database.room.entity.*
import kotlinx.coroutines.flow.Flow

class InvestigationRepository(
    private val investigationsDao: InvestigationsDao,
    private val notesDao: InvestigationNotesDao,
    private val timelineDao: InvestigationTimelineDao,
    private val evidenceDao: EvidenceDao
) {
    val allInvestigations: Flow<List<InvestigationEntity>> = investigationsDao.getAll()
    fun getByStatus(status: String): Flow<List<InvestigationEntity>> = investigationsDao.getByStatus(status)

    suspend fun getById(id: Long): InvestigationEntity? = investigationsDao.getById(id)
    suspend fun insert(investigation: InvestigationEntity): Long = investigationsDao.insert(investigation)
    suspend fun updateStatus(id: Long, status: String) {
        investigationsDao.updateStatus(id, status, System.currentTimeMillis())
    }
    suspend fun delete(investigation: InvestigationEntity) = investigationsDao.delete(investigation)

    fun getNotes(investigationId: Long): Flow<List<InvestigationNoteEntity>> = notesDao.getByInvestigationId(investigationId)
    suspend fun addNote(investigationId: Long, content: String): Long {
        val note = InvestigationNoteEntity(investigationId = investigationId, content = content)
        return notesDao.insert(note)
    }

    fun getTimeline(investigationId: Long): Flow<List<InvestigationTimelineEntity>> = timelineDao.getByInvestigationId(investigationId)
    suspend fun addTimelineEvent(investigationId: Long, event: String): Long {
        val evt = InvestigationTimelineEntity(investigationId = investigationId, event = event)
        return timelineDao.insert(evt)
    }

    fun getEvidence(investigationId: Long): Flow<List<EvidenceEntity>> = evidenceDao.getByInvestigationId(investigationId)
    suspend fun addEvidence(investigationId: Long, type: String, description: String, value: String): Long {
        val evidence = EvidenceEntity(investigationId = investigationId, type = type, description = description, value = value)
        return evidenceDao.insert(evidence)
    }
}