package com.cyberfusion.core.logging

import android.util.Log

object CyberFusionLogger {
    private const val TAG = "CyberFusion"
    private val logs = mutableListOf<String>()
    private var isDebug = true

    fun d(message: String) {
        if (isDebug) {
            Log.d(TAG, message)
            addLog("DEBUG: $message")
        }
    }

    fun i(message: String) {
        Log.i(TAG, message)
        addLog("INFO: $message")
    }

    fun w(message: String) {
        Log.w(TAG, message)
        addLog("WARN: $message")
    }

    fun e(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
        addLog("ERROR: $message${throwable?.let { " - ${it.message}" } ?: ""}")
    }

    fun addLog(message: String) {
        logs.add("${System.currentTimeMillis()}: $message")
        if (logs.size > 1000) {
            logs.removeAt(0)
        }
    }

    fun getLogs(): List<String> {
        return logs.toList()
    }

    fun clearLogs() {
        logs.clear()
    }

    fun exportLogs(): String {
        return logs.joinToString("\n")
    }

    fun setDebug(enabled: Boolean) {
        isDebug = enabled
    }
}
 
 
