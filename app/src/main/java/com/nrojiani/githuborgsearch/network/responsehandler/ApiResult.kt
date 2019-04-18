package com.nrojiani.githuborgsearch.network.responsehandler

/**
 * Possible outcomes of a network API call.
 */
sealed class ApiResult<out T> {
    data class Success<T>(val responseBody: T, val httpStatus: HttpStatus) : ApiResult<T>()
    data class Exception(val throwable: Throwable) : ApiResult<Nothing>()
    data class Error(val httpStatus: HttpStatus, val errorMessage: String = httpStatus.message) : ApiResult<Nothing>()
}