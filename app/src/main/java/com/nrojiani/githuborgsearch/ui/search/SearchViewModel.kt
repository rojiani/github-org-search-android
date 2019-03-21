package com.nrojiani.githuborgsearch.ui.search

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nrojiani.githuborgsearch.model.Organization
import com.nrojiani.githuborgsearch.network.GitHubService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class SearchViewModel
@Inject constructor(
    private val gitHubService: GitHubService
) : ViewModel() {

    private val TAG by lazy { this::class.java.simpleName }

    /* publicly exposed LiveData */
    fun getOrganization(): LiveData<Organization?> = organization

    fun getOrgLoadErrorMessage(): LiveData<String?> = orgLoadErrorMessage
    fun isLoading(): LiveData<Boolean> = loading

    private val organization: MutableLiveData<Organization?>
            by lazy { MutableLiveData<Organization?>() }
    private val orgLoadErrorMessage: MutableLiveData<String?> by lazy { MutableLiveData<String?>() }
    private val loading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    /** Search EditText contents */
    private var orgSearchInput: String = ""

    private var orgCall: Call<Organization>? = null

    /**
     * Try to retrieve the details for a GitHub Organization.
     */
    fun loadOrgDetails(searchInput: String) {
        loading.value = true
        orgCall = gitHubService.getOrg(searchInput)

        orgCall?.enqueue(object : Callback<Organization> {
            override fun onResponse(call: Call<Organization>, response: Response<Organization>) {
                // DEBUG
                Log.d(TAG, "loadOrgDetails - onResponse: response = $response")
                Log.d(TAG, "loadOrgDetails - onResponse: response body = ${response.body()}")

                // TODO - handle case with incomplete data, e.g., "NYTime"
                // - org exists but no name (because it was a typo)

                organization.value = response.body()

                if (organization.value != null) {
                    orgLoadErrorMessage.value = null
                    loading.value = false
                } else {
                    orgLoadErrorMessage.value = response.message()
                    loading.value = false
                }
            }

            override fun onFailure(call: Call<Organization>, t: Throwable) {
                Log.e(TAG, t.message, t)
                orgLoadErrorMessage.value = "GitHubService call failed (check org name)"
                loading.value = false
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        orgCall?.cancel()
    }

    fun saveToBundle(outState: Bundle) {
        organization.value?.let { org ->
            outState.putParcelable(KEY_ORGANIZATION, org)
        }
    }

    /** Restore LiveData after app killed by system */
    fun restoreFromBundle(savedInstanceState: Bundle?) {
        // Restore organization data (if it was present)
        savedInstanceState?.getParcelable<Organization>(KEY_ORGANIZATION)?.let { org ->
            organization.value = org
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


