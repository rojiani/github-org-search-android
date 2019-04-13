package com.nrojiani.githuborgsearch.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nrojiani.githuborgsearch.model.Organization
import com.nrojiani.githuborgsearch.network.GitHubService
import com.nrojiani.githuborgsearch.util.EspressoIdlingResource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class SearchViewModel
@Inject constructor(
    private val gitHubService: GitHubService
) : ViewModel() {

    private val TAG by lazy { this::class.java.simpleName }

    /* Mutable backing fields */
    private val _organization = MutableLiveData<Organization?>()
    private val _orgLoadErrorMessage = MutableLiveData<String?>()
    private val _loading = MutableLiveData<Boolean>()

    /* Publicly exposed immutable LiveData */
    val organization: LiveData<Organization?> = _organization
    val orgLoadErrorMessage: LiveData<String?> = _orgLoadErrorMessage
    // TODO: Remove isLoading (also orgLoadErrorMessage?)
    // Expose information about the state of your data using a wrapper or another LiveData.
    // https://medium.com/androiddevelopers/viewmodels-and-livedata-patterns-antipatterns-21efaef74a54
    // Example: https://developer.android.com/jetpack/docs/guide#addendum
    val loading: LiveData<Boolean> = _loading

    /** Search EditText contents */
    private var orgSearchInput: String = ""

    private var orgCall: Call<Organization>? = null

    /**
     * Try to retrieve the details for a GitHub Organization.
     */
    fun loadOrgDetails(searchInput: String) {
        _loading.value = true
        orgCall = gitHubService.getOrg(searchInput)

        EspressoIdlingResource.increment() // Set app as busy.

        orgCall?.enqueue(object : Callback<Organization> {
            override fun onResponse(call: Call<Organization>, response: Response<Organization>) {
                Log.d(TAG, "loadOrgDetails - onResponse: response = $response")
                Log.d(TAG, "loadOrgDetails - onResponse: response.body = ${response.body()}")

                _organization.value = response.body()

                if (_organization.value != null) {
                    _orgLoadErrorMessage.value = null
                    _loading.value = false
                } else {
                    _orgLoadErrorMessage.value = response.message()
                    _loading.value = false
                }

                EspressoIdlingResource.decrement() // Set app as idle.
            }

            override fun onFailure(call: Call<Organization>, t: Throwable) {
                Log.e(TAG, t.message, t)

                _orgLoadErrorMessage.value = "GitHubService call failed"
                _loading.value = false

                EspressoIdlingResource.decrement() // Set app as idle.
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        orgCall?.cancel()
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
            _organization.value = org
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


