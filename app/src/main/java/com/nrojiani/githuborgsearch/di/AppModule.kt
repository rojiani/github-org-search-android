@file:JvmName("AppModule")

package com.nrojiani.githuborgsearch.di

import com.nrojiani.githuborgsearch.controllers.fragments.OrgDetailsFragment
import com.nrojiani.githuborgsearch.controllers.fragments.SearchFragment
import com.nrojiani.githuborgsearch.data.repository.OrganizationRepository
import com.nrojiani.githuborgsearch.data.repository.ReposRepository
import com.nrojiani.githuborgsearch.viewmodel.OrgDetailsViewModel
import com.nrojiani.githuborgsearch.viewmodel.SearchViewModel
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * AppModule:
 * - Dependencies: NetworkModule
 */
val AppModule = module {

    /* Repositories */
    single {
        OrganizationRepository(gitHubService = get())
    }

    single {
        ReposRepository(gitHubService = get())
    }

    /* Fragments */
    fragment {
        SearchFragment(picasso = get())
    }

    fragment {
        OrgDetailsFragment(picasso = get())
    }

    /* ViewModels */
    viewModel {
        SearchViewModel(orgRepository = get())
    }

    viewModel {
        OrgDetailsViewModel(reposRepository = get())
    }
}
