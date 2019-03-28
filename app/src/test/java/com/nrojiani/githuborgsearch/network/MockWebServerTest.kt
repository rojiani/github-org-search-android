package com.nrojiani.githuborgsearch.network

import com.nrojiani.githuborgsearch.testing.readMockApiResponse
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before


/**
 * Adapted from:
 * [GitHub: Karumi/KataTODOApiClientKotlin - MockWebServerTest.kt](https://github.com/Karumi/KataTODOApiClientKotlin/blob/master/src/test/kotlin/com/karumi/todoapiclient/MockWebServerTest.kt)
 */
open class MockWebServerTest {

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
        mockResponse.setBody(fileContent)
        mockResponse.setResponseCode(code)
        server.enqueue(mockResponse)
    }

    protected fun assertIsExpectedHttpRequestMethod(httpMethod: HttpRequestMethod) {
        val request = server.takeRequest()
        assertEquals(httpMethod.name, request.method)
    }

    protected fun assertRequestSentTo(url: String) {
        val request = server.takeRequest()
        assertEquals(url, request.path)
    }
    
    fun assertRequestContainsHeaders(headers: Map<String, String>, requestIndex: Int = 0) {
        val recordedRequest = getRecordedRequestAtIndex(requestIndex)
        headers.forEach { (name, expectedValue) -> 
            assertEquals(expectedValue, recordedRequest.getHeader(name))
        }
    }

    private fun getRecordedRequestAtIndex(requestIndex: Int): RecordedRequest =
        (0..requestIndex).map { server.takeRequest() }.last()
}