package com.nrojiani.githuborgsearch.network.responsehandler

import retrofit2.Response

typealias ResponseConverter<T> = (Response<T>) -> ApiResult<T>

/**
 * Responsible for wrapping a Retrofit Response as one of the ApiResult types
 * (Success, Error, Exception).
 */
fun <T> defaultResponseConverter(response: Response<T>): ApiResult<T> {
    val httpStatus = HttpStatus(response.code(), response.raw().message())
    val body = response.body()

    return when {
        body == null -> ApiResult.Error(httpStatus)
        response.isSuccessful -> ApiResult.Success(body, httpStatus)
        else -> ApiResult.Error(httpStatus)
    }
}
