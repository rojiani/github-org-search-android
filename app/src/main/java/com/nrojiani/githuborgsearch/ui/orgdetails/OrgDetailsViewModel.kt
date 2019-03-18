package com.nrojiani.githuborgsearch.ui.orgdetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nrojiani.githuborgsearch.model.Repo
import com.nrojiani.githuborgsearch.network.GitHubService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

/**
 * ViewModel for the view displaying Org Details & top repos.
 */
class OrgDetailsViewModel
@Inject constructor(
    private val gitHubService: GitHubService
) : ViewModel() {

    private val TAG by lazy { this::class.java.simpleName }

    /* publicly exposed LiveData */
    fun getRepos(): LiveData<List<Repo>?> = repos
    fun getRepoLoadErrorMessage(): LiveData<String?> = repoLoadErrorMessage
    fun isLoading(): LiveData<Boolean> = loading

    private val repos: MutableLiveData<List<Repo>?> by lazy { MutableLiveData<List<Repo>?>() }
    private val repoLoadErrorMessage: MutableLiveData<String?> by lazy { MutableLiveData<String?>() }
    private val loading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    private var repoCall: Call<List<Repo>>? = null

    /**
     * Fetch all repositories owned by the organization.
     */
    fun fetchReposForOrg(org: String) {
        Log.d(TAG, "fetchReposForOrg($org)")
        loading.value = true
        repoCall = gitHubService.getRepositoriesForOrg(org)

        repoCall?.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                // DEBUG
                Log.d(TAG, "fetchRepos - onResponse: response = $response")
                Log.d(TAG, "fetchRepos - response body: ${response.body()}")

                repos.value = response.body()

                if (repos.value != null) {
                    repoLoadErrorMessage.value = null
                    loading.value = false
                } else {
                    repoLoadErrorMessage.value = response.message()
                    loading.value = false
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                Log.e(TAG, t.message, t)
                repoLoadErrorMessage.value =  "GitHubService call failed"
                loading.value = false
            }
        })
    }

}
