package com.cyberfusion.core.database.room.repository

import com.cyberfusion.core.database.room.dao.IocDao
import com.cyberfusion.core.database.room.dao.IocEnrichmentDao
import com.cyberfusion.core.database.room.entity.IocEntity
import com.cyberfusion.core.database.room.entity.IocEnrichmentEntity
import kotlinx.coroutines.flow.Flow

class IocRepository(private val iocDao: IocDao, private val enrichmentDao: IocEnrichmentDao) {
    val allIocs: Flow<List<IocEntity>> = iocDao.getAll()
    fun getByType(type: String): Flow<List<IocEntity>> = iocDao.getByType(type)

    suspend fun getById(id: Long): IocEntity? = iocDao.getById(id)
    suspend fun getByValue(value: String): IocEntity? = iocDao.getByValue(value)
    suspend fun insert(ioc: IocEntity): Long = iocDao.insert(ioc)
    suspend fun update(id: Long, reputation: String?, confidence: Int?, severity: String?, lastSeen: Long?, tags: String?) =
        iocDao.update(id, reputation, confidence, severity, lastSeen, tags)
    suspend fun delete(ioc: IocEntity) = iocDao.delete(ioc)

    fun getEnrichments(iocId: Long): Flow<List<IocEnrichmentEntity>> = enrichmentDao.getByIocId(iocId)
    suspend fun addEnrichment(enrichment: IocEnrichmentEntity): Long = enrichmentDao.insert(enrichment)
}