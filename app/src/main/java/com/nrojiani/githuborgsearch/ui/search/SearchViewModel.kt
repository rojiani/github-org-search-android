package com.nrojiani.githuborgsearch.ui.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nrojiani.githuborgsearch.model.Organization
import com.nrojiani.githuborgsearch.model.Repo
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

    // TODO these will go to orgdetails
//    private val repos: MutableLiveData<List<Repo>>
//            by lazy { MutableLiveData<List<Repo>>() }
//
//    private val repoLoadError: MutableLiveData<Boolean>
//            by lazy { MutableLiveData<Boolean>() }

//    private val loading: MutableLiveData<Boolean>
//            by lazy { MutableLiveData<Boolean>() }

    //private lateinit var repoCall: Call<List<Repo>>

    private val organization: MutableLiveData<Organization>
            by lazy { MutableLiveData<Organization>() }

    private val orgLoadError: MutableLiveData<Boolean>
            by lazy { MutableLiveData<Boolean>() }

    private val loading: MutableLiveData<Boolean>
            by lazy { MutableLiveData<Boolean>() }

    private lateinit var orgCall: Call<Organization>

    // TODO call in SearchFragment, implement SearchInitiatedListener
    fun fetchOrgDetails(orgSearchInput: String) {
        loading.value = true
        orgCall = gitHubService.getOrg(orgSearchInput)

        orgCall.enqueue(object : Callback<Organization> {
            override fun onResponse(call: Call<Organization>, response: Response<Organization>) {
                Log.d(TAG, "fetchOrgDetails - onResponse: response = $response")
                Log.d(TAG, "fetchOrgDetails - response body: ${response.body()}")
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

    override fun onCleared() {
        super.onCleared()
        orgCall.cancel()
    }
}


