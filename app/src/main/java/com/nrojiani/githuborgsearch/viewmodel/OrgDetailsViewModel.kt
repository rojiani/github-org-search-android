package com.nrojiani.githuborgsearch.viewmodel

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
 * ViewModel for the view displaying Org Details & most starred repos.
 */
class OrgDetailsViewModel
@Inject constructor(
    private val gitHubService: GitHubService
) : ViewModel() {

    private val TAG by lazy { this::class.java.simpleName }

    /* publicly exposed LiveData */
    fun getAllRepos(): LiveData<List<Repo>?> = allRepos

    fun getRepoLoadErrorMessage(): LiveData<String?> = repoLoadErrorMessage
    fun isLoading(): LiveData<Boolean> = loading
    fun getSelectedOrganization(): LiveData<Organization> = selectedOrganization
    fun setSelectedOrganization(org: Organization) {
        selectedOrganization.value = org
    }

    private val selectedOrganization: MutableLiveData<Organization>
            by lazy { MutableLiveData<Organization>() }


    private val allRepos: MutableLiveData<List<Repo>?> by lazy { MutableLiveData<List<Repo>?>() }
    private val loading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    private val repoLoadErrorMessage: MutableLiveData<String?> by lazy { MutableLiveData<String?>() }

    private var repoCall: Call<List<Repo>>? = null


    /** Stores the top repos keyed by each owning Organization. */
    private val topReposCache: MutableMap<Organization, List<Repo>> = HashMap()

    fun hasTopReposCached(org: Organization): Boolean = org in topReposCache

    /**
     * Fetch all repositories owned by the organization.
     */
    fun loadReposForOrg(organization: Organization) {
        Log.d(TAG, "loadReposForOrg($organization)")
        loading.value = true
        repoCall = gitHubService.getRepositoriesForOrg(organization.login)

        repoCall?.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                Log.d(TAG, "loadReposForOrg - onResponse: response = $response")

                allRepos.value = response.body()

                if (allRepos.value != null) {
                    repoLoadErrorMessage.value = null
                    loading.value = false
                } else {
                    repoLoadErrorMessage.value = response.message()
                    loading.value = false
                }

                // Cache top repos
                allRepos.value?.sortedByDescending { it.stars }
                    ?.take(NUM_REPOS_TO_DISPLAY)?.let { topRepos ->
                        topReposCache += (organization to topRepos)
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
        selectedOrganization.value?.let { org ->
            outState.putParcelable(KEY_ORGANIZATION, org)
        }
    }

    /** Restore LiveData after app killed */
    fun restoreFromBundle(savedInstanceState: Bundle?) {
        // If selectedOrganization (LiveData) is null, the ViewModel was destroyed.
        // Restore from Bundle. Otherwise we don't need to do anything.
        if (selectedOrganization.value == null) {
            savedInstanceState?.getParcelable<Organization>(KEY_ORGANIZATION)
                ?.let { org ->
                    selectedOrganization.value = org
                    loadReposForOrg(org)
                }
        }
    }

    companion object {
        const val NUM_REPOS_TO_DISPLAY = 3
        const val KEY_ORGANIZATION = "org_details"
    }

}
