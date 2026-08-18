package com.cyberfusion.core.database.room.repository

import com.cyberfusion.core.database.room.dao.ReportsDao
import com.cyberfusion.core.database.room.entity.ReportEntity
import kotlinx.coroutines.flow.Flow

class ReportRepository(private val reportsDao: ReportsDao) {
    val allReports: Flow<List<ReportEntity>> = reportsDao.getAll()
    fun getByType(type: String): Flow<List<ReportEntity>> = reportsDao.getByType(type)

    suspend fun getById(id: Long): ReportEntity? = reportsDao.getById(id)
    suspend fun insert(report: ReportEntity): Long = reportsDao.insert(report)
    suspend fun insertAll(reports: List<ReportEntity>) = reportsDao.insertAll(reports)
    suspend fun delete(report: ReportEntity) = reportsDao.delete(report)
    suspend fun deleteAll() = reportsDao.deleteAll()
}