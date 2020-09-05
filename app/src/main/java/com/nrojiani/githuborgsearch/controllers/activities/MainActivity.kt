package com.nrojiani.githuborgsearch.controllers.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.controllers.fragments.SearchFragment
import com.nrojiani.githuborgsearch.viewmodel.OrgDetailsViewModel
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    /**
     * The [OrgDetailsViewModel] is shared by [SearchFragment] and [OrgDetailsFragment]
     * because [SearchFragment] will set the selected org and prefetch an org's repos
     * before transitioning to the [OrgDetailsFragment].
     */
    private val orgDetailsViewModel: OrgDetailsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add<SearchFragment>(R.id.fragment_container)
                .commit()
        }
    }

    fun openWebContent(url: String) {
        val webDelegate = WebContentDelegate(this)
        webDelegate.openWebContent(url)
    }
}
