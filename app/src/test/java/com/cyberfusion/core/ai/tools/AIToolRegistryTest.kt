package com.cyberfusion.core.ai.tools

import org.junit.Assert.assertEquals
import org.junit.Test

class AIToolRegistryTest {
    @Test
    fun `detectIocType returns ip for IPv4`() {
        val result = AIToolRegistry.detectIocType("192.168.1.1")
        assertEquals("ip", result)
    }

    @Test
    fun `detectIocType returns hash for hex string`() {
        val result = AIToolRegistry.detectIocType("d41d8cd98f00b204e9800998ecf8427e")
        assertEquals("hash", result)
    }

    @Test
    fun `detectIocType returns cve for CVE prefix`() {
        val result = AIToolRegistry.detectIocType("CVE-2024-1234")
        assertEquals("cve", result)
    }

    @Test
    fun `detectIocType returns email for email address`() {
        val result = AIToolRegistry.detectIocType("test@example.com")
        assertEquals("email", result)
    }

    @Test
    fun `detectIocType returns url for http prefix`() {
        val result = AIToolRegistry.detectIocType("http://example.com")
        assertEquals("url", result)
    }

    @Test
    fun `detectIocType returns domain for domain string`() {
        val result = AIToolRegistry.detectIocType("example.com")
        assertEquals("domain", result)
    }

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
}
