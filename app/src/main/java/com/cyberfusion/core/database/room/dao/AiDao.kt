package com.cyberfusion.core.database.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberfusion.core.database.room.entity.AiTaskEntity
import com.cyberfusion.core.database.room.entity.AiTaskHistoryEntity
import com.cyberfusion.core.database.room.entity.AiToolCallEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiDao {
    @Query("SELECT * FROM ai_tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<AiTaskEntity>>

    @Query("SELECT * FROM ai_tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): AiTaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: AiTaskEntity): Long

    @Query("UPDATE ai_tasks SET status = :status, resultSummary = :resultSummary WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, status: String, resultSummary: String?)

    @Query("SELECT * FROM ai_task_history WHERE taskId = :taskId ORDER BY timestamp ASC")
    fun getHistoryByTaskId(taskId: Long): Flow<List<AiTaskHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: AiTaskHistoryEntity): Long

    @Query("SELECT * FROM ai_tool_calls WHERE taskId = :taskId ORDER BY timestamp ASC")
    fun getToolCallsByTaskId(taskId: Long): Flow<List<AiToolCallEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToolCall(toolCall: AiToolCallEntity): Long
}