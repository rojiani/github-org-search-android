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
    fun getLoading(): LiveData<Boolean> = isLoading

    private val organization: MutableLiveData<Organization?>
            by lazy { MutableLiveData<Organization?>() }

    private val orgLoadErrorMessage: MutableLiveData<String?> by lazy { MutableLiveData<String?>() }
    private val isLoading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    /** Search EditText contents */
    private var orgSearchInput: String = ""

    private var orgCall: Call<Organization>? = null


    /**
     * Try to retrieve the details for a GitHub Organization.
     */
    fun fetchOrgDetails(searchInput: String) {
        isLoading.value = true
        orgCall = gitHubService.getOrg(searchInput)

        orgCall?.enqueue(object : Callback<Organization> {
            override fun onResponse(call: Call<Organization>, response: Response<Organization>) {
                // DEBUG
                Log.d(TAG, "fetchOrgDetails - onResponse: response = $response")
                Log.d(TAG, "fetchOrgDetails - onResponse: response body = ${response.body()}")

                // TODO - handle case with incomplete data, e.g., "NYTime"
                // - org exists but no name (because it was a typo)

                organization.value = response.body()

                if (organization.value != null) {
                    orgLoadErrorMessage.value = null
                    isLoading.value = false
                } else {
                    orgLoadErrorMessage.value = response.message()
                    isLoading.value = false
                }
            }

            override fun onFailure(call: Call<Organization>, t: Throwable) {
                Log.e(TAG, t.message, t)
                orgLoadErrorMessage.value = "GitHubService call failed (check org name)"
                isLoading.value = false
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        orgCall?.cancel()
    }

    fun saveToBundle(outState: Bundle) {
        organization.value?.let { org ->
            outState.putStringArray(
                ORG_DETAILS_KEY,
                arrayOf(org.name, org.login, org.avatarUrl)
            )

            outState.putString(ORG_SEARCH_INPUT_KEY, orgSearchInput)
        }
    }

    fun restoreFromBundle(savedInstanceState: Bundle?) {
        // Restore organization data (if it was present)
        organization.value?.let {
            savedInstanceState?.getStringArray(ORG_DETAILS_KEY)?.let { orgData ->
                val (name, login, avatarUrl) = orgData
                organization.value = Organization(name, login, avatarUrl)
            }
        }

        // Restore search field contents
        savedInstanceState?.getString(ORG_SEARCH_INPUT_KEY)?.let {
            orgSearchInput = it
        }
    }

    companion object {
        const val ORG_SEARCH_INPUT_KEY = "search_input"
        private const val ORG_DETAILS_KEY = "org_details"
    }
}


