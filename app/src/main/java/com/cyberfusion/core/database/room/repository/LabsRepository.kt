package com.cyberfusion.core.database.room.repository

import com.cyberfusion.core.database.room.dao.LabsDao
import com.cyberfusion.core.database.room.entity.*
import kotlinx.coroutines.flow.Flow

class LabsRepository(private val labsDao: LabsDao) {
    val allLabs: Flow<List<LabEntity>> = labsDao.getAllLabs()
    suspend fun getLabById(id: Long): LabEntity? = labsDao.getLabById(id)
    suspend fun getQuestionsByLabId(labId: Long): List<LabQuestionEntity> = labsDao.getQuestionsByLabId(labId)
    suspend fun insertLab(lab: LabEntity): Long = labsDao.insertLab(lab)
    suspend fun insertQuestion(question: LabQuestionEntity): Long = labsDao.insertQuestion(question)

    fun getAttemptsByLabId(labId: Long): Flow<List<LabAttemptEntity>> = labsDao.getAttemptsByLabId(labId)
    suspend fun insertAttempt(attempt: LabAttemptEntity): Long = labsDao.insertAttempt(attempt)

    suspend fun getProgressByLabId(labId: Long): LabProgressEntity? = labsDao.getProgressByLabId(labId)
    suspend fun insertProgress(progress: LabProgressEntity): Long = labsDao.insertProgress(progress)
    suspend fun updateProgress(labId: Long, completed: Boolean, score: Int) =
        labsDao.updateProgress(labId, completed, score, System.currentTimeMillis())
}