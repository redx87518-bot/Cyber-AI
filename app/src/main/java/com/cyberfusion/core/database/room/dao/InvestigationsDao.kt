package com.cyberfusion.core.database.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberfusion.core.database.room.entity.InvestigationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestigationsDao {
    @Query("SELECT * FROM investigations ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<InvestigationEntity>>

    @Query("SELECT * FROM investigations WHERE id = :id")
    suspend fun getById(id: Long): InvestigationEntity?

    @Query("SELECT * FROM investigations WHERE status = :status ORDER BY updatedAt DESC")
    fun getByStatus(status: String): Flow<List<InvestigationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(investigation: InvestigationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(investigations: List<InvestigationEntity>)

    @Query("UPDATE investigations SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, updatedAt: Long)

    @Delete
    suspend fun delete(investigation: InvestigationEntity)

    @Query("DELETE FROM investigations")
    suspend fun deleteAll()
}