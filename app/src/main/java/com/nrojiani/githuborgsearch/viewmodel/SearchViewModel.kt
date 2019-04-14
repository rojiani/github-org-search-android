package com.nrojiani.githuborgsearch.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nrojiani.githuborgsearch.data.model.Organization
import com.nrojiani.githuborgsearch.data.repository.GitHubRepository
import javax.inject.Inject

class SearchViewModel
@Inject constructor(
    private val gitHubRepository: GitHubRepository
) : ViewModel() {

    private val TAG by lazy { this::class.java.simpleName }


    /* Publicly exposed immutable LiveData */
    val organization: LiveData<Organization?> = gitHubRepository.organization
    val orgLoadErrorMessage: LiveData<String?> = gitHubRepository.orgLoadErrorMessage
    val loading: LiveData<Boolean> = gitHubRepository.loading

    /** Search EditText contents */
    private var orgSearchInput: String = ""

    /**
     * Try to retrieve the details for a GitHub Organization.
     */
    fun loadOrgDetails(searchInput: String) {
        Log.d(TAG, "loadOrgDetails")
        gitHubRepository.getOrganization(searchInput)
    }

    override fun onCleared() {
        super.onCleared()
        gitHubRepository.cancelGetOrganizationCall()
    }

    // TODO:
    // ViewModel shouldn't know about Android. Move to Fragment
    fun saveToBundle(outState: Bundle) {
        organization.value?.let { org ->
            outState.putParcelable(KEY_ORGANIZATION, org)
        }
    }

    // TODO:
    // ViewModel shouldn't know about Android. Move to Fragment
    /** Restore LiveData after app killed by system */
    fun restoreFromBundle(savedInstanceState: Bundle?) {
        // Restore organization data (if it was present)
        savedInstanceState?.getParcelable<Organization>(KEY_ORGANIZATION)?.let { org ->
            //_organization.value = org
            Log.d(TAG, "restoreFromBundle: TODO")
        }

        // Restore search field contents
        savedInstanceState?.getString(KEY_ORG_SEARCH_INPUT)?.let {
            orgSearchInput = it
        }
    }

    companion object {
        const val KEY_ORG_SEARCH_INPUT = "search_input"
        const val KEY_ORGANIZATION = "org_parcelable"
    }
}


