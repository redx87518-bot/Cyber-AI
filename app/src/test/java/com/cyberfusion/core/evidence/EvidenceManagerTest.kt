package com.cyberfusion.core.evidence

import org.junit.Assert.assertEquals
import org.junit.Test

class EvidenceManagerTest {
    @Test
    fun `add and retrieve evidence`() {
        val item = EvidenceItem(
            id = "1",
            taskId = "task1",
            type = "tool_output",
            source = "getAlerts",
            content = "Test evidence",
            confidence = 80.0,
            verified = true
        )
        EvidenceManager.addEvidence("task1", item)
        val retrieved = EvidenceManager.getEvidence("task1")
        assertEquals(1, retrieved.size)
        assertEquals("Test evidence", retrieved[0].content)
    }

    @Test
    fun `calculate confidence with verified evidence`() {
        EvidenceManager.clearTask("task2")
        val item1 = EvidenceItem(id = "1", taskId = "task2", type = "tool_output", source = "test", content = "test", confidence = 80.0, verified = true)
        val item2 = EvidenceItem(id = "2", taskId = "task2", type = "tool_output", source = "test", content = "test", confidence = 60.0, verified = false)
        EvidenceManager.addEvidence("task2", item1)
        EvidenceManager.addEvidence("task2", item2)
        val confidence = EvidenceManager.calculateTaskConfidence("task2")
        assertEquals(true, confidence > 0)
    }

    @Test
    fun `clear task removes all evidence`() {
        EvidenceManager.clearTask("task3")
        val item = EvidenceItem(id = "1", taskId = "task3", type = "tool_output", source = "test", content = "test", confidence = 80.0, verified = true)
        EvidenceManager.addEvidence("task3", item)
        EvidenceManager.clearTask("task3")
        assertEquals(0, EvidenceManager.getEvidence("task3").size)
    }
}
