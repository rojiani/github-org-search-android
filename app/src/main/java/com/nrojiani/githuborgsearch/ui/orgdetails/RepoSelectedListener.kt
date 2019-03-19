package com.nrojiani.githuborgsearch.ui.orgdetails

import com.nrojiani.githuborgsearch.model.Repo

interface RepoSelectedListener {
    fun onRepoSelected(repo: Repo)
}
