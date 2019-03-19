package com.nrojiani.githuborgsearch.ui.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.di.MyApplication
import com.nrojiani.githuborgsearch.model.Organization
import com.nrojiani.githuborgsearch.ui.shared.OrgDetailsDisplayerFragment
import com.nrojiani.githuborgsearch.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject

/**
 * Fragment associated with searching for an Organization and displaying
 * details about the Organization (or error messages if not found).
 */
class SearchFragment : Fragment(), OrgDetailsDisplayerFragment {

    private val TAG by lazy { this::class.java.simpleName }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: SearchViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MyApplication.getApplicationComponent(context).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory)
            .get(SearchViewModel::class.java)

        Log.d(TAG, "onViewCreated: savedInstanceState: $savedInstanceState")

        savedInstanceState?.let {
            viewModel.restoreFromBundle(savedInstanceState)
            viewModel.getOrganization().value?.let {
                searchOrgCardView.isVisible = true
            }
        }

        Log.d(
            TAG,
            "onViewCreated: after viewModel.restoreFromBundle - viewModel.getOrg ${viewModel.getOrganization().value}"
        )
        viewModel.getOrganization().value?.let { org ->
            Log.d(TAG, "onViewCreated: show card result (after process death)")
            showOrgCardView(org)
            // showOrgCardView(this, org)
        }

        initViews()
        observeViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (searchEditText.text.toString().isNotBlank()) {
            outState.putString(SearchViewModel.KEY_ORG_SEARCH_INPUT, searchEditText.text.toString())
        }
        viewModel.getOrganization().value?.let { org ->
            outState.putParcelable(SearchViewModel.KEY_ORGANIZATION, org)
            Log.d(
                TAG, "onSaveInstanceState: parcelable org added to bundle:" +
                        "${outState.getParcelable<Organization>(SearchViewModel.KEY_ORGANIZATION)}"
            )
        }

        viewModel.saveToBundle(outState)
    }

    private fun initViews() {
        // TODO: listen for Android keyboard search button
        // TODO: tap outside of keyboard dismisses it

        // TODO does nothing

        // When search button is clicked, trigger callback
        searchButton.setOnClickListener {
            Log.d(TAG, "searchButton onClickListener")

            val orgQuery = searchEditText.text.toString()
            if (orgQuery.isBlank()) {
                searchEditText.error = "Invalid organization name"
            } else {
                // Dismiss Keyboard
                searchEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)

                // Trigger API call
                viewModel.fetchOrgDetails(orgQuery)
            }
        }

        searchOrgCardView.setOnClickListener {
            val selectedOrg = viewModel.getOrganization().value!!// dev. error if null.
            Log.d(
                TAG, "orgCardView.onClickListener: selectedOrg " +
                        "(will be passed to detail frag): $selectedOrg"
            )
            val action = SearchFragmentDirections
                .actionSearchFragmentToOrgDetailsFragment(selectedOrg)
            findNavController().navigate(action)
        }
    }

    private fun observeViewModel() {
        viewModel.getOrganization().observe(this, Observer { org: Organization? ->
            if (org == null) {
                // DEBUG
                Log.d(TAG, "observeViewModel: Observer<Organization?> - org was null")
            }

            org?.let {
                progressBar.isInvisible = true
                errorTextView.isVisible = false
                showOrgCardView(org)
            }
        })

        // Error message
        viewModel.getOrgLoadErrorMessage().observe(this, Observer { errorMessage: String? ->
            when {
                errorMessage.isNullOrBlank() -> {
                    errorTextView.isVisible = false
                    errorTextView.text = ""
                }
                else -> {
                    searchOrgCardView.isInvisible = true
                    errorTextView.isVisible = true
                    errorTextView.text = generateErrorMessage()
                }
            }
        })

        // If loading
        viewModel.isLoading().observe(this, Observer<Boolean> { isLoading ->
            if (isLoading) {
                progressBar.isVisible = true
                errorTextView.isVisible = false
                searchOrgCardView.isInvisible = true
            } else {
                progressBar.isInvisible = true
            }
        })
    }

    private fun generateErrorMessage(): String? = buildString {
        append(getString(R.string.api_error_loading_org))
        viewModel.getOrgLoadErrorMessage().value?.let { e ->
            append(":\n$e")
        }
    }
}
