package com.cyberfusion.core.database.room.repository

import com.cyberfusion.core.database.room.dao.AiDao
import com.cyberfusion.core.database.room.entity.AiTaskEntity
import com.cyberfusion.core.database.room.entity.AiTaskHistoryEntity
import com.cyberfusion.core.database.room.entity.AiToolCallEntity
import kotlinx.coroutines.flow.Flow

class AiRepository(private val aiDao: AiDao) {
    val allTasks: Flow<List<AiTaskEntity>> = aiDao.getAllTasks()

    suspend fun getTaskById(id: Long): AiTaskEntity? = aiDao.getTaskById(id)
    suspend fun insertTask(task: AiTaskEntity): Long = aiDao.insertTask(task)
    suspend fun updateTaskStatus(id: Long, status: String, resultSummary: String?) =
        aiDao.updateTaskStatus(id, status, resultSummary)

    fun getHistoryByTaskId(taskId: Long): Flow<List<AiTaskHistoryEntity>> = aiDao.getHistoryByTaskId(taskId)
    suspend fun insertHistory(history: AiTaskHistoryEntity): Long = aiDao.insertHistory(history)

    fun getToolCallsByTaskId(taskId: Long): Flow<List<AiToolCallEntity>> = aiDao.getToolCallsByTaskId(taskId)
    suspend fun insertToolCall(toolCall: AiToolCallEntity): Long = aiDao.insertToolCall(toolCall)
}