package com.nrojiani.githuborgsearch.ui.orgdetails

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
import com.nrojiani.githuborgsearch.di.MyApplication
import com.nrojiani.githuborgsearch.model.Organization
import com.nrojiani.githuborgsearch.model.Repo
import com.nrojiani.githuborgsearch.ui.shared.OrgDetailsDisplayerFragment
import com.nrojiani.githuborgsearch.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.screen_list.*
import javax.inject.Inject

/**
 * Fragment which displays the top 3 (most-starred) repos for
 * an organization.
 */
class OrgDetailsFragment : Fragment(), OrgDetailsDisplayerFragment {

    private val TAG by lazy { this::class.java.simpleName }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: OrgDetailsViewModel

    // TODO
    fun onRepoSelected(repo: Repo) {
        Log.d(TAG, "onRepoSelected(repo = $repo)")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MyApplication.getApplicationComponent(context).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_org_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory)
            .get(OrgDetailsViewModel::class.java)

        viewModel.restoreFromBundle(savedInstanceState)

        // DEBUG
        requireNotNull(viewModel.getSelectedOrganization().value) {
            "ERROR - selectedOrganization is null after restoreFromBundle"
        }

        val repos: List<Repo>? = viewModel.getAllRepos().value
         if (repos.isNullOrEmpty()) {
             Log.d(TAG, "onViewCreated: repos null or empty. calling viewModel.loadReposForOrg()")
             viewModel.getSelectedOrganization().value?.let {
                 showOrgCardView(it)
                 viewModel.loadReposForOrg(it)
             }
         }

        // TODO set click listeners on repo cards

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

    private fun observeViewModel() {
        viewModel.getSelectedOrganization().observe(this, Observer { org: Organization? ->
            Log.d(TAG, "(Observer): OrgDetailsFragment getSelectedOrganization() changed to $org")
            org?.let {
                showOrgCardView(org)
            }
        })

        viewModel.getAllRepos().observe(this, Observer { repos ->
            Log.d(TAG, "(Observer): OrgDetailsViewModel getAllRepos() changed to $repos")
            if (repos.isNullOrEmpty()) {
            } else {
                recyclerView.isVisible = true
            }
        })

        // Error message
        viewModel.getRepoLoadErrorMessage().observe(this, Observer { errorMessage ->
            Log.d(TAG, "(Observer): OrgDetailsViewModel getRepoLoadErrorMessage() changed to $errorMessage")
            if (errorMessage.isNullOrBlank()) {
                repoErrorTextView.isVisible = false
            } else {
                recyclerView.isVisible = false
                repoErrorTextView.isVisible = true
                repoErrorTextView.text = getString(R.string.api_error_loading_repos)
            }
        })

        // If loading
        viewModel.isLoading().observe(this, Observer<Boolean> { isLoading ->
            Log.d(TAG, "(Observer): OrgDetailsViewModel isLoading() changed to $isLoading")
            repoProgressBar.isVisible = isLoading
            if (isLoading) {
                repoErrorTextView.isVisible = false
                recyclerView.isVisible = false
            }
        })
    }


    // LIFECYCLE



    }

}