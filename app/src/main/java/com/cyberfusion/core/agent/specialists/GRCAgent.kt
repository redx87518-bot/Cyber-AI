package com.cyberfusion.core.agent.specialists
import com.cyberfusion.core.agent.ToolExecutionResult

import com.cyberfusion.core.agent.AgentPlanStep
import com.cyberfusion.core.ai.tools.AIToolRegistry
import com.cyberfusion.core.ai.tools.ToolRepositories
import com.cyberfusion.core.ai.provider.AIProviderConfig

class GRCAgent : BaseAgent(
    name = "GRCAgent",
    allowedTools = listOf("getRisks", "createRisk", "getControls", "iso27001Lookup")
) {
    override suspend fun execute(step: AgentPlanStep, provider: AIProviderConfig, repositories: ToolRepositories): ToolExecutionResult {
        return try {
            val result = AIToolRegistry.executeTool(step.tool ?: return ToolExecutionResult(false, error = "No tool specified"), step.input, repositories)
            ToolExecutionResult(result.success, result.result, result.error)
        } catch (e: Exception) {
            ToolExecutionResult(false, error = e.message)
        }
    }
}
