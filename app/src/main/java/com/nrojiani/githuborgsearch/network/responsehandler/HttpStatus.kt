package com.nrojiani.githuborgsearch.network.responsehandler

import com.nrojiani.githuborgsearch.network.responsehandler.HttpStatus.Series.*

/**
 * HTTP status code & metadata.
 *
 * Example:
 * `val httpStatus = HttpStatus(code = 404, message = "Not Found")`
 *
 */
data class HttpStatus(
    val code: Int,
    val message: String = ""
) {
    val category: Series = when (code) {
        in 100..199 -> INFORMATIONAL
        in 200..299 -> SUCCESSFUL
        in 300..399 -> REDIRECTION
        in 400..499 -> CLIENT_ERROR
        in 500..599 -> SERVER_ERROR
        else -> throw IllegalArgumentException("Invalid HTTP status code")
    }

    enum class Series(val codeRange: String) {
        INFORMATIONAL("1xx"),
        SUCCESSFUL("2xx"),
        REDIRECTION("3xx"),
        CLIENT_ERROR("4xx"),
        SERVER_ERROR("5xx");
    }

}