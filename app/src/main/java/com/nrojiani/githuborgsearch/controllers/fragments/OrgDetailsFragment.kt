package com.nrojiani.githuborgsearch.controllers.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.adapters.RepoListAdapter
import com.nrojiani.githuborgsearch.controllers.activities.MainActivity
import com.nrojiani.githuborgsearch.data.model.Organization
import com.nrojiani.githuborgsearch.data.model.Repo
import com.nrojiani.githuborgsearch.databinding.FragmentOrgDetailsBinding
import com.nrojiani.githuborgsearch.network.responsehandler.ApiResult
import com.nrojiani.githuborgsearch.network.responsehandler.formattedErrorMessage
import com.nrojiani.githuborgsearch.viewmodel.OrgDetailsViewModel
import com.squareup.picasso.Picasso
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Fragment which displays the top (most-starred) repos for
 * an organization.
 */
class OrgDetailsFragment(private val picasso: Picasso) : Fragment() {

    private val viewModel: OrgDetailsViewModel by sharedViewModel()

    private var _binding: FragmentOrgDetailsBinding? = null
    /** This property is only valid between onCreateView and onDestroyView. */
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrgDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // If selectedOrganization (LiveData) is null, the ViewModel was destroyed.
        // Restore from Bundle. Otherwise we don't need to do anything.
        restoreFromBundle(savedInstanceState)

        // RecyclerView setup
        binding.repoList.recyclerView.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )
        binding.repoList.recyclerView.adapter = RepoListAdapter(viewModel, this, ::onRepoSelected)
        binding.repoList.recyclerView.layoutManager = LinearLayoutManager(context)

        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun observeViewModel() {
        viewModel.selectedOrganization.observe(viewLifecycleOwner) { org: Organization? ->
            Log.d(TAG, "(Observer): selectedOrganization => $org")
            org?.let { newOrg ->
                viewModel.getReposForOrg(newOrg)
                showCondensedOrgDetails(newOrg)
            }
        }

        viewModel.topRepos.observe(viewLifecycleOwner) { topReposResource ->
            Log.d(TAG, "(Observer): topRepos => $topReposResource")
            topReposResource?.let { updateUI(it) }
        }
    }

    private fun updateUI(apiResult: ApiResult<List<Repo>>) = when (apiResult) {
        is ApiResult.Loading -> {
            binding.repoList.repoProgressBar.isVisible = true
            binding.repoList.repoErrorTextView.isVisible = false
        }
        is ApiResult.Cancelled -> {
            binding.repoList.repoProgressBar.isVisible = false
            binding.repoList.repoErrorTextView.isVisible = false
        }
        is ApiResult.Exception -> {
            binding.repoList.repoProgressBar.isVisible = false
            displayErrorMessage(apiResult.formattedErrorMessage)
        }
        is ApiResult.Error -> {
            binding.repoList.repoProgressBar.isVisible = false
            displayErrorMessage(apiResult.formattedErrorMessage)
        }
        is ApiResult.Success -> {
            // If an org. exists but owns 0 repos, display an error message (e.g. 'nytime')
            // Other updates handled by RepoListAdapter.
            binding.orgOwnsNoReposErrorMessage.isVisible = apiResult.data.isEmpty()
            binding.repoList.repoProgressBar.isVisible = false
            binding.repoList.repoErrorTextView.isVisible = false
        }
    }

    private fun showCondensedOrgDetails(org: Organization) {
        val condensedOrgViewBinding = binding.condensedOrgView

        org.apply {
            picasso.load(avatarUrl).into(condensedOrgViewBinding.orgAvatarImageView)
            condensedOrgViewBinding.condensedOrgNameTextView.text = name
            condensedOrgViewBinding.condensedOrgLoginTextView.text = getString(R.string.org_login_condensed, login)
        }
    }

    private fun displayErrorMessage(errorMessage: String?) = if (errorMessage.isNullOrBlank()) {
        binding.repoList.repoErrorTextView.isVisible = false
        binding.repoList.repoErrorTextView.text = ""
    } else {
        binding.repoList.repoErrorTextView.isVisible = true
        binding.repoList.repoErrorTextView.text = errorMessage
    }

    companion object {
        private const val TAG = "OrgDetailsFragment"
        private const val KEY_SELECTED_ORGANIZATION = "selected_org_details"
    }
}
