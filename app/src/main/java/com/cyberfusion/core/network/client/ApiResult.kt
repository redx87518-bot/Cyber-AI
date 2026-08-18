package com.cyberfusion.core.network.client

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : ApiResult<Nothing>()
    data object RateLimited : ApiResult<Nothing>()
    data object Unauthorized : ApiResult<Nothing>()
    data object NotFound : ApiResult<Nothing>()
}