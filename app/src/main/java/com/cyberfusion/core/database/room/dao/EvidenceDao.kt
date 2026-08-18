package com.cyberfusion.core.database.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberfusion.core.database.room.entity.EvidenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EvidenceDao {
    @Query("SELECT * FROM evidence WHERE investigationId = :investigationId ORDER BY timestamp DESC")
    fun getByInvestigationId(investigationId: Long): Flow<List<EvidenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(evidence: EvidenceEntity): Long

    @Query("UPDATE evidence SET description = :description, value = :value WHERE id = :id")
    suspend fun update(id: Long, description: String, value: String)

    @Delete
    suspend fun delete(evidence: EvidenceEntity)
}