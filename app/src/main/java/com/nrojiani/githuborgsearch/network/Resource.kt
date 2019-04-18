package com.nrojiani.githuborgsearch.network

/**
 * Encapsulates the state of data from either network or database.
 */
data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: String?
) {
    companion object Factory {
        fun <T> success(data: T?): Resource<T> = Resource(Status.SUCCESS, data, null)
        fun <T> error(message: String?): Resource<T> = Resource(Status.ERROR, null, message)
        fun <T> loading(data: T? = null): Resource<T> = Resource(Status.LOADING, data, null)
    }
}

/**
 * Status of a [Resource].
 */
enum class Status {
    SUCCESS, ERROR, LOADING
}