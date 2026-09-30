package com.cyberfusion.core.database.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberfusion.core.database.room.entity.AgentMemoryEntity
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

    // ── Agent memory ────────────────────────────────────────────────────────────

    @Query(
        """SELECT * FROM agent_memories
        ORDER BY
            CASE WHEN relatedIoc IS NOT NULL AND relatedIoc = :ioc THEN 0 ELSE 1 END,
            importance DESC,
            lastAccessedAt DESC
        LIMIT :limit"""
    )
    suspend fun recallMemories(ioc: String?, limit: Int): List<AgentMemoryEntity>

    @Query(
        """SELECT * FROM agent_memories
        ORDER BY importance DESC, lastAccessedAt DESC
        LIMIT :limit"""
    )
    suspend fun recallTopMemories(limit: Int): List<AgentMemoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: AgentMemoryEntity): Long

    @Query("UPDATE agent_memories SET lastAccessedAt = :now, accessCount = accessCount + 1 WHERE id = :id")
    suspend fun touchMemory(id: Long, now: Long)

    @Query("DELETE FROM agent_memories WHERE lastAccessedAt < :before AND importance < 6")
    suspend fun pruneMemories(before: Long)

    @Query("SELECT COUNT(id) FROM agent_memories")
    suspend fun countMemories(): Int
}