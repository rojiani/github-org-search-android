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
import com.nrojiani.githuborgsearch.ui.search.SearchViewModel
import com.nrojiani.githuborgsearch.ui.shared.OrgDetailsDisplayerFragment
import com.nrojiani.githuborgsearch.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.screen_list.*
import javax.inject.Inject

/**
 * Fragment which displays the top 3 (most-starred) repos for
 * an organization.
 */
class OrgDetailsFragment : Fragment(), OrgDetailsDisplayerFragment, RepoSelectedListener {


    private val TAG by lazy { this::class.java.simpleName }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: OrgDetailsViewModel
    private var selectedOrg: Organization? = null


    override fun onRepoSelected(repo: Repo) {
        // TODO
        Log.d(TAG, "onRepoSelected")
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


        // TODO restore on config changes

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory)
            .get(OrgDetailsViewModel::class.java)

        arguments?.let {
            val selectedOrgArg = OrgDetailsFragmentArgs.fromBundle(it).selectedOrg
            selectedOrg = selectedOrgArg
            Log.e(TAG, "onViewCreated: Fragment arg selectedOrg: $selectedOrgArg")
            viewModel.setSelectedOrganization(selectedOrgArg)

            // Fetch repos for the Organization
            viewModel.fetchReposForOrg(selectedOrgArg.login)

            // Display org cardview
            showOrgCardView(selectedOrgArg)
        } ?: Log.e(TAG, "onViewCreated: arguments (Bundle.arguments) null")

        // TODO set click listeners on repo cards

        // RecyclerView setup
        recyclerView.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )
        recyclerView.adapter = RepoListAdapter(viewModel, this, this)
        recyclerView.layoutManager = LinearLayoutManager(context)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.getRepos().observe(this, Observer { repos ->
            Log.d(TAG, "observeViewModels: getRepos() - Observer<List<Repo>?>")
            if (repos != null) {
                recyclerView.isVisible = true
            }
        })


        // Error message
        viewModel.getRepoLoadErrorMessage().observe(this, Observer { errorMessage ->
            if (errorMessage != null) {
                recyclerView.isVisible = false
                repoErrorTextView.isVisible = true
                repoErrorTextView.text = getString(R.string.api_error_loading_repos)
            } else {
                repoErrorTextView.isVisible = false
                repoErrorTextView.text = null
            }
        })

        // If loading
        viewModel.isLoading().observe(this, Observer<Boolean> { isLoading ->
            repoProgressBar.isVisible = isLoading
            if (isLoading) {
                repoErrorTextView.isVisible = false
                recyclerView.isVisible = false
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Restore selectedOrg

        viewModel.getOrganization().value?.let { org ->
            outState.putParcelable(SearchViewModel.KEY_ORGANIZATION, org)
            Log.d(TAG, "onSaveInstanceState: parcelable org added to bundle:" +
                        "${outState.getParcelable<Organization>(SearchViewModel.KEY_ORGANIZATION)}"
            )
        }

        // TODO
//        if (allRepos.size >= REPO_COUNT_TO_SHOW) {
//            val (repo1, repo2, repo3) = mostStarredRepos
//            outState.putParcelable(KEY_REPO_1, repo1)
//            outState.putParcelable(KEY_REPO_2, repo2)
//            outState.putParcelable(KEY_REPO_3, repo3)
//        } else {
//            Log.d(TAG, "onSaveInstanceState: < 3 repos. allRepos: $allRepos")
//        }

        viewModel.saveToBundle(outState)
    }

}