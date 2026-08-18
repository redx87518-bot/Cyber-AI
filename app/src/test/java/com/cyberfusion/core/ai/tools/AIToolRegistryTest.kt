package com.cyberfusion.core.ai.tools

import org.junit.Assert.assertEquals
import org.junit.Test

class AIToolRegistryTest {
    @Test
    fun `tool registry contains expected tools`() {
        val toolNames = AIToolRegistry.tools.map { it.name }
        assertEquals(true, toolNames.contains("getAlerts"))
        assertEquals(true, toolNames.contains("enrichIOC"))
        assertEquals(true, toolNames.contains("queryMalwareBazaar"))
        assertEquals(true, toolNames.contains("checkAbuseIPDB"))
        assertEquals(true, toolNames.contains("searchThreatFox"))
        assertEquals(true, toolNames.contains("getNvdCve"))
    }

    @Test
    fun `tool registry has at least 10 tools`() {
        assertEquals(true, AIToolRegistry.tools.size >= 10)
    }
}
