package com.cyberfusion.core.agent

data class ToolExecutionResult(
    val success: Boolean,
    val result: String? = null,
    val error: String? = null
)
