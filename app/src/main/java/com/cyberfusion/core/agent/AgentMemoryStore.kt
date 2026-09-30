package com.cyberfusion.core.agent

import android.util.Base64
import com.cyberfusion.core.database.room.dao.AiDao
import com.cyberfusion.core.database.room.entity.AgentMemoryEntity
import org.json.JSONArray
import org.json.JSONObject

/**
 * Persistent long-term memory for the agent, backed by Room.
 *
 * - [recall]    returns high-importance + IOC-relevant memories as a prompt block
 * - [remember]  persists concise facts extracted from Rax AI responses (JSON-marked)
 * - extraction looks for a trailing ```memory block``` in the model output:
 *
 * ```memory
 * [{"kind":"ioc_reputation","content":"45.33.32.156 is a clean Linode host","importance":6,"relatedIoc":"45.33.32.156"}]
 * ```
 */
class AgentMemoryStore(private val aiDao: AiDao) {

    suspend fun recall(iocHint: String? = null, limit: Int = 12): String {
        val memories = if (iocHint.isNullOrBlank()) {
            aiDao.recallTopMemories(limit)
        } else {
            aiDao.recallMemories(iocHint, limit)
        }
        if (memories.isEmpty()) return ""

        memories.forEach { aiDao.touchMemory(it.id, System.currentTimeMillis()) }

        return buildString {
            appendLine("## Long-term memory (from previous investigations)")
            memories.forEach { m ->
                appendLine("- [${m.kind}] ${m.content}")
            }
        }.trimEnd()
    }

    suspend fun remember(rawModelOutput: String, currentIoc: String?) {
        val memories = extractMemories(rawModelOutput, currentIoc)
        memories.forEach { aiDao.insertMemory(it) }
        if (memories.isNotEmpty()) {
            // Keep the store lean; low-importance, stale entries are dropped.
            runCatching { aiDao.pruneMemories(System.currentTimeMillis() - PRUNE_AFTER_MS) }
        }
    }

    suspend fun count(): Int = aiDao.countMemories()

    private fun extractMemories(output: String, currentIoc: String?): List<AgentMemoryEntity> {
        val block = MEMORY_BLOCK_REGEX.find(output)?.groupValues?.get(1) ?: return emptyList()
        return try {
            val array = JSONArray(block.trim())
            (0 until array.length()).mapNotNull { i ->
                val obj = array.optJSONObject(i) ?: return@mapNotNull null
                val content = obj.optString("content").takeIf { it.isNotBlank() } ?: return@mapNotNull null
                AgentMemoryEntity(
                    kind = obj.optString("kind", "methodology").take(40),
                    content = content.take(400),
                    importance = obj.optInt("importance", 5).coerceIn(1, 10),
                    relatedIoc = obj.optString("relatedIoc", currentIoc ?: "").takeIf { it.isNotBlank() }
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    companion object {
        private val MEMORY_BLOCK_REGEX =
            Regex("```memory\\s*([\\s\\S]*?)```", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))

        /** System-prompt instruction teaching the model how to save memories. */
        const val MEMORY_PROTOCOL: String = """
You have persistent long-term memory. At the END of your final answer, if and only if you
learned durable facts worth remembering (IOC reputations, infrastructure links, user
preferences, methodology improvements), append one fenced block:

```memory
[{"kind":"ioc_reputation|infrastructure|methodology|user_preference","content":"<one concise fact>","importance":1-10,"relatedIoc":"<ip/domain/hash or null>"}]
```

Rules: save only durable facts (not one-off tool outputs), keep each under 400 chars,
importance 8-10 for confirmed malicious infrastructure, 4-6 for methodology/preferences.
"""

        private const val PRUNE_AFTER_MS = 1000L * 60 * 60 * 24 * 30 // 30 days
    }
}
