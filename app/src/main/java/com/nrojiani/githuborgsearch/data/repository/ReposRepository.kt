package com.nrojiani.githuborgsearch.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nrojiani.githuborgsearch.data.model.Organization
import com.nrojiani.githuborgsearch.data.model.Repo
import com.nrojiani.githuborgsearch.network.GitHubService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository responsible for retrieving the repositories owned by an organization on GitHub
 * from the database or over the network.
 */
@Singleton
class ReposRepository
@Inject constructor(private val gitHubService: GitHubService) {

    private val TAG by lazy { this::class.java.simpleName }

    /* Mutable backing fields */
    private val _allRepos = MutableLiveData<List<Repo>?>()
    private val _isLoadingRepos = MutableLiveData<Boolean>()
    private val _repoLoadErrorMessage = MutableLiveData<String?>()

    /* Publicly exposed immutable LiveData */
    // TODO: Encapsulate errorMessage & isLoading in new class
    val allRepos: LiveData<List<Repo>?> = _allRepos
    val repoLoadErrorMessage: LiveData<String?> = _repoLoadErrorMessage
    val isLoadingRepos: LiveData<Boolean> = _isLoadingRepos

    /** Stores the top repos keyed by each owning Organization. */
    private val reposCache: MutableMap<Organization, List<Repo>> = HashMap()

    private var repoCall: Call<List<Repo>>? = null

    /**
     * Retrieve all repositories owned by the organization from database (currently unimplemented)
     * or network.
     *
     * TODO: suboptimal since it never checks if call has already been made
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

    fun cancelGetReposCall() = repoCall?.cancel()
}