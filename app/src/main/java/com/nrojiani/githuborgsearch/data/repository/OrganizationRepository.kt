package com.nrojiani.githuborgsearch.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nrojiani.githuborgsearch.data.model.Organization
import com.nrojiani.githuborgsearch.network.GitHubService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository responsible for retrieving data about an organization on GitHub
 * from the database or over the network.
 */
@Singleton
class OrganizationRepository
@Inject constructor(private val gitHubService: GitHubService) {

    private val TAG by lazy { this::class.java.simpleName }

    // basic in-memory cache (orgName: String => Organization)
    private val orgCache: MutableMap<String, Organization> = HashMap()

    /* Mutable backing fields */
    private val _organization = MutableLiveData<Organization?>()
    private val _orgLoadErrorMessage = MutableLiveData<String?>()
    private val _isLoadingOrg = MutableLiveData<Boolean>()

    /* Publicly exposed immutable LiveData */
    val organization: LiveData<Organization?> = _organization
    val orgLoadErrorMessage: LiveData<String?> = _orgLoadErrorMessage
    // TODO: Encapsulate errorMessage & isLoading in new class
    // Expose information about the state of your data using a wrapper or another LiveData.
    // https://medium.com/androiddevelopers/viewmodels-and-livedata-patterns-antipatterns-21efaef74a54
    // Example: https://developer.android.com/jetpack/docs/guide#addendum
    val isLoadingOrg: LiveData<Boolean> = _isLoadingOrg

    private var orgCall: Call<Organization>? = null

    /**
     * Retrieve the details for a GitHub Organization from database (currently unimplemented)
     * or network.
     *
     * TODO: suboptimal since it never checks if call has already been made
     */
    fun getOrganization(organizationName: String) {
        Log.d(TAG, "getOrganization($organizationName)")

        if (organizationName in orgCache) {
            _organization.value = orgCache[organizationName]
            return
        }

        _isLoadingOrg.value = true
        orgCall = gitHubService.getOrg(organizationName)

        orgCall?.enqueue(object : Callback<Organization> {
            override fun onResponse(call: Call<Organization>, response: Response<Organization>) {
                Log.d(TAG, "loadOrgDetails - onResponse: response = $response")
                Log.d(TAG, "loadOrgDetails - onResponse: response.body = ${response.body()}")

                val orgDetails = response.body()
                 _organization.value = orgDetails

                if (orgDetails != null) {
                    _orgLoadErrorMessage.value = null
                    _isLoadingOrg.value = false
                    orgCache[organizationName] = orgDetails
                } else {
                    _orgLoadErrorMessage.value = response.message()
                    _isLoadingOrg.value = false
                }
            }

            override fun onFailure(call: Call<Organization>, t: Throwable) {
                Log.e(TAG, t.message, t)

                _orgLoadErrorMessage.value = "GitHubService call failed"
                _isLoadingOrg.value = false
            }
        })
    }

    fun cancelGetOrganizationCall() = orgCall?.cancel()
}