package com.cyberfusion.core.database.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberfusion.core.database.room.entity.IncidentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentsDao {
    @Query("SELECT * FROM incidents ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<IncidentEntity>>

    @Query("SELECT * FROM incidents WHERE id = :id")
    suspend fun getById(id: Long): IncidentEntity?

    @Query("SELECT * FROM incidents WHERE status = :status ORDER BY updatedAt DESC")
    fun getByStatus(status: String): Flow<List<IncidentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(incident: IncidentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(incidents: List<IncidentEntity>)

    @Query("UPDATE incidents SET status = :status, responseNotes = :responseNotes, updatedAt = :updatedAt WHERE id = :id")
    suspend fun update(id: Long, status: String, responseNotes: String?, updatedAt: Long)

    @Delete
    suspend fun delete(incident: IncidentEntity)
}