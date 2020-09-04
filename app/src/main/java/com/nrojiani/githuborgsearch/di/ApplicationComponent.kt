package com.nrojiani.githuborgsearch.di

import com.nrojiani.githuborgsearch.controllers.fragments.OrgDetailsFragment
import com.nrojiani.githuborgsearch.controllers.fragments.SearchFragment
import com.nrojiani.githuborgsearch.network.NetworkModule
import com.nrojiani.githuborgsearch.viewmodel.ViewModelModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ContextModule::class,
        NetworkModule::class,
        ViewModelModule::class
    ]
)
interface ApplicationComponent {
    fun inject(searchFragment: SearchFragment)
    fun inject(orgDetailsFragment: OrgDetailsFragment)
}
