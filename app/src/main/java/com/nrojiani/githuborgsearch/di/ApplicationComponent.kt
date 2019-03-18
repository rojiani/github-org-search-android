package com.nrojiani.githuborgsearch.di

import com.nrojiani.githuborgsearch.network.NetworkModule
import com.nrojiani.githuborgsearch.ui.orgrepos.OrgDetailsFragment
import com.nrojiani.githuborgsearch.ui.search.SearchFragment
import com.nrojiani.githuborgsearch.viewmodel.ViewModelModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    NetworkModule::class,
    ViewModelModule::class
])
interface ApplicationComponent {
    fun inject(searchFragment: SearchFragment)
    fun inject(orgDetailsFragment: OrgDetailsFragment)
}