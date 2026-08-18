package com.cyberfusion.core.database.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberfusion.core.database.room.entity.InvestigationTimelineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestigationTimelineDao {
    @Query("SELECT * FROM investigation_timeline WHERE investigationId = :investigationId ORDER BY timestamp DESC")
    fun getByInvestigationId(investigationId: Long): Flow<List<InvestigationTimelineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: InvestigationTimelineEntity): Long

    @Delete
    suspend fun delete(event: InvestigationTimelineEntity)
}