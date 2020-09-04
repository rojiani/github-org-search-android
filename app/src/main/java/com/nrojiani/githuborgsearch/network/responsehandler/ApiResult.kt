package com.nrojiani.githuborgsearch.network.responsehandler

/**
 * Represents a network API call.
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T, val httpStatus: HttpStatus) : ApiResult<T>()
    data class Exception(val throwable: Throwable) : ApiResult<Nothing>()
    data class Error(val httpStatus: HttpStatus, val errorMessage: String = httpStatus.message) :
        ApiResult<Nothing>()

    object Loading : ApiResult<Nothing>()
    object Cancelled : ApiResult<Nothing>()
}

/**
 * Generate an error message (for applicable types).
 */
val <T> ApiResult<T>?.formattedErrorMessage: String?
    get() = when (this) {
        is ApiResult.Error -> buildString {
            val (errMsg, code) = errorMessage to httpStatus.code
            append("Error: ")
            val msg = if (errMsg.isBlank()) "Unknown Error ($code)"
            else "$errMsg ($code)"
            append(msg)
        }
        is ApiResult.Exception -> buildString {
            val detailMessage = throwable.localizedMessage
            val message = if (detailMessage.isNullOrBlank()) throwable::class.simpleName
            else detailMessage
            append("Exception: $message")
        }
        else -> null
    }

/**
 * Check if the ApiResult is a finished type (i.e., an HTTP response was received).
 */
val <T> ApiResult<T>?.isCompleted: Boolean
    get() = (this != null) &&
        (this != ApiResult.Loading) &&
        (this != ApiResult.Cancelled) &&
        (this !is ApiResult.Exception)

/**
 * Return [ApiResult.Success.data] if the [ApiResult] type is [ApiResult.Success], or
 * null otherwise.
 */
val <T> ApiResult<T>?.responseData: T?
    get() = when (this) {
        is ApiResult.Success<T> -> data
        else -> null
    }
