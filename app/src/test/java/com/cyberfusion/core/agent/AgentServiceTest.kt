package com.cyberfusion.core.agent

import org.junit.Assert.assertEquals
import org.junit.Test

class AgentServiceTest {
    @Test
    fun `AgentStatus has all expected values`() {
        val values = AgentStatus.values()
        assertEquals(5, values.size)
        assertEquals(AgentStatus.PENDING, values[0])
        assertEquals(AgentStatus.COMPLETED, values[2])
    }

    @Test
    fun `AgentPlanStep default status is PENDING`() {
        val step = AgentPlanStep("1", "Test", "Test Agent")
        assertEquals(AgentStepStatus.PENDING, step.status)
    }

    @Test
    fun `AgentReport filePath is nullable`() {
        val report = AgentReport(
            reportId = "RPT-1",
            title = "Test",
            summary = "Summary",
            findings = emptyList(),
            evidence = emptyList(),
            recommendations = emptyList()
        )
        assertEquals(null, report.filePath)
    }

    @Test
    fun `AgentEvent timestamp defaults to now`() {
        val event = AgentEvent("task-1", AgentEventType.TASK_CREATED, "Orchestrator", status = AgentStepStatus.RUNNING)
        assert(event.timestamp > 0)
    }
}
