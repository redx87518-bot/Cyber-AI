package com.cyberfusion.core.agent.specialists

import com.cyberfusion.core.agent.AgentPlanStep
import com.cyberfusion.core.agent.AgentStepStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class SpecialistAgentsTest {
    @Test
    fun `SOCAgent can execute SOC tools`() {
        val agent = SOCAgent()
        assertEquals(true, agent.canExecute("getAlerts"))
        assertEquals(true, agent.canExecute("getInvestigations"))
        assertEquals(false, agent.canExecute("queryMalwareBazaar"))
    }

    @Test
    fun `ThreatIntelligenceAgent can execute threat intel tools`() {
        val agent = ThreatIntelligenceAgent()
        assertEquals(true, agent.canExecute("enrichIOC"))
        assertEquals(true, agent.canExecute("checkAbuseIPDB"))
        assertEquals(true, agent.canExecute("dnsLookup"))
        assertEquals(false, agent.canExecute("getAlerts"))
    }

    @Test
    fun `GRCAgent can execute GRC tools`() {
        val agent = GRCAgent()
        assertEquals(true, agent.canExecute("getRisks"))
        assertEquals(true, agent.canExecute("iso27001Lookup"))
        assertEquals(false, agent.canExecute("getAlerts"))
    }

    @Test
    fun `VulnerabilityAgent can execute vulnerability tools`() {
        val agent = VulnerabilityAgent()
        assertEquals(true, agent.canExecute("getNvdCve"))
        assertEquals(true, agent.canExecute("mitreLookup"))
        assertEquals(false, agent.canExecute("getAlerts"))
    }

    @Test
    fun `ReportAgent can execute report tools`() {
        val agent = ReportAgent()
        assertEquals(true, agent.canExecute("generateReport"))
        assertEquals(true, agent.canExecute("saveReport"))
        assertEquals(false, agent.canExecute("getAlerts"))
    }

    @Test
    fun `AgentPlanStep defaults are correct`() {
        val step = AgentPlanStep("1", "Test", "Test Agent")
        assertEquals(AgentStepStatus.PENDING, step.status)
        assertEquals(emptyMap<String, String>(), step.input)
    }
}
