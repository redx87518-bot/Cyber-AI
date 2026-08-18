package com.cyberfusion.core.database.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberfusion.core.database.room.entity.IocEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IocDao {
    @Query("SELECT * FROM iocs ORDER BY createdAt DESC")
    fun getAll(): Flow<List<IocEntity>>

    @Query("SELECT * FROM iocs WHERE id = :id")
    suspend fun getById(id: Long): IocEntity?

    @Query("SELECT * FROM iocs WHERE value = :value LIMIT 1")
    suspend fun getByValue(value: String): IocEntity?

    @Query("SELECT * FROM iocs WHERE type = :type ORDER BY createdAt DESC")
    fun getByType(type: String): Flow<List<IocEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ioc: IocEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(iocs: List<IocEntity>)

    @Query("UPDATE iocs SET reputation = :reputation, confidence = :confidence, severity = :severity, lastSeen = :lastSeen, tags = :tags WHERE id = :id")
    suspend fun update(id: Long, reputation: String?, confidence: Int?, severity: String?, lastSeen: Long?, tags: String?)

    @Delete
    suspend fun delete(ioc: IocEntity)
}