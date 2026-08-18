package com.cyberfusion.core.database.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberfusion.core.database.room.entity.ReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportsDao {
    @Query("SELECT * FROM reports ORDER BY createdAt DESC")
    fun getAll(): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE id = :id")
    suspend fun getById(id: Long): ReportEntity?

    @Query("SELECT * FROM reports WHERE type = :type ORDER BY createdAt DESC")
    fun getByType(type: String): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: ReportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reports: List<ReportEntity>)

    @Delete
    suspend fun delete(report: ReportEntity)

    @Query("DELETE FROM reports")
    suspend fun deleteAll()
}