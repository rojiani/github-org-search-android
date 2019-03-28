package com.nrojiani.githuborgsearch.network

import com.nrojiani.githuborgsearch.model.Organization
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val ORG_NAME = "nytimes"

class GitHubServiceTest : MockWebServerTest() {

    private enum class MockApiResponse(val filename: String, val code: Int) {
        ORG_SUCCESS("org-nytimes.json", 200),
        ORG_SUCCESS_WITH_MISSING_AND_NULL_PROPS("org-amzn.json", 200),
        ORG_NOT_FOUND("org-404.json", 404),
        REPO_SUCCESS("repo-nytimes.json", 200),
        REPO_NOT_FOUND("repo-404.json", 404)
    }

    private lateinit var gitHubClient: GitHubService

    @Before
    override fun setUp() {
        super.setUp()
        val mockWebServerEndpoint = baseEndpoint

        // TODO - Dagger-ize
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(mockWebServerEndpoint)
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(
                        KotlinJsonAdapterFactory()
                    ).build()
                )
            ).build()
        gitHubClient = retrofit.create(GitHubService::class.java)
    }

    @Test
    fun sendsAcceptAndContentTypeHeaders() {
        enqueueMockApiResponse(MockApiResponse.ORG_SUCCESS)
        gitHubClient.getOrg(ORG_NAME).execute()

        val expectedHeaders = mapOf(
            "Accept" to "application/vnd.github.v3+json",
            "Content-Type" to "application/json"
        )
        assertRequestContainsHeaders(expectedHeaders)
    }

    @Test
    fun `getOrg call sends to correct resource path endpoint`() {
        enqueueMockApiResponse(MockApiResponse.ORG_SUCCESS)
        gitHubClient.getOrg(ORG_NAME).execute()
        assertGetRequestSentTo("/orgs/$ORG_NAME")
    }

    @Test
    fun `getOrg sends a GET request`() {
        enqueueMockApiResponse(MockApiResponse.ORG_SUCCESS)
        gitHubClient.getOrg(ORG_NAME).execute()
        assertIsExpectedHttpRequestMethod(HttpRequestMethod.GET)
    }

    @Test
    fun `getOrg deserialized to Organization type`() {
        enqueueMockApiResponse(MockApiResponse.ORG_SUCCESS)
        val response: Response<Organization> = gitHubClient.getOrg(ORG_NAME).execute()
        val org = response.body()
        assertNotNull(org)

        org?.apply {
            assertEquals(name, "The New York Times")
            assertEquals(login, "nytimes")
            assertEquals(avatarUrl, "https://avatars0.githubusercontent.com/u/221409?v=4")
            assertEquals(blogUrl, "nytimes.com")
            assertEquals(location, "New York, NY")
            assertEquals(description, "")
        }
    }

    @Test
    fun `getOrg deserialized to Organization type when response has null or empty fields`() {
        /* In the getOrg response for "Amazon", location is null & blog & description are empty
           strings. Retrofit should still be able to deserialize it. */

        enqueueMockApiResponse(MockApiResponse.ORG_SUCCESS_WITH_MISSING_AND_NULL_PROPS)
        val response: Response<Organization> = gitHubClient.getOrg("amzn").execute()
        val org = response.body()
        assertNotNull(org)

        org?.apply {
            assertEquals(name, "Amazon")
            assertEquals(login, "amzn")
            assertEquals(avatarUrl, "https://avatars1.githubusercontent.com/u/8594673?v=4")
            assertEquals(blogUrl, "")
            assertEquals(description, "")
            assertNull(location)
        }
    }


    private fun enqueueMockApiResponse(mockResponse: MockApiResponse) =
        enqueueMockResponse(code = mockResponse.code, fileName = mockResponse.filename)

}