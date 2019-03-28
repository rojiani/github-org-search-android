@file:JvmName("TestUtils")

package com.nrojiani.githuborgsearch.testing

import okhttp3.mockwebserver.MockResponse
import java.io.File

//import java.io.IOException

private const val API_RESPONSES_DIR = "src/test/resources/mock-api-responses"

/**
 * Return a File located in [API_RESPONSES_DIR]
 */
fun apiResponseFile(filename: String): File = File("$API_RESPONSES_DIR/$filename")

/**
 * Reads a JSON file rooted in [API_RESPONSES_DIR].
 */
fun readMockApiResponse(filename: String): String = buildString {
    apiResponseFile(filename).useLines { lines ->
        lines.forEach { append("$it") }
    }
}


fun createMockResponse(
    body: String,
    responseCode: Int = 200,
    headers: Map<String, String> = mapOf("Content-type" to "application/json")
): MockResponse {
    return MockResponse().apply {
        setResponseCode(responseCode)
        setBody(body)
        headers.forEach { (name, value) ->
            addHeader("$name: $value")
        }
    }
}
