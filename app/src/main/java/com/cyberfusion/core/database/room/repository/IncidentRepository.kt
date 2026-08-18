package com.cyberfusion.core.database.room.repository

import com.cyberfusion.core.database.room.dao.IncidentsDao
import com.cyberfusion.core.database.room.entity.IncidentEntity
import kotlinx.coroutines.flow.Flow

class IncidentRepository(private val incidentsDao: IncidentsDao) {
    val allIncidents: Flow<List<IncidentEntity>> = incidentsDao.getAll()
    fun getByStatus(status: String): Flow<List<IncidentEntity>> = incidentsDao.getByStatus(status)

    suspend fun getById(id: Long): IncidentEntity? = incidentsDao.getById(id)
    suspend fun insert(incident: IncidentEntity): Long = incidentsDao.insert(incident)
    suspend fun update(id: Long, status: String, responseNotes: String?) {
        incidentsDao.update(id, status, responseNotes, System.currentTimeMillis())
    }
    suspend fun delete(incident: IncidentEntity) = incidentsDao.delete(incident)
}