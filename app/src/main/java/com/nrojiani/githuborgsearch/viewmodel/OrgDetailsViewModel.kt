package com.nrojiani.githuborgsearch.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.nrojiani.githuborgsearch.data.model.Organization
import com.nrojiani.githuborgsearch.data.model.Repo
import com.nrojiani.githuborgsearch.data.repository.ReposRepository
import com.nrojiani.githuborgsearch.network.responsehandler.ApiResult

/**
 * ViewModel for the view displaying Org Details & most starred repos.
 */
class OrgDetailsViewModel(
    private val reposRepository: ReposRepository
) : ViewModel() {

    init {
        Log.d(TAG, "initializing $TAG - ${hashCode()}")
    }

    /**
     * The top `n` most starred repos for the [selectedOrganization] in decreasing order,
     * where `n` is [REPOS_TO_DISPLAY].
     */
    val topRepos: LiveData<ApiResult<List<Repo>>> =
        Transformations.map(reposRepository.allRepos) { allReposResult ->
            when (allReposResult) {
                is ApiResult.Success<List<Repo>> -> {
                    val mostStarred = allReposResult.data
                        .sortedByDescending { it.stars }
                        .take(REPOS_TO_DISPLAY)
                    ApiResult.Success(mostStarred, allReposResult.httpStatus)
                }
                else -> allReposResult
            }
        }

    val selectedOrganization = MutableLiveData<Organization>()

    fun getReposForOrg(organization: Organization) = reposRepository.getReposForOrg(organization)

    override fun onCleared() {
        super.onCleared()
        reposRepository.cancelGetReposCall()
    }

    companion object {
        const val REPOS_TO_DISPLAY = 4
        private const val TAG = "OrgDetailsViewModel"
    }
}
