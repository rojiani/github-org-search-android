package com.nrojiani.githuborgsearch.ui.search

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.di.MyApplication
import com.nrojiani.githuborgsearch.model.Organization
import com.nrojiani.githuborgsearch.ui.orgdetails.OrgDetailsFragment
import com.nrojiani.githuborgsearch.ui.orgdetails.OrgDetailsViewModel
import com.nrojiani.githuborgsearch.viewmodel.ViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_org_full.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.widget_search_bar.*
import javax.inject.Inject

/**
 * Fragment associated with searching for an Organization and displaying
 * details about the Organization (or error messages if not found).
 */
class SearchFragment : Fragment() {

    private val TAG by lazy { this::class.java.simpleName }

    @Inject lateinit var viewModelFactory: ViewModelFactory
    @Inject lateinit var picasso: Picasso

    private lateinit var viewModel: SearchViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach")
        MyApplication.getApplicationComponent(context).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory)
            .get(SearchViewModel::class.java)

        Log.d(TAG, "onViewCreated: savedInstanceState: $savedInstanceState")

        savedInstanceState?.let {
            viewModel.restoreFromBundle(savedInstanceState)
            viewModel.getOrganization().value?.let {
                orgCardView.isVisible = true
            }
            Log.d(
                TAG, "onViewCreated: after viewModel.restoreFromBundle, " +
                        "org: ${viewModel.getOrganization().value}"
            )
        }

        // TODO may be unnecessary
        viewModel.getOrganization().value?.let(this::showOrgDetails)

        initViews()
        observeViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        Log.d(TAG, "onSaveInstanceState: outState (Bundle): $outState")

        // TODO: Use Data Binding on searchEditText. Its text should be in ViewModel.
        searchEditText?.text?.toString()?.run {
            if (isNotBlank()) {
                outState.putString(SearchViewModel.KEY_ORG_SEARCH_INPUT, this)
            }
        }

        viewModel.saveToBundle(outState)
    }

    private fun initViews() {
        /* Trigger GitHub Org search call when either the Search button is pressed
           or the search key is pressed on keyboard */
        searchButton.setOnClickListener { performSearch() }
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    performSearch()
                    true
                }
                else -> false
            }
        }

        // Tap outside of text field dismisses keyboard
        searchFragment.setOnClickListener {
            activity?.let(this::hideSoftKeyBoard)
        }

        orgCardView.setOnClickListener {
            viewModel.getOrganization()?.value?.let {
                onOrgSelected(it)
            }
        }
    }

    private fun performSearch() {
        val orgQuery = searchEditText.text.toString().trim()
        if (orgQuery.isBlank()) {
            searchEditText.error = "Please enter an organization name"
        } else {
            // Dismiss Keyboard
            searchEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)

            // Fetch Org info
            viewModel.loadOrgDetails(orgQuery)
        }
    }

    private fun showOrgDetails(org: Organization) {
        orgCardView.isVisible = true

        orgCardView.apply {
            picasso.load(org.avatarUrl).into(orgImageView)

            orgNameTextView.text = org.name
            orgLoginTextView.text = org.login

            if (org.location.isNullOrBlank()) {
                orgLocationTextView.isVisible = false
            } else {
                orgLocationTextView.text = org.location
                orgLocationTextView.isVisible = true
            }

            if (org.blogUrl.isNullOrBlank()) {
                orgBlogTextView.isInvisible = true
            } else {
                orgBlogTextView.text = org.blogUrl
                orgBlogTextView.isVisible = true
            }

            if (org.description.isNullOrBlank()) {
                orgDescriptionTextView.isVisible = false
            } else {
                orgDescriptionTextView.text = org.description
                orgDescriptionTextView.isVisible = true
            }
        }
    }

    private fun onOrgSelected(org: Organization) {
        Log.d(TAG, "onOrgSelected: org = $org")
        // Scope the ViewModel to the Activity, not the fragment
        val orgDetailsViewModel = ViewModelProviders.of(activity!!, viewModelFactory)
            .get(OrgDetailsViewModel::class.java)

        // set the selected organization in that ViewModel
        orgDetailsViewModel.setSelectedOrganization(org)

        // Replace SearchFragment with OrgDetailsFragment
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, OrgDetailsFragment())
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun observeViewModel() {
        viewModel.getOrganization().observe(this, Observer { org: Organization? ->
            Log.d(TAG, "(Observer) SearchViewModel getSelectedOrganization() changed to $org")

            org?.let {
                progressBar.isInvisible = true
                errorTextView.isVisible = false
                showOrgDetails(org)
            }
        })

        // Error message
        viewModel.getOrgLoadErrorMessage().observe(this, Observer { errorMessage: String? ->
            Log.d(TAG,
                "(Observer) SearchViewModel getOrgLoadErrorMessage() changed to $errorMessage"
            )
            when {
                errorMessage.isNullOrBlank() -> {
                    errorTextView.isVisible = false
                    errorTextView.text = ""
                }
                else -> {
                    orgCardView.isInvisible = true
                    errorTextView.isVisible = true
                    errorTextView.text = generateErrorMessage()
                }
            }
        })

        // If loading
        viewModel.isLoading().observe(this, Observer<Boolean> { isLoading ->
            Log.d(TAG, "(Observer) SearchViewModel isLoading() changed to $isLoading")
            if (isLoading) {
                progressBar.isVisible = true
                errorTextView.isVisible = false
                orgCardView.isInvisible = true
            } else {
                progressBar.isInvisible = true
            }
        })
    }

    private fun generateErrorMessage(): String? = buildString {
        append("Error: ")
        val msg = viewModel.getOrgLoadErrorMessage().value
            ?: "Unknown (error message not provided by GitHub)"
        append(msg)
    }

    private fun hideSoftKeyBoard(parentActivity: Activity) {
        val inputMethodManager = parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE)
                as? InputMethodManager

        val currentFocus = parentActivity.currentFocus ?: return
        inputMethodManager?.takeIf { it.isAcceptingText }
            ?.apply { hideSoftInputFromWindow(currentFocus.windowToken, 0) }
    }
}
