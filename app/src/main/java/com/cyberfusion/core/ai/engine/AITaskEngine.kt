package com.cyberfusion.core.ai.engine

import com.cyberfusion.core.ai.provider.AIProvider
import com.cyberfusion.core.ai.provider.AIProviderConfig
import com.cyberfusion.core.ai.provider.AIProviderFactory
import com.cyberfusion.core.ai.tools.AIToolRegistry
import com.cyberfusion.core.ai.tools.ToolRepositories
import com.cyberfusion.core.database.room.entity.AiTaskEntity
import com.cyberfusion.core.database.room.entity.AiTaskHistoryEntity
import com.cyberfusion.core.database.room.entity.AiToolCallEntity
import com.cyberfusion.core.database.room.repository.AiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AITaskEngine(private val aiRepository: AiRepository) {
    suspend fun executeTask(
        prompt: String,
        provider: AIProvider?,
        repositories: ToolRepositories
    ): Flow<AIResult> = flow {
        emit(AIResult.Processing("Starting task..."))
        
        val taskId = aiRepository.insertTask(
            AiTaskEntity(prompt = prompt, provider = provider?.id, status = "processing")
        )

        emit(AIResult.Processing("Analyzing request..."))

        try {
            if (provider == null) {
                emit(AIResult.Error("No AI provider configured"))
                aiRepository.updateTaskStatus(taskId, "failed", "No provider configured")
                return@flow
            }

            val config = provider as? AIProviderConfig
                ?: return@flow.also { emit(AIResult.Error("Invalid provider configuration")) }
            val adapter = AIProviderFactory().create(config)
            
            emit(AIResult.Processing("Executing tools..."))
            
            val toolResult = AIToolRegistry.executeTool("getAlerts", emptyMap(), repositories)
            aiRepository.insertToolCall(
                AiToolCallEntity(taskId = taskId, toolName = "getAlerts", parameters = "{}", result = toolResult.result, timestamp = System.currentTimeMillis())
            )

            emit(AIResult.Success("Task completed. Found alerts."))
            aiRepository.updateTaskStatus(taskId, "completed", "Task completed successfully")
            aiRepository.insertHistory(
                AiTaskHistoryEntity(taskId = taskId, provider = provider.id, resultStatus = "success", summarizedResult = "Task completed", timestamp = System.currentTimeMillis())
            )
        } catch (e: Exception) {
            emit(AIResult.Error(e.message ?: "Unknown error"))
            aiRepository.updateTaskStatus(taskId, "failed", e.message)
        }
    }

    suspend fun testConnection(config: AIProviderConfig): Boolean {
        return try {
            val adapter = AIProviderFactory().create(config)
            adapter.validateKey()
        } catch (e: Exception) {
            false
        }
    }
}

sealed interface AIResult {
    data class Processing(val message: String) : AIResult
    data class Success(val result: String) : AIResult
    data class Error(val message: String) : AIResult
}