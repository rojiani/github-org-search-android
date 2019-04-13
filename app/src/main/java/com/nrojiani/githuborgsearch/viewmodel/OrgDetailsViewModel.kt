package com.nrojiani.githuborgsearch.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nrojiani.githuborgsearch.data.model.Organization
import com.nrojiani.githuborgsearch.data.model.Repo
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

    /* Mutable backing fields */
    private val _allRepos = MutableLiveData<List<Repo>?>()
    private val _loading = MutableLiveData<Boolean>()
    private val _repoLoadErrorMessage = MutableLiveData<String?>()

    /* Publicly exposed LiveData */
    val allRepos: LiveData<List<Repo>?> = _allRepos
    val repoLoadErrorMessage: LiveData<String?> = _repoLoadErrorMessage
    val isLoading: LiveData<Boolean> = _loading

    // TODO - revisit later. Need to pass data between fragments. Is there a better way that
    // wouldn't expose this as MutableLiveData?
    val selectedOrganization = MutableLiveData<Organization>()

    private lateinit var repoCall: Call<List<Repo>>

    /** Stores the top repos keyed by each owning Organization. */
    private val topReposCache: MutableMap<Organization, List<Repo>> = HashMap()

    fun hasTopReposCached(org: Organization): Boolean = org in topReposCache

    /**
     * Fetch all repositories owned by the organization.
     */
    fun loadReposForOrg(organization: Organization) {
        Log.d(TAG, "loadReposForOrg($organization)")
        _loading.value = true
        repoCall = gitHubService.getRepositoriesForOrg(organization.login)

        repoCall.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                Log.d(TAG, "loadReposForOrg - onResponse: response = $response")

                _allRepos.value = response.body()

                if (allRepos.value != null) {
                    _repoLoadErrorMessage.value = null
                    _loading.value = false
                } else {
                    _repoLoadErrorMessage.value = response.message()
                    _loading.value = false
                }

                // Cache top repos
                allRepos.value?.sortedByDescending { it.stars }
                    ?.take(NUM_REPOS_TO_DISPLAY)?.let { topRepos ->
                        topReposCache += (organization to topRepos)
                    }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                Log.e(TAG, t.message, t)
                _repoLoadErrorMessage.value = "GitHubService call failed"
                _loading.value = false
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
