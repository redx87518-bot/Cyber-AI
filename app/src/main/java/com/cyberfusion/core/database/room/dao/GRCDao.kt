package com.cyberfusion.core.database.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberfusion.core.database.room.entity.ControlEntity
import com.cyberfusion.core.database.room.entity.FrameworkEntity
import com.cyberfusion.core.database.room.entity.RemediationTaskEntity
import com.cyberfusion.core.database.room.entity.RiskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GRCDao {
    // Risks
    @Query("SELECT * FROM risks ORDER BY riskScore DESC")
    fun getAllRisks(): Flow<List<RiskEntity>>

    @Query("SELECT * FROM risks WHERE id = :id")
    suspend fun getRiskById(id: Long): RiskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRisk(risk: RiskEntity): Long

    @Query("UPDATE risks SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateRiskStatus(id: Long, status: String, updatedAt: Long)

    @Delete
    suspend fun deleteRisk(risk: RiskEntity)

    // Controls
    @Query("SELECT * FROM controls ORDER BY createdAt DESC")
    fun getAllControls(): Flow<List<ControlEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertControl(control: ControlEntity): Long

    // Frameworks
    @Query("SELECT * FROM frameworks ORDER BY name ASC")
    fun getAllFrameworks(): Flow<List<FrameworkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFramework(framework: FrameworkEntity): Long

    // Remediation Tasks
    @Query("SELECT * FROM remediation_tasks ORDER BY targetDate ASC")
    fun getAllRemediationTasks(): Flow<List<RemediationTaskEntity>>

    @Query("SELECT * FROM remediation_tasks WHERE riskId = :riskId")
    fun getRemediationTasksByRisk(riskId: Long): Flow<List<RemediationTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemediationTask(task: RemediationTaskEntity): Long

    @Query("UPDATE remediation_tasks SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateRemediationTaskStatus(id: Long, status: String, updatedAt: Long)
}