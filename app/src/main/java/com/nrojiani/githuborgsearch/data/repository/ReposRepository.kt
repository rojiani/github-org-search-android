package com.nrojiani.githuborgsearch.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nrojiani.githuborgsearch.data.model.Organization
import com.nrojiani.githuborgsearch.data.model.Repo
import com.nrojiani.githuborgsearch.misc.OpenForTesting
import com.nrojiani.githuborgsearch.network.GitHubService
import com.nrojiani.githuborgsearch.network.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository responsible for retrieving the repositories owned by an organization on GitHub
 * from the database or over the network.
 */
@OpenForTesting
@Singleton
class ReposRepository
@Inject constructor(private val gitHubService: GitHubService) {

    private val TAG by lazy { this::class.java.simpleName }

    /* Mutable backing field */
    private val _allRepos = MutableLiveData<Resource<List<Repo>>>()
    /* Publicly exposed immutable LiveData */
    val allRepos: LiveData<Resource<List<Repo>>> = _allRepos

    /** Stores the top repos keyed by each owning Organization. */
    private val reposCache: MutableMap<Organization, List<Repo>> = HashMap()

    private var repoCall: Call<List<Repo>>? = null

    /**
     * Retrieve all repositories owned by the organization from database (currently unimplemented)
     * or network.
     */
    fun getReposForOrg(organization: Organization) {
        Log.d(TAG, "getReposForOrg(${organization.login})")

        if (organization in reposCache) {
            _allRepos.value = Resource.success(reposCache[organization])
            return
        }

        _allRepos.value = Resource.loading()
        repoCall = gitHubService.getRepositoriesForOrg(organization.login)

        repoCall?.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                Log.d(TAG, "getReposForOrg - onResponse: response.body() = ${response.body()}")

                val orgRepos = response.body()
                if (orgRepos != null) {
                    reposCache[organization] = orgRepos
                    _allRepos.value = Resource.success(orgRepos)
                } else {
                    // TODO response strategy
                    // TODO get error message based on status code
                    _allRepos.value = Resource.error(response.message())
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                Log.e(TAG, t.message, t)
                _allRepos.value = Resource.error(t.message)
            }
        })
    }

    fun cancelGetReposCall() = repoCall?.cancel()
}