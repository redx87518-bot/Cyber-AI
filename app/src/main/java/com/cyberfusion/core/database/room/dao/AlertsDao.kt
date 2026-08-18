package com.cyberfusion.core.database.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberfusion.core.database.room.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertsDao {
    @Query("SELECT * FROM alerts ORDER BY timestamp DESC")
    fun getAll(): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts WHERE id = :id")
    suspend fun getById(id: Long): AlertEntity?

    @Query("SELECT * FROM alerts WHERE status = :status ORDER BY timestamp DESC")
    fun getByStatus(status: String): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: AlertEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(alerts: List<AlertEntity>)

    @Query("UPDATE alerts SET status = :status, aiAnalysis = :aiAnalysis, threatIntelSummary = :threatIntelSummary WHERE id = :id")
    suspend fun update(id: Long, status: String, aiAnalysis: String?, threatIntelSummary: String?)

    @Delete
    suspend fun delete(alert: AlertEntity)

    @Query("DELETE FROM alerts")
    suspend fun deleteAll()
}