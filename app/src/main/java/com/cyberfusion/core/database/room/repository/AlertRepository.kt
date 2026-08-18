package com.cyberfusion.core.database.room.repository

import com.cyberfusion.core.database.room.dao.AlertsDao
import com.cyberfusion.core.database.room.entity.AlertEntity
import kotlinx.coroutines.flow.Flow

class AlertRepository(private val alertsDao: AlertsDao) {
    val allAlerts: Flow<List<AlertEntity>> = alertsDao.getAll()
    fun getByStatus(status: String): Flow<List<AlertEntity>> = alertsDao.getByStatus(status)

    suspend fun getById(id: Long): AlertEntity? = alertsDao.getById(id)
    suspend fun insert(alert: AlertEntity): Long = alertsDao.insert(alert)
    suspend fun insertAll(alerts: List<AlertEntity>) = alertsDao.insertAll(alerts)
    suspend fun update(id: Long, status: String, aiAnalysis: String?, threatIntelSummary: String?) =
        alertsDao.update(id, status, aiAnalysis, threatIntelSummary)
    suspend fun delete(alert: AlertEntity) = alertsDao.delete(alert)
    suspend fun deleteAll() = alertsDao.deleteAll()
}