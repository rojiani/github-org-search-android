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

    /**
     * Fetch all repositories owned by the organization.
     */
    fun loadReposForOrg(organization: Organization) {
        Log.d(TAG, "loadReposForOrg($organization)")
        loading.value = true
        repoCall = gitHubService.getRepositoriesForOrg(organization.login)

        repoCall?.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                // DEBUG
                Log.d(TAG, "loadReposForOrg - onResponse: response = $response")
                Log.d(TAG, "loadReposForOrg - response body: ${response.body()}")

                allRepos.value = response.body()

                if (allRepos.value != null) {
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
        Log.d(TAG, "saveToBundle")
        selectedOrganization.value?.let { org ->
            outState.putParcelable(OrgDetailsViewModel.KEY_ORGANIZATION, org)
        }
    }

    /** Restore LiveData after app killed */
    fun restoreFromBundle(savedInstanceState: Bundle?) {
        Log.d(TAG, "restoreFromBundle")
        // If selectedOrganization (LiveData) is null, the ViewModel was destroyed.
        // Restore from Bundle. Otherwise we don't need to do anything.
        if (selectedOrganization.value == null) {
            savedInstanceState?.getParcelable<Organization>(OrgDetailsViewModel.KEY_ORGANIZATION)
                ?.let { org ->
                    selectedOrganization.value = org
                    Log.d(TAG, "restoreFromBundle - selectedOrganization restored from Bundle")
                    loadReposForOrg(org)
                    Log.d(TAG, "restoreFromBundle - Re-fetching repos")
                }
        }

        // TODO what selectedOrganization != null, but
    }

    companion object {
        const val REPO_COUNT_TO_SHOW = 3
        const val KEY_ORGANIZATION = "org_details"
    }

}
