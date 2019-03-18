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
import com.nrojiani.githuborgsearch.viewmodel.ViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_org.view.*
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject

/**
 * Fragment associated with searching for an Organization and displaying
 * details about the Organization (or error messages if not found).
 */
class SearchFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory)
            .get(SearchViewModel::class.java)

        savedInstanceState?.let {
            viewModel.restoreFromBundle(savedInstanceState)
        }

        initViews()
        observeViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SearchViewModel.ORG_SEARCH_INPUT_KEY, searchEditText.text.toString())
        viewModel.saveToBundle(outState)
    }

    private fun initViews() {
        // TODO: listen for Android keyboard search button
        // TODO: tap outside of keyboard dismisses it
        searchButton.setOnEditorActionListener { textView, actionId, keyEvent ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    Log.d(TAG, "setOnEditorActionListener - IME_ACTION_DONE")
                    true
                }
                else -> {
                    Log.d(TAG, "setOnEditorActionListener - action: $actionId")
                    false
                }
            }
        }

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

        orgCardView.setOnClickListener {
            Log.d(TAG, "orgCardView onClickListener (unimplemented)")
//            val orgName = orgCardNameTextView.text.toString()
//            Log.d(TAG, "orgCardView: orgName (will be passed to detail frag): $orgName")
            val orgNameArg = viewModel.getOrganization().value?.name ?: ""
            Log.d(TAG, "orgCardView: orgName (will be passed to detail frag): $orgNameArg")
            val action =
                SearchFragmentDirections.actionSearchFragmentToOrgDetailsFragment(orgNameArg)
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
                errorTextView.isInvisible = true
                showOrgCardResult(org)
            }
        })

        // Error message
        viewModel.getOrgLoadErrorMessage().observe(this, Observer { errorMessage: String? ->
            when {
                errorMessage.isNullOrBlank() -> {
                    errorTextView.isInvisible = true
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
            if (isLoading) {
                progressBar.isVisible = true
                errorTextView.isInvisible = true
                orgCardView.isInvisible = true
            } else {
                progressBar.isInvisible = true
            }
        })
    }

    /**
     * Show the Organization result in a CardView.
     */
    private fun showOrgCardResult(org: Organization) {
        orgCardView.isVisible = true

        orgCardView.apply {
            orgCardNameTextView.text = org.name
            orgCardLoginTextView.text = org.login
            Picasso.with(context)
                .load(org.avatarUrl)
                .into(orgCardImageView)
        }
    }

    private fun generateErrorMessage(): String? = buildString {
        append(getString(R.string.api_error_loading_org))
        viewModel?.getOrgLoadErrorMessage()?.value?.let { e ->
            append(":\n$e")
        }
    }
}
