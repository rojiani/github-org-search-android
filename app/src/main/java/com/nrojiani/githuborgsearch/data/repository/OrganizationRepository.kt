package com.nrojiani.githuborgsearch.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nrojiani.githuborgsearch.data.model.Organization
import com.nrojiani.githuborgsearch.network.GitHubService
import com.nrojiani.githuborgsearch.network.Resource
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

    /* Mutable backing field */
    private val _organization = MutableLiveData<Resource<Organization>>()

    /* Publicly exposed immutable LiveData */
    val organization: LiveData<Resource<Organization>> = _organization

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
            _organization.value = Resource.success(orgCache[organizationName])
            return
        }

        _organization.value = Resource.loading()

        orgCall = gitHubService.getOrg(organizationName)

        orgCall?.enqueue(object : Callback<Organization> {
            override fun onResponse(call: Call<Organization>, response: Response<Organization>) {
                Log.d(TAG, "loadOrgDetails - onResponse: response = $response")
                Log.d(TAG, "loadOrgDetails - onResponse: response.body = ${response.body()}")

                val orgDetails = response.body()
                if (orgDetails != null) {
                    orgCache[organizationName] = orgDetails
                    _organization.value = Resource.success(orgDetails)
                } else {
                    // TODO response strategy
                    // TODO get error message based on status code
                    _organization.value = Resource.error(response.message())
                }
            }

            override fun onFailure(call: Call<Organization>, t: Throwable) {
                Log.e(TAG, t.message, t)
                // TODO response strategy
                _organization.value = Resource.error(t.message)
            }
        })
    }

    fun cancelGetOrganizationCall() = orgCall?.cancel()
}