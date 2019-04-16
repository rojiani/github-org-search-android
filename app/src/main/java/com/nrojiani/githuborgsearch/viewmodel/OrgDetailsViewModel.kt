package com.nrojiani.githuborgsearch.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.nrojiani.githuborgsearch.data.model.Organization
import com.nrojiani.githuborgsearch.data.model.Repo
import com.nrojiani.githuborgsearch.data.repository.ReposRepository
import javax.inject.Inject


/**
 * ViewModel for the view displaying Org Details & most starred repos.
 */
class OrgDetailsViewModel
@Inject constructor(
    private val reposRepository: ReposRepository
) : ViewModel() {

    private val TAG by lazy { this::class.java.simpleName }

    /**
     * The top `n` most starred repos for the [selectedOrganization] in decreasing order,
     * where `n` is [NUM_REPOS_TO_DISPLAY].
     */
    val topRepos: LiveData<List<Repo>?> =
        Transformations.map(reposRepository.allRepos) { allRepos ->
            allRepos?.sortedByDescending { it.stars }?.take(NUM_REPOS_TO_DISPLAY)
        }

    val repoLoadErrorMessage: LiveData<String?> = reposRepository.repoLoadErrorMessage
    val isLoadingRepos: LiveData<Boolean> = reposRepository.isLoadingRepos

    // TODO - revisit later. Need to pass data between fragments. Is there a better way that
    // wouldn't expose this as MutableLiveData?
    val selectedOrganization = MutableLiveData<Organization>()

    fun getReposForOrg(organization: Organization) = reposRepository.getReposForOrg(organization)

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
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        reposRepository.cancelGetReposCall()
    }

    companion object {
        const val NUM_REPOS_TO_DISPLAY = 3
        const val KEY_ORGANIZATION = "org_details"
    }

}
