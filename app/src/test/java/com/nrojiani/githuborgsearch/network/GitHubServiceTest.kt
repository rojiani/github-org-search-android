package com.nrojiani.githuborgsearch.network

import com.nrojiani.githuborgsearch.data.model.Organization
import com.nrojiani.githuborgsearch.data.model.Repo
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

    private val expectedHeaders = mapOf(
        "Accept" to "application/vnd.github.v3+json",
        "Content-Type" to "application/json"
    )

    private enum class MockApiResponse(val filename: String, val code: Int) {
        ORG_SUCCESS("org-nytimes.json", 200),
        ORG_SUCCESS_WITH_MISSING_AND_NULL_PROPS("org-amzn.json", 200),
        ORG_NOT_FOUND("org-404.json", 404),
        REPOS_SUCCESS("repos-nytimes.json", 200),
        REPOS_SUCCESS_WITH_MISSING_AND_NULL_PROPS("repos-amzn.json", 200),
        REPOS_NOT_FOUND("repos-404.json", 404)
    }

    private lateinit var gitHubClient: GitHubService

    @Before
    override fun setUp() {
        super.setUp()
        val mockWebServerEndpoint = baseEndpoint

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
    fun `expected headers sent with getOrg request`() {
        enqueueMockApiResponse(MockApiResponse.ORG_SUCCESS)
        gitHubClient.getOrg(ORG_NAME).execute()
        assertRequestContainsHeaders(expectedHeaders)
    }

    @Test
    fun `getOrg call sends to correct resource path endpoint`() {
        enqueueMockApiResponse(MockApiResponse.ORG_SUCCESS)
        gitHubClient.getOrg(ORG_NAME).execute()
        assertRequestSentTo("/orgs/$ORG_NAME")
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
            assertEquals("Amazon", name)
            assertEquals("amzn", login)
            assertEquals("https://avatars1.githubusercontent.com/u/8594673?v=4", avatarUrl)
            assertEquals("", blogUrl)
            assertEquals("", description)
            assertNull(location)
        }
    }

    @Test
    fun `getOrg 404 not found`() {
        enqueueMockApiResponse(MockApiResponse.ORG_NOT_FOUND)
        val response: Response<Organization> = gitHubClient.getOrg("foobar").execute()
        assertNull(response.body())
        assertEquals(404, response.code())

        /* response.message() would be "Not Found", but MockResponse doesn't expose a way
           to set the message, and it is set based on HTTP status code category (1xx, 2xx, ..., 5xx).
           code: http://tinyurl.com/y5hx4pge */
        assertEquals("Client Error", response.message())
    }

    @Test
    fun `expected headers sent with getRepositoriesForOrg request`() {
        enqueueMockApiResponse(MockApiResponse.REPOS_SUCCESS)
        gitHubClient.getRepositoriesForOrg(ORG_NAME).execute()
        assertRequestContainsHeaders(expectedHeaders)
    }


    @Test
    fun `getRepositoriesForOrg call sends to correct resource path endpoint`() {
        enqueueMockApiResponse(MockApiResponse.REPOS_SUCCESS)
        gitHubClient.getRepositoriesForOrg(ORG_NAME).execute()
        assertRequestSentTo("/orgs/$ORG_NAME/repos")
    }

    @Test
    fun `getRepositoriesForOrg sends a GET request`() {
        enqueueMockApiResponse(MockApiResponse.REPOS_SUCCESS)
        gitHubClient.getRepositoriesForOrg(ORG_NAME).execute()
        assertIsExpectedHttpRequestMethod(HttpRequestMethod.GET)
    }

    @Test
    fun `getRepositoriesForOrg deserialized to List of type Repo`() {
        enqueueMockApiResponse(MockApiResponse.REPOS_SUCCESS)
        val response: Response<List<Repo>> = gitHubClient.getRepositoriesForOrg(ORG_NAME).execute()
        val repos = response.body()

        assertNotNull(repos)
        assertEquals(30, repos!!.size)
        assertTrue(repos is List)
        val firstRepo = repos.first()
        assertTrue(firstRepo is Repo)

        firstRepo.apply {
            assertEquals(590289, id)
            assertEquals("tweetftp", name)
            assertEquals("https://github.com/nytimes/tweetftp", repoUrl)
            assertEquals(
                "Ruby Implementation of the Tweet File Transfer Protocol (APRIL FOOLS JOKE)",
                description
            )
            assertEquals(19, stars)
            assertEquals(1, forks)
            assertEquals("Ruby", language)
        }
    }

    @Test
    fun `getRepositoriesForOrg deserialized when Repo items in response hav null or empty fields`() {
        /* In the getRepositoriesForOrg response for "Amazon", some of the Repos have language = null */

        enqueueMockApiResponse(MockApiResponse.REPOS_SUCCESS_WITH_MISSING_AND_NULL_PROPS)
        val response: Response<List<Repo>> = gitHubClient.getRepositoriesForOrg("amzn").execute()

        val repos = response.body()
        assertNotNull(repos)
        assertEquals(30, repos!!.size)
        assertTrue(repos is List)
        val firstRepo = repos.first()
        assertTrue(firstRepo is Repo)
    }

    @Test
    fun `getRepositoriesForOrg 404 not found`() {
        enqueueMockApiResponse(MockApiResponse.REPOS_NOT_FOUND)
        val response: Response<List<Repo>> = gitHubClient.getRepositoriesForOrg("foobar").execute()
        assertNull(response.body())
        assertEquals(404, response.code())
        assertEquals("Client Error", response.message())
    }

    private fun enqueueMockApiResponse(mockResponse: MockApiResponse) =
        enqueueMockResponse(code = mockResponse.code, fileName = mockResponse.filename)

}