package com.nrojiani.githuborgsearch.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nrojiani.githuborgsearch.network.GitHubService
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class SearchViewModelTest {

    object OrgQueries {
        internal const val VALID = "nytimes"
        internal const val INVALID = "googlez"
    }

//    companion object {
//        private const val ORG_QUERY_VALID = "nytimes"
//        private const val ORG_QUERY_NOT_FOUND = "googlez"
//
//    }

    // A JUnit Test Rule that swaps the background executor used by the Architecture Components
    // with a different one which executes each task synchronously.
    // @Rule
    @get:Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var gitHubService: GitHubService

    private lateinit var searchViewModel: SearchViewModel
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        searchViewModel = SearchViewModel(gitHubService)
    }

    @Test
    fun `loadOrgDetails success`() {
        Mockito.`when`(
            gitHubService.getOrg(OrgQueries.VALID)
        ).thenReturn(

        )
    }

    /*
    LiveData:

        isLoading():
            true ->


    onCleared() ->
        orgCall is cancelled

----------------------------
    SearchFragment test:

        hide keyboard

        org query whitespace trimmed
        capitalization doesn't matter

        orgCardView clicked ->
            isInvisible -> nothing
            isVisible -> see onOrgSelected()
                setSelectedOrganization called
                fragment transition stuff


        searchButton clicked ->
            null or blank -> searchEditText.error set to "Please enter an organization name"
            viewModel.loadOrgDetails(orgQuery) is called



    LiveData/Observers:

        getOrganization():
            null ->
                orgCardView.isInvisible
            present ->
                orgCardView.isVisible
                progressBar.isInvisible
                errorTextView.isGone

                each view in orgCardView has data (see showOrgDetails)
                - https://mdswanson.com/blog/2013/12/16/reliable-android-http-testing-with-retrofit-and-mockito.html

        isLoading():
            true ->
                progressBar.isVisible
                errorTextView.isGone
                orgCardView.isInvisible
            false ->
                progressBar.isInvisible

        getOrgLoadErrorMessage():
            null or blank ->
                errorTextView.isGone
                errorTextView.text = ""
            has valid msg ->
                errorTextView.isVisible
                orgCardView.isInvisible
                progressBar.isGone
     */

}