package com.cyberfusion.core.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

fun <T> Flow<T>.safeCollect(onError: (Throwable) -> Unit = {}): Flow<T> = catch { e ->
    com.cyberfusion.core.logging.CyberFusionLogger.e("Flow error", e)
    onError(e)
    throw e
}