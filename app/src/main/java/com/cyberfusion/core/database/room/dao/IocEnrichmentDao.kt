package com.cyberfusion.core.database.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberfusion.core.database.room.entity.IocEnrichmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IocEnrichmentDao {
    @Query("SELECT * FROM ioc_enrichment WHERE iocId = :iocId ORDER BY timestamp DESC")
    fun getByIocId(iocId: Long): Flow<List<IocEnrichmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(enrichment: IocEnrichmentEntity): Long
}