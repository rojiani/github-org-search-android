package com.nrojiani.githuborgsearch.ui.orgdetails

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nrojiani.githuborgsearch.model.Organization
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
    fun getOrganization(): LiveData<Organization> = organization
    fun setSelectedOrganization(org: Organization) {
        organization.value = org
    }

    private val organization: MutableLiveData<Organization>
            by lazy { MutableLiveData<Organization>() }


    private val repos: MutableLiveData<List<Repo>?> by lazy { MutableLiveData<List<Repo>?>() }
    private val loading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    private val repoLoadErrorMessage: MutableLiveData<String?> by lazy { MutableLiveData<String?>() }

    private var repoCall: Call<List<Repo>>? = null

    /**
     * Fetch all repositories owned by the organization.
     */
    fun fetchReposForOrg(orgLogin: String) {
        Log.d(TAG, "fetchReposForOrg($orgLogin)")
        loading.value = true
        repoCall = gitHubService.getRepositoriesForOrg(orgLogin)

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
                repoLoadErrorMessage.value = "GitHubService call failed"
                loading.value = false
            }
        })
    }

    fun saveToBundle(outState: Bundle) {
        organization.value?.let { org ->
            outState.putParcelable(OrgDetailsViewModel.KEY_ORGANIZATION, org)
        }

        // TODO top repos
    }

    /** Restore LiveData after app killed by system */
    fun restoreFromBundle(savedInstanceState: Bundle?) {
        // Restore organization data (if it was present)
        savedInstanceState?.getParcelable<Organization>(OrgDetailsViewModel.KEY_ORGANIZATION)
            ?.let { org ->
                organization.value = org
            }

        // TODO top repos
    }


    companion object {
        const val REPO_COUNT_TO_SHOW = 3

        const val KEY_ORGANIZATION = "org_details"
//        const val KEY_REPO_1 = "key_repo_1"
//        const val KEY_REPO_2 = "key_repo_2"
//        const val KEY_REPO_3 = "key_repo_3"
    }

}
