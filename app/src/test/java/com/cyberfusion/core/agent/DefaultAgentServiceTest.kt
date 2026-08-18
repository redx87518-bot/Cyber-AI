package com.cyberfusion.core.agent

import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultAgentServiceTest {
    @Test
    fun `buildPlan adds alert steps for alert queries`() {
        val plan = buildPlan("Analyze alerts")
        assertEquals(true, plan.steps.any { it.tool == "getAlerts" })
    }

    @Test
    fun `buildPlan adds IOC enrichment for IP queries`() {
        val plan = buildPlan("Investigate IP 8.8.8.8")
        assertEquals(true, plan.steps.any { it.tool == "enrichIOC" })
    }

    @Test
    fun `buildPlan adds malware check for hash queries`() {
        val plan = buildPlan("Check hash d41d8cd98f00b204e9800998ecf8427e")
        assertEquals(true, plan.steps.any { it.tool == "queryMalwareBazaar" })
    }

    @Test
    fun `buildPlan adds CVE lookup for CVE queries`() {
        val plan = buildPlan("Look up CVE-2024-1234")
        assertEquals(true, plan.steps.any { it.tool == "getNvdCve" })
    }

    @Test
    fun `buildPlan adds risk steps for GRC queries`() {
        val plan = buildPlan("Assess GRC risks")
        assertEquals(true, plan.steps.any { it.tool == "getRisks" })
    }

    @Test
    fun `buildPlan falls back to analysis for unknown queries`() {
        val plan = buildPlan("Hello world")
        assertEquals(1, plan.steps.size)
        assertEquals("Analyze request", plan.steps[0].description)
    }
}
