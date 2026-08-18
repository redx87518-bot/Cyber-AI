package com.cyberfusion.core.logging

object CyberFusionLogger {
    private const val TAG = "CyberFusion"
    
    fun d(message: String) {
        android.util.Log.d(TAG, sanitize(message))
    }
    
    fun e(message: String, throwable: Throwable? = null) {
        android.util.Log.e(TAG, sanitize(message), throwable)
    }
    
    fun i(message: String) {
        android.util.Log.i(TAG, sanitize(message))
    }
    
    fun w(message: String) {
        android.util.Log.w(TAG, sanitize(message))
    }
    
    private fun sanitize(message: String): String {
        var sanitized = message
        val patterns = listOf(
            Regex(r"(api[_-]?key|apikey|secret|token|password|private[_-]?key)\s*[=:]\s*[\"']?[A-Za-z0-9_\-]{8,}", RegexOption.IGNORE_CASE),
            Regex(r"(key|token|secret)\s*[=:]\s*[\"']?[A-Za-z0-9_\-]{8,}", RegexOption.IGNORE_CASE)
        )
        patterns.forEach { pattern ->
            sanitized = pattern.replace(sanitized) { "***REDACTED***" }
        }
        return sanitized
    }
}