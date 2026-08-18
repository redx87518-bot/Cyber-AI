package com.cyberfusion.core.database.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberfusion.core.database.room.entity.LabAttemptEntity
import com.cyberfusion.core.database.room.entity.LabEntity
import com.cyberfusion.core.database.room.entity.LabProgressEntity
import com.cyberfusion.core.database.room.entity.LabQuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LabsDao {
    @Query("SELECT * FROM labs ORDER BY createdAt DESC")
    fun getAllLabs(): Flow<List<LabEntity>>

    @Query("SELECT * FROM labs WHERE id = :id")
    suspend fun getLabById(id: Long): LabEntity?

    @Query("SELECT * FROM lab_questions WHERE labId = :labId ORDER BY id ASC")
    suspend fun getQuestionsByLabId(labId: Long): List<LabQuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLab(lab: LabEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: LabQuestionEntity): Long

    @Query("SELECT * FROM lab_attempts WHERE labId = :labId ORDER BY createdAt DESC")
    fun getAttemptsByLabId(labId: Long): Flow<List<LabAttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: LabAttemptEntity): Long

    @Query("SELECT * FROM lab_progress WHERE labId = :labId LIMIT 1")
    suspend fun getProgressByLabId(labId: Long): LabProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: LabProgressEntity): Long

    @Query("UPDATE lab_progress SET completed = :completed, score = :score, attempts = attempts + 1, lastAttemptAt = :lastAttemptAt WHERE labId = :labId")
    suspend fun updateProgress(labId: Long, completed: Boolean, score: Int, lastAttemptAt: Long)
}