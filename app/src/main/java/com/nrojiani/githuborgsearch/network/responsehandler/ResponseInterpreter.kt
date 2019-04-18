package com.nrojiani.githuborgsearch.network.responsehandler

import retrofit2.Response

/**
 * Responsible for interpreting a Retrofit Response.
 */
class ResponseInterpreter<T> {
    /**
     * Categorize the Response as success or error.
     */
    fun interpret(response: Response<T>): ApiResult<T> {
        val httpStatus = HttpStatus(response.code(), response.raw().message())
        val body = response.body()

        return when {
            body == null -> ApiResult.Error(httpStatus)
            response.isSuccessful -> ApiResult.Success(body, httpStatus)
            else -> ApiResult.Error(httpStatus)
        }
    }
}