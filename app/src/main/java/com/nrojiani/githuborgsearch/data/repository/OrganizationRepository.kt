package com.nrojiani.githuborgsearch.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nrojiani.githuborgsearch.data.model.Organization
import com.nrojiani.githuborgsearch.network.GitHubService
import com.nrojiani.githuborgsearch.network.responsehandler.ApiResult
import com.nrojiani.githuborgsearch.network.responsehandler.ResponseInterpreter
import com.nrojiani.githuborgsearch.network.responsehandler.isCompleted
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
    private val orgCache: MutableMap<String, ApiResult<Organization>> = HashMap()

    /* Mutable backing field */
    private val _organization = MutableLiveData<ApiResult<Organization>>()

    /* Publicly exposed immutable LiveData */
    val organization: LiveData<ApiResult<Organization>> = _organization

    private var orgCall: Call<Organization>? = null

    /**
     * Retrieve the details for a GitHub Organization from database (currently unimplemented)
     * or network.
     */
    fun getOrganization(organizationName: String) {
        Log.d(TAG, "getOrganization($organizationName)")

        // Check if response cached
        if (organizationName in orgCache) {
            val cachedResult = orgCache[organizationName]
            if (cachedResult.isCompleted) {
                Log.d(TAG, "$organizationName in orgCache & completed")
                _organization.value = orgCache[organizationName]
                return
            } else {
                Log.d(TAG, "$organizationName in orgCache, but result not completed")
            }
        }

        // Set as loading
        _organization.value = ApiResult.Loading

        orgCall = gitHubService.getOrg(organizationName)
        orgCall?.enqueue(object : Callback<Organization> {
            override fun onResponse(call: Call<Organization>, response: Response<Organization>) {
                Log.d(TAG, "getOrganization - onResponse: response.body = ${response.body()}")

                // TODO inject
                val responseInterpreter = ResponseInterpreter<Organization>()
                val apiResult = responseInterpreter.interpret(response)
                _organization.value = apiResult
                orgCache[organizationName] = apiResult
            }

            override fun onFailure(call: Call<Organization>, t: Throwable) {
                Log.e(TAG, t.message, t)
                val apiResult = ApiResult.Exception(t)
                orgCache[organizationName] = apiResult
                _organization.value = apiResult
            }
        })
    }

    fun cancelGetOrganizationCall() {
        orgCall?.cancel()
        _organization.value = ApiResult.Cancelled
    }
}