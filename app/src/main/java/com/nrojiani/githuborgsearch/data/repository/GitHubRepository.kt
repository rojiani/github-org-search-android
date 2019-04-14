package com.nrojiani.githuborgsearch.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nrojiani.githuborgsearch.data.model.Organization
import com.nrojiani.githuborgsearch.data.model.Repo
import com.nrojiani.githuborgsearch.network.GitHubService
import com.nrojiani.githuborgsearch.util.EspressoIdlingResource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of Repository Pattern - see https://developer.android.com/jetpack/docs/guide
 * (rather than a git repo on GitHub)
 * TODO - rename. Confusing name due to dual meanings.
 */
@Singleton
class GitHubRepository
@Inject constructor(private val gitHubService: GitHubService) {

    private val TAG by lazy { this::class.java.simpleName }

    /* Mutable backing fields */
    private val _organization = MutableLiveData<Organization?>()
    private val _orgLoadErrorMessage = MutableLiveData<String?>()
    private val _isLoadingOrg = MutableLiveData<Boolean>()

    private val _allRepos = MutableLiveData<List<Repo>?>()
    private val _isLoadingRepos = MutableLiveData<Boolean>()
    private val _repoLoadErrorMessage = MutableLiveData<String?>()

    /* Publicly exposed immutable LiveData */
    val organization: LiveData<Organization?> = _organization
    val orgLoadErrorMessage: LiveData<String?> = _orgLoadErrorMessage
    // TODO: Remove isLoadingOrg (also orgLoadErrorMessage?)
    // Expose information about the state of your data using a wrapper or another LiveData.
    // https://medium.com/androiddevelopers/viewmodels-and-livedata-patterns-antipatterns-21efaef74a54
    // Example: https://developer.android.com/jetpack/docs/guide#addendum
    val isLoadingOrg: LiveData<Boolean> = _isLoadingOrg

    val allRepos: LiveData<List<Repo>?> = _allRepos
    val repoLoadErrorMessage: LiveData<String?> = _repoLoadErrorMessage
    val isLoadingRepos: LiveData<Boolean> = _isLoadingRepos

    /** Stores the top repos keyed by each owning Organization. */
    private val reposCache: MutableMap<Organization, List<Repo>> = HashMap()

    private var orgCall: Call<Organization>? = null
    private var repoCall: Call<List<Repo>>? = null

    // TODO: suboptimal since it never checks if call has already been made
    /**
     * Retrieve the details for a GitHub Organization from database (currently unimplemented)
     * or network.
     */
    fun getOrganization(organizationName: String) {
        Log.d(TAG, "getOrganization($organizationName)")

        _isLoadingOrg.value = true

        // TODO: This isn't an optimal implementation. We'll fix it later.
        // https://developer.android.com/jetpack/docs/guide

        orgCall = gitHubService.getOrg(organizationName)

        EspressoIdlingResource.increment() // Set app as busy.

        orgCall?.enqueue(object : Callback<Organization> {
            override fun onResponse(call: Call<Organization>, response: Response<Organization>) {
                Log.d(TAG, "loadOrgDetails - onResponse: response = $response")
                Log.d(TAG, "loadOrgDetails - onResponse: response.body = ${response.body()}")

                _organization.value = response.body()

                if (_organization.value != null) {
                    _orgLoadErrorMessage.value = null
                    _isLoadingOrg.value = false
                } else {
                    _orgLoadErrorMessage.value = response.message()
                    _isLoadingOrg.value = false
                }

                EspressoIdlingResource.decrement() // Set app as idle.
            }

            override fun onFailure(call: Call<Organization>, t: Throwable) {
                Log.e(TAG, t.message, t)

                _orgLoadErrorMessage.value = "GitHubService call failed"
                _isLoadingOrg.value = false

                EspressoIdlingResource.decrement() // Set app as idle.
            }
        })
    }

    /**
     * Retrieve all repositories owned by the organization from database (currently unimplemented)
     * or network.
     */
    fun getReposForOrg(organization: Organization) {
        Log.d(TAG, "getReposForOrg($organization)")

        // Check cache
        if (organization in reposCache) {
            _allRepos.value = reposCache.getValue(organization)
            return
        }

        _isLoadingRepos.value = true
        repoCall = gitHubService.getRepositoriesForOrg(organization.login)

        repoCall?.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                Log.d(TAG, "getReposForOrg - onResponse: response = $response")

                _allRepos.value = response.body()

                if (_allRepos.value != null) {
                    _repoLoadErrorMessage.value = null
                    _isLoadingRepos.value = false
                } else {
                    _repoLoadErrorMessage.value = response.message()
                    _isLoadingRepos.value = false
                }

                // Cache repos for org
                _allRepos.value?.let { repos ->
                    reposCache += (organization to repos)
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                Log.e(TAG, t.message, t)
                _repoLoadErrorMessage.value = "GitHubService call failed"
                _isLoadingRepos.value = false
            }
        })
    }

    fun cancelGetOrganizationCall() = orgCall?.cancel()
    fun cancelGetReposCall() = repoCall?.cancel()

}