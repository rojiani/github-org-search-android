package com.nrojiani.githuborgsearch.controllers.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.data.model.Organization
import com.nrojiani.githuborgsearch.databinding.CardOrgFullBinding
import com.nrojiani.githuborgsearch.databinding.FragmentSearchBinding
import com.nrojiani.githuborgsearch.extensions.displayTextOrHide
import com.nrojiani.githuborgsearch.network.responsehandler.ApiResult
import com.nrojiani.githuborgsearch.network.responsehandler.formattedErrorMessage
import com.nrojiani.githuborgsearch.network.responsehandler.responseData
import com.nrojiani.githuborgsearch.viewmodel.OrgDetailsViewModel
import com.nrojiani.githuborgsearch.viewmodel.SearchViewModel
import com.squareup.picasso.Picasso
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Fragment associated with searching for an Organization and displaying
 * details about the Organization (or error messages if not found).
 */
class SearchFragment(private val picasso: Picasso) : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    private var _binding: FragmentSearchBinding? = null
    /** This property is only valid between onCreateView and onDestroyView. */
    private val binding get() = _binding!!

    private lateinit var orgCardViewBinding: CardOrgFullBinding

    /**
     * [OrgDetailsViewModel] reference used for pre-fetching view data for next screen,
     * and setting the selected org.
     */
    private val orgDetailsViewModel: OrgDetailsViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root
        orgCardViewBinding = binding.orgCardView
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: savedInstanceState: $savedInstanceState")

        registerListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /** Set listeners */
    private fun registerListeners() {
        /* Trigger GitHub Org search call when either the Search button is pressed
           or the search key is pressed on keyboard */
        binding.searchBarWidget.searchButton.setOnClickListener { performSearch() }
        binding.searchBarWidget.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    performSearch()
                    true
                }
                else -> false
            }
        }

        // Tap outside of text field dismisses keyboard
        binding.root.setOnClickListener {
            activity?.let(this::hideSoftKeyBoard)
        }

        orgCardViewBinding.root.setOnClickListener {
            val apiResult = viewModel.organization.value
            apiResult.responseData?.let {
                onOrgSelected(it)
            } ?: Log.e(
                TAG,
                "registerListeners: cardView clicked but not success - apiResult = $apiResult"
            )
        }
    }

    private fun observeViewModel() {
        viewModel.organization.observe(viewLifecycleOwner) { orgApiResult ->
            Log.d(TAG, "(Observer) orgApiResult => $orgApiResult")
            orgApiResult?.let {
                updateUI(it)
            } ?: Log.d(TAG, "orgApiResult null")
        }
    }

    /**
     * Update UI in response to change in observed ViewModel data.
     */
    private fun updateUI(apiResult: ApiResult<Organization>) = when (apiResult) {
        is ApiResult.Loading -> {
            binding.progressBar.isVisible = true
            binding.errorTextView.isVisible = false
            orgCardViewBinding.root.isInvisible = true
        }
        is ApiResult.Cancelled -> {
            binding.progressBar.isVisible = false
            binding.errorTextView.isVisible = false
            orgCardViewBinding.root.isInvisible = true
        }
        is ApiResult.Exception -> {
            Log.e(TAG, "updateUI: ApiResult.Exception: $apiResult")
            Log.e(TAG, "stack trace: ${apiResult.throwable.stackTrace}")

            binding.progressBar.isInvisible = true
            orgCardViewBinding.root.isInvisible = true
            displayErrorMessage(apiResult.formattedErrorMessage)
        }
        is ApiResult.Error -> {
            Log.e(TAG, "updateUI: ApiResult.Error: $apiResult")
            binding.progressBar.isInvisible = true
            orgCardViewBinding.root.isInvisible = true
            displayErrorMessage(apiResult.formattedErrorMessage)
        }
        is ApiResult.Success -> {
            val org = apiResult.data
            binding.progressBar.isInvisible = true
            binding.errorTextView.isVisible = false
            showOrgDetails(org)
            prefetchTopRepos(org)
        }
    }

    private fun performSearch() {
        val orgQuery = binding.searchBarWidget.searchEditText.text.toString().trim()

        when {
            orgQuery.isBlank() -> binding.searchBarWidget.searchEditText.error = EMPTY_SEARCH_ERROR_MESSAGE
            else -> {
                // Dismiss Keyboard
                binding.searchBarWidget.searchEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)

                // Fetch Org info
                viewModel.loadOrgDetails(orgQuery)
            }
        }
    }

    private fun showOrgDetails(org: Organization) {
        orgCardViewBinding.root.isVisible = true

        org.apply {
            picasso.load(avatarUrl).into(orgCardViewBinding.orgImageView)

            orgCardViewBinding.orgNameTextView.text = name
            orgCardViewBinding.orgLoginTextView.text = login

            mapOf(
                orgCardViewBinding.orgLocationTextView to location,
                orgCardViewBinding.orgBlogTextView to blogUrl,
                orgCardViewBinding.orgDescriptionTextView to description
            ).forEach { (textView, text) ->
                textView.displayTextOrHide(text)
            }
        }
    }

    private fun prefetchTopRepos(org: Organization) {
        orgDetailsViewModel.getReposForOrg(org)
    }

    private fun onOrgSelected(org: Organization) {
        Log.d(TAG, "onOrgSelected($org)")
        // set the selected organization in that ViewModel
        orgDetailsViewModel.selectedOrganization.value = org

        // Replace SearchFragment with OrgDetailsFragment
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace<OrgDetailsFragment>(R.id.fragment_container)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun hideSoftKeyBoard(parentActivity: Activity) {
        val inputMethodManager = parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE)
            as? InputMethodManager

        val currentFocus = parentActivity.currentFocus ?: return
        inputMethodManager?.takeIf { it.isAcceptingText }
            ?.apply { hideSoftInputFromWindow(currentFocus.windowToken, 0) }
    }

    private fun displayErrorMessage(errorMessage: String?) = if (errorMessage.isNullOrBlank()) {
        binding.errorTextView.isVisible = false
        binding.errorTextView.text = ""
    } else {
        binding.errorTextView.isVisible = true
        binding.errorTextView.text = errorMessage
    }

    companion object {
        private const val TAG = "SearchFragment"
        internal const val EMPTY_SEARCH_ERROR_MESSAGE =
            "Please enter an organization name (e.g., 'nytimes')"
    }
}
