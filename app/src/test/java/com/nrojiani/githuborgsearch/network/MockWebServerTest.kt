package com.nrojiani.githuborgsearch.network

import android.util.Log
import com.nrojiani.githuborgsearch.testing.readMockApiResponse
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.core.StringContains
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import java.io.File


/**
 * Adapted from [GitHub: Karumi/KataTODOApiClientKotlin](https://github.com/Karumi/KataTODOApiClientKotlin)
 * [GitHub: Karumi/KataTODOApiClientKotlin - MockWebServerTest.kt](https://github.com/Karumi/KataTODOApiClientKotlin/blob/master/src/test/kotlin/com/karumi/todoapiclient/MockWebServerTest.kt)
 */
open class MockWebServerTest {

    private val TAG by lazy { this::class.java.simpleName }

    companion object {
        private val FILE_ENCODING = Charsets.UTF_8
    }

    private var server: MockWebServer = MockWebServer()

    protected val baseEndpoint: String
        get() = server.url("/").toString()

    protected enum class HttpRequestMethod {
        GET, HEAD, PUT, POST, PATCH, DELETE, CONNECT, OPTIONS, TRACE 
    }
    
    @Before
    open fun setUp() {
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    fun enqueueMockResponse(code: Int = 200, fileName: String? = null) {
        val mockResponse = MockResponse()
        val fileContent = readMockApiResponse(fileName!!)
        mockResponse.setResponseCode(code)
        mockResponse.setBody(fileContent)
        server.enqueue(mockResponse)
    }

    protected fun assertRequestSentTo(url: String) {
        val request = server.takeRequest()
        assertEquals(url, request.path)
    }

    protected fun assertIsExpectedHttpRequestMethod(httpMethod: HttpRequestMethod) {
        val request = server.takeRequest()
        assertEquals(httpMethod.name, request.method)
    }
    
    protected fun assertGetRequestSentTo(url: String) {
        val request = server.takeRequest()
        assertEquals(url, request.path)
        assertEquals("GET", request.method)
    }

    protected fun assertPostRequestSentTo(url: String) {
        val request = server.takeRequest()
        assertEquals(url, request.path)
        assertEquals("POST", request.method)
    }

    protected fun assertPutRequestSentTo(url: String) {
        val request = server.takeRequest()
        assertEquals(url, request.path)
        assertEquals("PUT", request.method)
    }

    protected fun assertDeleteRequestSentTo(url: String) {
        val request = server.takeRequest()
        assertEquals(url, request.path)
        assertEquals("DELETE", request.method)
    }

    protected fun assertRequestSentToContains(vararg paths: String) {
        val request = server.takeRequest()

        for (path in paths) {
            assertThat(request.path, StringContains.containsString(path))
        }
    }
    
    fun assertRequestContainsHeaders(headers: Map<String, String>, requestIndex: Int = 0) {
        val recordedRequest = getRecordedRequestAtIndex(requestIndex)
        headers.forEach { (name, expectedValue) -> 
            assertEquals(expectedValue, recordedRequest.getHeader(name))
        }
    }

    protected fun assertRequestBodyEquals(jsonFile: String) {
        val request = server.takeRequest()
        assertEquals(readMockApiResponse(jsonFile), request.body.readUtf8())
    }

    private fun getContentFromFile(fileName: String? = null): String {
        // TODO
        if (fileName == null) {
            Log.d(TAG, "getContentFromFile: fileName was null")
            return ""
        }

        val file = File(javaClass.getResource("/" + fileName).file)
        return buildString {
            file.useLines(FILE_ENCODING) { lines ->
                lines.forEach { append("$it") }
            }
        }
    }

    private fun getRecordedRequestAtIndex(requestIndex: Int): RecordedRequest =
        (0..requestIndex).map { server.takeRequest() }.last()
}