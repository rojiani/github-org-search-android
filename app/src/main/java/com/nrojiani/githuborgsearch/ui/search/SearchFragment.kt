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
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.di.MyApplication
import com.nrojiani.githuborgsearch.model.Organization
import com.nrojiani.githuborgsearch.viewmodel.ViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_org.*
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

        viewModel.restoreFromBundle(savedInstanceState)

        initViews()
        observeViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.saveToBundle(outState)
    }

    private fun initViews() {
        // TODO: listen for Android keyboard search button


        // When search button is clicked, trigger callback
        searchButton.setOnClickListener {
            Log.d(TAG, "searchButton onClickListener")

            val orgQuery = searchEditText.text.toString()
            if (orgQuery.isNullOrBlank()) {
                searchEditText.error = "Invalid organization name"
            } else {
                // Dismiss Keyboard
                searchEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)

                // Trigger API call
                val searchViewModel = ViewModelProviders.of(activity!!, viewModelFactory)
                    .get(SearchViewModel::class.java)
                // searchViewModel.setOrganization(repo)
                searchViewModel.fetchOrgDetails(orgQuery)
            }
        }

        orgCardView.setOnClickListener {
            Log.d(TAG, "orgCardView onClickListener (unimplemented)")
            val orgName = orgCardNameTextView.text.toString()
            Log.d(TAG, "orgCardView: orgName (will be passed to detail frag): $orgName")
            // TODO generated name change
            //val action = SearchFragmentDirections.actionSearchFragmentToOrgDetailsFragment(orgName)
            // findNavController().navigate(action)
        }
    }

    private fun observeViewModel() {
        viewModel.getOrgSearchInput().observe(this, Observer<String> { query ->
            // TODO trigger on input?
            Log.d(TAG, "observeViewModel - orgSearchInput: $query")
        })

        viewModel.getOrganization().observe(this, Observer<Organization> { org ->
            progressBar.isInvisible = true
            errorTextView.isInvisible = true
            showOrgCardResult(org)
        })


        // Error message
        viewModel.getOrgLoadError().observe(this, Observer<Boolean> { hasError ->
            if (hasError) {
                orgCardView.isInvisible = true
                errorTextView.isVisible = true
                errorTextView.text = getString(R.string.api_error_loading_org)
            } else {
                errorTextView.isInvisible = true
                errorTextView.text = null
            }
        })

        // If loading
        viewModel.getLoading().observe(this, Observer<Boolean> { isLoading ->
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
}
