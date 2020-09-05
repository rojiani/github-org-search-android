package com.nrojiani.githuborgsearch.network.responsehandler

import io.kotest.matchers.shouldBe
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Test
import retrofit2.Response

internal class ResponseConverterTest {

    @Test
    fun whenResponseSuccess_convertedToSuccess() {
        val response: Response<String> = Response.success("foobar")
        defaultResponseConverter(response)
            .shouldBe(
                ApiResult.Success("foobar", HttpStatus(200, "OK"))
            )
    }

    // Need to mock response
    @Test
    fun whenResponseError_convertedToError() {
        val responseBody: ResponseBody =
            "".toResponseBody("application/json".toMediaType())

        val errorResponse: Response<String> = Response.error(404, responseBody)

        // With static methods, error message set to "Response.error()".
        // Class is final so cannot be mocked. see https://github.com/square/retrofit/issues/2089
        defaultResponseConverter(errorResponse)
            .shouldBe(
                ApiResult.Error(HttpStatus(404, "Response.error()"))
            )
    }
}
