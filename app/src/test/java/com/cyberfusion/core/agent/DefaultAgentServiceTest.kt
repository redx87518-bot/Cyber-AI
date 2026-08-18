package com.cyberfusion.core.agent

import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultAgentServiceTest {
    @Test
    fun `AgentPlanStep has correct default values`() {
        val step = AgentPlanStep("1", "Test", "Test Agent")
        assertEquals("1", step.stepId)
        assertEquals("Test", step.description)
        assertEquals("Test Agent", step.agent)
        assertEquals(AgentStepStatus.PENDING, step.status)
    }

    @Test
    fun `AgentPlan can be created with steps`() {
        val plan = AgentPlan(
            steps = listOf(
                AgentPlanStep("1", "Get alerts", "SOC Agent", "getAlerts"),
                AgentPlanStep("2", "Enrich IOC", "Threat Intelligence Agent", "enrichIOC")
            ),
            summary = "Test plan"
        )
        assertEquals(2, plan.steps.size)
        assertEquals("Test plan", plan.summary)
    }

    @Test
    fun `AgentEvent types are valid`() {
        val types = AgentEventType.values()
        assertEquals(true, types.contains(AgentEventType.TASK_CREATED))
        assertEquals(true, types.contains(AgentEventType.TASK_COMPLETED))
        assertEquals(true, types.contains(AgentEventType.TASK_FAILED))
    }
}
