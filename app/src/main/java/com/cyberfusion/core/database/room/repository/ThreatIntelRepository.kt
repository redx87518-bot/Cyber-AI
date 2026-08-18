package com.cyberfusion.core.database.room.repository

import com.cyberfusion.core.database.room.dao.ThreatIntelDao
import com.cyberfusion.core.database.room.entity.ThreatIntelligenceEntity
import kotlinx.coroutines.flow.Flow

class ThreatIntelRepository(private val threatIntelDao: ThreatIntelDao) {
    fun getByIocId(iocId: Long): Flow<List<ThreatIntelligenceEntity>> = threatIntelDao.getByIocId(iocId)
    fun getAll(limit: Int = 100): Flow<List<ThreatIntelligenceEntity>> = threatIntelDao.getAll(limit)

    suspend fun insert(intel: ThreatIntelligenceEntity): Long = threatIntelDao.insert(intel)
    suspend fun insertAll(intel: List<ThreatIntelligenceEntity>) = threatIntelDao.insertAll(intel)
}