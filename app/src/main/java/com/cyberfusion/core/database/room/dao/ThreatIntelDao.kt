package com.cyberfusion.core.database.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberfusion.core.database.room.entity.ThreatIntelligenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ThreatIntelDao {
    @Query("SELECT * FROM threat_intelligence WHERE iocId = :iocId ORDER BY createdAt DESC")
    fun getByIocId(iocId: Long): Flow<List<ThreatIntelligenceEntity>>

    @Query("SELECT * FROM threat_intelligence ORDER BY createdAt DESC LIMIT :limit")
    fun getAll(limit: Int = 100): Flow<List<ThreatIntelligenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(intel: ThreatIntelligenceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(intel: List<ThreatIntelligenceEntity>)
}