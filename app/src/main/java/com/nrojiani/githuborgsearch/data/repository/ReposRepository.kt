package com.nrojiani.githuborgsearch.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nrojiani.githuborgsearch.data.model.Organization
import com.nrojiani.githuborgsearch.data.model.Repo
import com.nrojiani.githuborgsearch.network.GitHubService
import com.nrojiani.githuborgsearch.network.responsehandler.ApiResult
import com.nrojiani.githuborgsearch.network.responsehandler.ResponseConverter
import com.nrojiani.githuborgsearch.network.responsehandler.defaultResponseConverter
import com.nrojiani.githuborgsearch.network.responsehandler.isCompleted
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

    val responseConverter: ResponseConverter<List<Repo>> = ::defaultResponseConverter

    /* Mutable backing field */
    private val _allRepos = MutableLiveData<ApiResult<List<Repo>>>()
    /* Publicly exposed immutable LiveData */
    val allRepos: LiveData<ApiResult<List<Repo>>> = _allRepos

    /** Stores the top repos keyed by each owning Organization. */
    private val reposCache: MutableMap<Organization, ApiResult<List<Repo>>> = HashMap()

    private var repoCall: Call<List<Repo>>? = null

    /**
     * Retrieve all repositories owned by the organization from database (currently unimplemented)
     * or network.
     */
    fun getReposForOrg(organization: Organization) {
        Log.d(TAG, "getReposForOrg(${organization.login})")

        // Check if response cached
        if (organization in reposCache) {
            val cachedResult = reposCache[organization]
            if (cachedResult.isCompleted) {
                Log.d(TAG, "${organization.login} in reposCache & completed")
                _allRepos.value = cachedResult
                return
            } else {
                Log.d(TAG, "${organization.login} in reposCache, but result not completed")
            }
        }

        // Set as loading
        _allRepos.value = ApiResult.Loading

        repoCall = gitHubService.getRepositoriesForOrg(organization.login)
        repoCall?.enqueue(
            object : Callback<List<Repo>> {
                override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                    Log.d(TAG, "getReposForOrg - onResponse: response.body() = ${response.body()}")

                    val apiResult = responseConverter(response)
                    _allRepos.value = apiResult
                    reposCache[organization] = apiResult
                }

                override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                    Log.e(TAG, t.message, t)
                    val apiResult = ApiResult.Exception(t)
                    reposCache[organization] = apiResult
                    _allRepos.value = apiResult
                }
            }
        )
    }

    fun cancelGetReposCall(): Unit? {
        _allRepos.value = ApiResult.Cancelled
        return repoCall?.cancel()
    }

    companion object {
        private const val TAG = "ReposRepository"
    }
}
