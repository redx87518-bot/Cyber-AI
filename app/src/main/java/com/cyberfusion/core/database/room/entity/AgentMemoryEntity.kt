package com.cyberfusion.core.database.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Long-term memory for the CyberFusion agent.
 *
 * Memories are concise, reusable facts the agent (Rax AI) learned from past
 * investigations — IOC reputations, infrastructure links, methodology notes,
 * user preferences. They are recalled by recency/importance/IOC relevance and
 * injected into the reasoning loop so the agent gets smarter over time.
 */
@Entity(
    tableName = "agent_memories",
    indices = [Index("relatedIoc"), Index("lastAccessedAt")]
)
data class AgentMemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val kind: String,                    // ioc_reputation | infrastructure | methodology | user_preference
    val content: String,                 // the fact, kept concise
    val importance: Int = 5,             // 1..10 — drives recall order and pruning
    val relatedIoc: String? = null,      // enables IOC-scoped recall
    val createdAt: Long = System.currentTimeMillis(),
    val lastAccessedAt: Long = System.currentTimeMillis(),
    val accessCount: Int = 0
)
