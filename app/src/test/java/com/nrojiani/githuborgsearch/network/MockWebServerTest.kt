package com.nrojiani.githuborgsearch.network

import com.nrojiani.githuborgsearch.testutils.readJsonFile
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import java.io.File


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

    fun enqueueMockResponse(code: Int = 200, fileName: String) {
        val json: String = readMockApiResponseJsonFile(fileName)
        
        val mockResponse = MockResponse().apply {
            setBody(json)
            setResponseCode(code)
        }

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


    /**
     * Reads a JSON file rooted in [MOCK_API_RESPONSES_DIR].
     */
    private fun readMockApiResponseJsonFile(filename: String): String =
        filename.run(::mockApiResponseFile)
                .run(::readJsonFile)

    /**
     * Return a mock response JSON file located in [MOCK_API_RESPONSES_DIR]
     */
    private fun mockApiResponseFile(filename: String): File = File("$MOCK_API_RESPONSES_DIR/$filename")

    
    companion object {
        private const val TEST_RESOURCES_DIR = "src/test/resources"
        private const val MOCK_API_DIR = "$TEST_RESOURCES_DIR/mock-api"
        private const val MOCK_API_RESPONSES_DIR = "$MOCK_API_DIR/responses"
    }
}