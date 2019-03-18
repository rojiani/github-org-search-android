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

    private val orgSearchInput: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    fun getOrgSearchInput(): LiveData<String> = orgSearchInput


    private val organization: MutableLiveData<Organization>
            by lazy { MutableLiveData<Organization>() }
    fun getOrganization(): LiveData<Organization> = organization
    fun setOrganization(org: Organization) {
        organization.value = org
    }

    private val orgLoadError: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    fun getOrgLoadError(): LiveData<Boolean> = orgLoadError

    private val loading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    fun getLoading(): LiveData<Boolean> = loading

    private lateinit var orgCall: Call<Organization>

    fun fetchOrgDetails(orgSearchInput: String) {
        loading.value = true
        orgCall = gitHubService.getOrg(orgSearchInput)

        orgCall.enqueue(object : Callback<Organization> {
            override fun onResponse(call: Call<Organization>, response: Response<Organization>) {
                Log.d(TAG, "fetchOrgDetails - onResponse: response = $response")
                Log.d(TAG, "fetchOrgDetails - response body: ${response.body()}")

                // TODO - handle case with incomplete data, e.g., "NYTime"
                // - org exists but no name (because it was a typo)

                orgLoadError.value = false
                loading.value = false
                organization.value = response.body()
            }

            override fun onFailure(call: Call<Organization>, t: Throwable) {
                Log.e(TAG, t.message, t)
                orgLoadError.value = true
                loading.value = false
            }
        })

    }

    fun saveToBundle(outState: Bundle) {
        val org = organization.value
        org?.let {
            outState.putStringArray(
                ORG_DETAILS_KEY,
                arrayOf(org.name, org.login, org.avatarUrl)
            )

            Log.d(TAG, "saveToBundle: saved data ${outState.getStringArray(ORG_DETAILS_KEY).toList()}")
        } ?: Log.d(TAG, "saveToBundle: org null - not saved")
    }

    fun restoreFromBundle(savedInstanceState: Bundle?) {
        // We only care about restoring if we have Organization details
        organization.value?.let {
            savedInstanceState?.containsKey(ORG_DETAILS_KEY)?.let {
                val orgData = savedInstanceState.getStringArray(ORG_DETAILS_KEY)
                Log.d(TAG, "restoreFromBundle: orgData: $orgData")
                orgData?.let {
                    val (name, login, avatarUrl) = it
                    organization.value = Organization(name, login, avatarUrl)
                }
                Log.d(TAG, "restoreFromBundle: organization.value: ${organization.value}")
            }
        } ?: Log.d(TAG, "restoreFromBundle: organization was null - nothing to restore")
    }

    override fun onCleared() {
        super.onCleared()
        orgCall.cancel()
    }

    companion object {
        private const val ORG_DETAILS_KEY = "org_details"
    }
}


