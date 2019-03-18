package com.nrojiani.githuborgsearch.viewmodel

import androidx.lifecycle.ViewModel
import com.nrojiani.githuborgsearch.ui.search.SearchViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(viewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OrgViewModel::class)
    abstract fun bindOrgViewModel(viewModel: OrgViewModel): ViewModel
}