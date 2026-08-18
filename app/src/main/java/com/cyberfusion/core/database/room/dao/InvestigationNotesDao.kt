package com.cyberfusion.core.database.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberfusion.core.database.room.entity.InvestigationNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestigationNotesDao {
    @Query("SELECT * FROM investigation_notes WHERE investigationId = :investigationId ORDER BY timestamp DESC")
    fun getByInvestigationId(investigationId: Long): Flow<List<InvestigationNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: InvestigationNoteEntity): Long

    @Delete
    suspend fun delete(note: InvestigationNoteEntity)

    @Query("DELETE FROM investigation_notes WHERE investigationId = :investigationId")
    suspend fun deleteByInvestigationId(investigationId: Long)
}