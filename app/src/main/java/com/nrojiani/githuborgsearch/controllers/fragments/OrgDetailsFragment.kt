package com.nrojiani.githuborgsearch.controllers.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.adapters.RepoListAdapter
import com.nrojiani.githuborgsearch.di.MyApplication
import com.nrojiani.githuborgsearch.model.Organization
import com.nrojiani.githuborgsearch.model.Repo
import com.nrojiani.githuborgsearch.controllers.activities.MainActivity
import com.nrojiani.githuborgsearch.viewmodel.OrgDetailsViewModel
import com.nrojiani.githuborgsearch.viewmodel.ViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_org_condensed.view.*
import kotlinx.android.synthetic.main.fragment_org_details.*
import kotlinx.android.synthetic.main.screen_list.*
import javax.inject.Inject

/**
 * Fragment which displays the top 3 (most-starred) repos for
 * an organization.
 */
class OrgDetailsFragment : Fragment() {

    private val TAG by lazy { this::class.java.simpleName }

    @Inject lateinit var viewModelFactory: ViewModelFactory
    @Inject lateinit var picasso: Picasso
    private lateinit var viewModel: OrgDetailsViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MyApplication.getApplicationComponent(context).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_org_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory)
            .get(OrgDetailsViewModel::class.java)

        viewModel.restoreFromBundle(savedInstanceState)

        requireNotNull(viewModel.getSelectedOrganization().value) {
            "ERROR - selectedOrganization is null after restoreFromBundle"
        }

        val repos: List<Repo>? = viewModel.getAllRepos().value
        if (repos.isNullOrEmpty()) {
            viewModel.getSelectedOrganization().value?.let {
                showCondensedOrgDetails(it)
                viewModel.loadReposForOrg(it)
            }
        }

        // RecyclerView setup
        recyclerView.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )
        recyclerView.adapter = RepoListAdapter(viewModel, this, ::onRepoSelected)
        recyclerView.layoutManager = LinearLayoutManager(context)

        observeViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.saveToBundle(outState)
    }

    private fun onRepoSelected(repo: Repo) {
        Log.d(TAG, "onRepoSelected($repo)")
        (activity as MainActivity).openWebContent(repo.repoUrl)
    }

    private fun showCondensedOrgDetails(org: Organization) {
        condensedOrgView.apply {
            picasso.load(org.avatarUrl).into(orgAvatarImageView)

            condensedOrgNameTextView.text = org.name
            condensedOrgLoginTextView.text = "@${org.login}"
        }
    }

    private fun observeViewModel() {
        viewModel.getSelectedOrganization().observe(this, Observer { org: Organization? ->
            Log.d(TAG, "(Observer): getSelectedOrganization() => $org")
            org?.let { newOrg ->
                if (!viewModel.hasTopReposCached(newOrg)) {
                    viewModel.loadReposForOrg(newOrg)
                }
                showCondensedOrgDetails(newOrg)
            }
        })

        viewModel.getAllRepos().observe(this, Observer { repos ->
            Log.d(TAG, "(Observer): getAllRepos() => $repos")
            if (repos.isNullOrEmpty()) {
            } else {
                recyclerView.isVisible = true
            }
        })

        viewModel.getRepoLoadErrorMessage().observe(this, Observer { errorMessage ->
            Log.d(TAG, "(Observer): getRepoLoadErrorMessage() => $errorMessage")
            if (errorMessage.isNullOrBlank()) {
                repoErrorTextView.isVisible = false
            } else {
                recyclerView.isVisible = false
                repoErrorTextView.isVisible = true
                repoErrorTextView.text = getString(R.string.api_error_loading_repos)
            }
        })

        viewModel.isLoading().observe(this, Observer<Boolean> { isLoading ->
            Log.d(TAG, "(Observer): isLoading() => $isLoading")
            repoProgressBar.isVisible = isLoading
            if (isLoading) {
                repoErrorTextView.isVisible = false
                recyclerView.isVisible = false
            }
        })
    }
}