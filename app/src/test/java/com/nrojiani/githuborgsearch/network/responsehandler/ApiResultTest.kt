package com.nrojiani.githuborgsearch.network.responsehandler

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.net.UnknownHostException

internal class ApiResultTest {

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
        success.isCompleted.shouldBeTrue()
        error.isCompleted.shouldBeTrue()
        exception.isCompleted.shouldBeFalse()
        loading.isCompleted.shouldBeFalse()
        cancelled.isCompleted.shouldBeFalse()
        nullResult.isCompleted.shouldBeFalse()
    }

    @Test
    fun responseData() {
        val successResult: ApiResult<List<Int>>? =
            ApiResult.Success(listOf(1, 2, 3), HttpStatus(200, "OK"))
        listOf(1, 2, 3).shouldBe(successResult.responseData)

        error.responseData.shouldBeNull()
        exception.responseData.shouldBeNull()
        loading.responseData.shouldBeNull()
        cancelled.responseData.shouldBeNull()
        nullResult.responseData.shouldBeNull()
    }

    @Test
    fun formattedErrorMessage_onErrorResult() {
        error.formattedErrorMessage.shouldBe("Error: Not Found (404)")

        val errorWithEmptyMessage = ApiResult.Error(HttpStatus(599, ""))
        errorWithEmptyMessage.formattedErrorMessage
            .shouldBe("Error: Unknown Error (599)")
    }

    @Test
    fun formattedErrorMessage_onExceptionResult() {
        exception.formattedErrorMessage
            .shouldBe(
                "Exception: Unable to resolve host api.github.com: No address associated with hostname"
            )
        val exceptionWithNoDetailMsg = ApiResult.Exception(IllegalStateException())
        exceptionWithNoDetailMsg.formattedErrorMessage
            .shouldBe("Exception: IllegalStateException")
    }

    @Test
    fun formattedErrorMessage_onNonFailureResult() {
        success.formattedErrorMessage.shouldBeNull()
        loading.formattedErrorMessage.shouldBeNull()
        cancelled.formattedErrorMessage.shouldBeNull()
        nullResult.formattedErrorMessage.shouldBeNull()
    }
}
