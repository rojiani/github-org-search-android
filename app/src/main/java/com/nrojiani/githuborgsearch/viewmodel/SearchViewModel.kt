package com.nrojiani.githuborgsearch.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nrojiani.githuborgsearch.data.model.Organization
import com.nrojiani.githuborgsearch.data.repository.OrganizationRepository
import com.nrojiani.githuborgsearch.network.responsehandler.ApiResult
import javax.inject.Inject

class SearchViewModel
@Inject constructor(
    private val orgRepository: OrganizationRepository
) : ViewModel() {

    val organization: LiveData<ApiResult<Organization>> = orgRepository.organization

    /**
     * Try to retrieve the details for a GitHub Organization.
     */
    fun loadOrgDetails(searchInput: String) = orgRepository.getOrganization(searchInput)

    override fun onCleared() {
        super.onCleared()
        orgRepository.cancelGetOrganizationCall()
    }

}


