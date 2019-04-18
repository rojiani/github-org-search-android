package com.nrojiani.githuborgsearch.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nrojiani.githuborgsearch.data.model.Organization
import com.nrojiani.githuborgsearch.network.GitHubService
import com.nrojiani.githuborgsearch.network.Resource
import com.nrojiani.githuborgsearch.network.responsehandler.ApiResult
import com.nrojiani.githuborgsearch.network.responsehandler.ResponseInterpreter
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
                Log.d(TAG, "getOrganization - onResponse: response.body = ${response.body()}")

                // TODO inject
                val responseInterpreter = ResponseInterpreter<Organization>()
                val apiResult = responseInterpreter.interpret(response)
                // TODO if ApiResult.Error, propagate errorMessage to UI
                val orgDetails = response.body()
                if (orgDetails != null) {
                    orgCache[organizationName] = orgDetails
                    _organization.value = Resource.success(orgDetails)
                } else {
                    _organization.value = Resource.error(response.message())
                }
            }

            override fun onFailure(call: Call<Organization>, t: Throwable) {
                Log.e(TAG, t.message, t)
                // TODO response strategy
                _organization.value = Resource.error(t.message)

                val apiResult = ApiResult.Exception(t)
                // TODO
            }
        })
    }

    fun cancelGetOrganizationCall() = orgCall?.cancel()
}