package com.nrojiani.githuborgsearch.network.responsehandler

import junit.framework.Assert.*
import org.junit.Test
import java.net.UnknownHostException

class ApiResultTest {

    private val success = ApiResult.Success(listOf(1, 2, 3), HttpStatus(200, "OK"))
    private val error = ApiResult.Error(HttpStatus(404, "Not Found"))
    private val exception = ApiResult.Exception(
        UnknownHostException("Unable to resolve host api.github.com: No address associated with hostname")
    )
    private val loading = ApiResult.Loading
    private val cancelled = ApiResult.Cancelled
    private val nullResult: ApiResult<List<Int>>? = null

    @Test
    fun isCompleted() {
        assertTrue(success.isCompleted)
        assertTrue(error.isCompleted)
        assertFalse(exception.isCompleted)
        assertFalse(loading.isCompleted)
        assertFalse(cancelled.isCompleted)
        assertFalse(nullResult.isCompleted)
    }

    @Test
    fun responseData() {
        val successResult: ApiResult<List<Int>>? =
            ApiResult.Success(listOf(1, 2, 3), HttpStatus(200, "OK"))
        assertEquals(listOf(1, 2, 3), successResult.responseData)

        assertNull(error.responseData)
        assertNull(exception.responseData)
        assertNull(loading.responseData)
        assertNull(cancelled.responseData)
        assertNull(nullResult.responseData)
    }

    @Test
    fun formattedErrorMessage_onErrorResult() {
        assertEquals("Error: Not Found (404)", error.formattedErrorMessage)

        val errorWithEmptyMessage = ApiResult.Error(HttpStatus(599, ""))
        assertEquals("Error: Unknown Error (599)", errorWithEmptyMessage.formattedErrorMessage)
    }

    @Test
    fun formattedErrorMessage_onExceptionResult() {
        assertEquals("Exception: Unable to resolve host api.github.com: No address associated with hostname", exception.formattedErrorMessage)
        val exceptionWithNoDetailMsg = ApiResult.Exception(IllegalStateException())
        assertEquals("Exception: IllegalStateException", exceptionWithNoDetailMsg.formattedErrorMessage)
    }

    @Test
    fun formattedErrorMessage_onNonFailureResult() {
        assertNull(success.formattedErrorMessage)
        assertNull(loading.formattedErrorMessage)
        assertNull(cancelled.formattedErrorMessage)
        assertNull(nullResult.formattedErrorMessage)
    }
}