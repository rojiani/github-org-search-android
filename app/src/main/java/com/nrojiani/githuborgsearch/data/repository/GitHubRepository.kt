package com.nrojiani.githuborgsearch.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nrojiani.githuborgsearch.data.model.Organization
import com.nrojiani.githuborgsearch.network.GitHubService
import com.nrojiani.githuborgsearch.util.EspressoIdlingResource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of Repository Pattern - see https://developer.android.com/jetpack/docs/guide
 * (rather than a git repo on GitHub)
 */
@Singleton
class GitHubRepository
@Inject constructor(private val gitHubService: GitHubService) {

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

    private lateinit var orgCall: Call<Organization>

    // TODO: suboptimal since it never checks if call has already been made
    /**
     * Try to retrieve the details for a GitHub Organization.
     */
    fun getOrganization(organizationName: String) {
        Log.d(TAG, "getOrganization($organizationName)")

        _loading.value = true

        // TODO: This isn't an optimal implementation. We'll fix it later.

        orgCall = gitHubService.getOrg(organizationName)

        EspressoIdlingResource.increment() // Set app as busy.

        orgCall.enqueue(object : Callback<Organization> {
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

    fun cancelGetOrganizationCall() {
        orgCall.cancel()
    }

}