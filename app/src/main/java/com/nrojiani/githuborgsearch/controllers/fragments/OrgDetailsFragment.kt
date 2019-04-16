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
import com.nrojiani.githuborgsearch.controllers.activities.MainActivity
import com.nrojiani.githuborgsearch.data.model.Organization
import com.nrojiani.githuborgsearch.data.model.Repo
import com.nrojiani.githuborgsearch.di.MyApplication
import com.nrojiani.githuborgsearch.viewmodel.OrgDetailsViewModel
import com.nrojiani.githuborgsearch.viewmodel.ViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_org_condensed.*
import kotlinx.android.synthetic.main.fragment_org_details.*
import kotlinx.android.synthetic.main.screen_list.*
import javax.inject.Inject

/**
 * Fragment which displays the top 3 (most-starred) repos for
 * an organization.
 */
class OrgDetailsFragment : Fragment() {

    private val TAG by lazy { this::class.java.simpleName }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var picasso: Picasso

    private lateinit var viewModel: OrgDetailsViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MyApplication.getApplicationComponent(context).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_org_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory)
            .get(OrgDetailsViewModel::class.java)

        // If selectedOrganization (LiveData) is null, the ViewModel was destroyed.
        // Restore from Bundle. Otherwise we don't need to do anything.
        restoreFromBundle(savedInstanceState)

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
        viewModel.selectedOrganization.value?.let { org ->
            outState.putParcelable(KEY_SELECTED_ORGANIZATION, org)
        }
    }

    private fun restoreFromBundle(savedInstanceState: Bundle?) {
        if (viewModel.selectedOrganization.value == null) {
            savedInstanceState?.getParcelable<Organization>(KEY_SELECTED_ORGANIZATION)?.let {
                viewModel.selectedOrganization.value = it
            }
        }
    }

    private fun onRepoSelected(repo: Repo) =
        (activity as MainActivity).openWebContent(repo.repoUrl)

    private fun showCondensedOrgDetails(org: Organization) {
        org.apply {
            picasso.load(avatarUrl).into(orgAvatarImageView)
            condensedOrgNameTextView.text = name
            condensedOrgLoginTextView.text = getString(R.string.org_login_condensed, login)
        }
    }

    private fun observeViewModel() {
        viewModel.selectedOrganization.observe(this, Observer { org: Organization? ->
            Log.d(TAG, "(Observer): selectedOrganization => $org")
            org?.let { newOrg ->
                viewModel.getReposForOrg(newOrg)
                showCondensedOrgDetails(newOrg)
            }
        })

        viewModel.repoLoadErrorMessage.observe(this, Observer { errorMessage ->
            Log.d(TAG, "(Observer): repoLoadErrorMessage => $errorMessage")
            if (errorMessage.isNullOrBlank()) {
                repoErrorTextView.isVisible = false
            } else {
                repoErrorTextView.isVisible = true
                repoErrorTextView.text = getString(R.string.api_error_loading_repos)
            }
        })

        viewModel.isLoadingRepos.observe(this, Observer<Boolean> { isLoading ->
            Log.d(TAG, "(Observer): isLoadingOrg => $isLoading")
            repoProgressBar.isVisible = isLoading
            if (isLoading) {
                repoErrorTextView.isVisible = false
            }
        })

        viewModel.topRepos.observe(this, Observer { topRepos ->
            // If an org. exists but owns 0 repos, display an error message (e.g. 'nytime')
            orgOwnsNoReposErrorMessage.isVisible = topRepos?.isEmpty() ?: false
        })
    }

    companion object {
        const val KEY_SELECTED_ORGANIZATION = "selected_org_details"
    }
}