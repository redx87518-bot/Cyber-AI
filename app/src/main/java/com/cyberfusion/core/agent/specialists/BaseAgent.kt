package com.cyberfusion.core.agent.specialists
import com.cyberfusion.core.agent.AgentPlanStep
import com.cyberfusion.core.agent.ToolExecutionResult

import com.cyberfusion.core.ai.tools.ToolRepositories
import com.cyberfusion.core.ai.provider.AIProviderConfig

abstract class BaseAgent(
    open val name: String,
    open val allowedTools: List<String>
) {
    open suspend fun execute(step: AgentPlanStep, provider: AIProviderConfig, repositories: ToolRepositories): ToolExecutionResult {
        return ToolExecutionResult(false, error = "Not implemented")
    }
    
    open fun canExecute(tool: String): Boolean {
        return allowedTools.contains(tool)
    }
}
