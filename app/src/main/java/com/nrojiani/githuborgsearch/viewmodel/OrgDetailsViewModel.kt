package com.nrojiani.githuborgsearch.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.nrojiani.githuborgsearch.data.model.Organization
import com.nrojiani.githuborgsearch.data.model.Repo
import com.nrojiani.githuborgsearch.data.repository.ReposRepository
import com.nrojiani.githuborgsearch.network.Resource
import com.nrojiani.githuborgsearch.network.Status
import javax.inject.Inject


/**
 * ViewModel for the view displaying Org Details & most starred repos.
 */
class OrgDetailsViewModel
@Inject constructor(
    private val reposRepository: ReposRepository
) : ViewModel() {

    /**
     * The top `n` most starred repos for the [selectedOrganization] in decreasing order,
     * where `n` is [NUM_REPOS_TO_DISPLAY].
     */
    val topRepos: LiveData<Resource<List<Repo>>> =
        Transformations.map(reposRepository.allRepos) { allReposResource ->
            when (allReposResource.status) {
                Status.SUCCESS -> {
                    val mostStarred = allReposResource.data
                        ?.sortedByDescending { it.stars }
                        ?.take(NUM_REPOS_TO_DISPLAY)
                    Resource.success(mostStarred)
                }
                else -> allReposResource
            }
        }

    val selectedOrganization = MutableLiveData<Organization>()

    fun getReposForOrg(organization: Organization) = reposRepository.getReposForOrg(organization)

    override fun onCleared() {
        super.onCleared()
        reposRepository.cancelGetReposCall()
    }

    companion object {
        const val NUM_REPOS_TO_DISPLAY = 3
    }

}
