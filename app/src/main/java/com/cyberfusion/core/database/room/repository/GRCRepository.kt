package com.cyberfusion.core.database.room.repository

import com.cyberfusion.core.database.room.dao.GRCDao
import com.cyberfusion.core.database.room.entity.*
import kotlinx.coroutines.flow.Flow

class GRCRepository(private val grcDao: GRCDao) {
    val allRisks: Flow<List<RiskEntity>> = grcDao.getAllRisks()
    suspend fun getRiskById(id: Long): RiskEntity? = grcDao.getRiskById(id)
    suspend fun insertRisk(risk: RiskEntity): Long = grcDao.insertRisk(risk)
    suspend fun updateRiskStatus(id: Long, status: String) = grcDao.updateRiskStatus(id, status, System.currentTimeMillis())
    suspend fun deleteRisk(risk: RiskEntity) = grcDao.deleteRisk(risk)

    val allControls: Flow<List<ControlEntity>> = grcDao.getAllControls()
    suspend fun insertControl(control: ControlEntity): Long = grcDao.insertControl(control)

    val allFrameworks: Flow<List<FrameworkEntity>> = grcDao.getAllFrameworks()
    suspend fun insertFramework(framework: FrameworkEntity): Long = grcDao.insertFramework(framework)

    val allRemediationTasks: Flow<List<RemediationTaskEntity>> = grcDao.getAllRemediationTasks()
    fun getRemediationTasksByRisk(riskId: Long): Flow<List<RemediationTaskEntity>> = grcDao.getRemediationTasksByRisk(riskId)
    suspend fun insertRemediationTask(task: RemediationTaskEntity): Long = grcDao.insertRemediationTask(task)
    suspend fun updateRemediationTaskStatus(id: Long, status: String) = grcDao.updateRemediationTaskStatus(id, status, System.currentTimeMillis())
}