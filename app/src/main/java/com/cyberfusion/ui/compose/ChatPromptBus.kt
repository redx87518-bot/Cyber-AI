package com.cyberfusion.ui.compose

/**
 * Simple process-wide hand-off for prompts queued from other screens
 * (e.g. the Lab Detail "Ask the AI mentor" button) to the AI chat tab.
 * The chat screen consumes (and clears) the pending prompt on resume.
 */
object ChatPromptBus {
    @Volatile
    private var pending: String? = null

    fun queue(prompt: String) {
        pending = prompt
    }

    fun consume(): String? = pending.also { pending = null }
}
