package com.nrojiani.githuborgsearch.ui.orgdetails

import androidx.lifecycle.ViewModel
import com.nrojiani.githuborgsearch.network.GitHubService
import javax.inject.Inject

/**
 * TODO
 */
class OrgDetailsViewModel
@Inject constructor(
    private val gitHubService: GitHubService
) : ViewModel() {

    private val TAG by lazy { this::class.java.simpleName }

    // TODO
}
