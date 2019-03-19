package com.nrojiani.githuborgsearch.ui.orgdetails

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.di.MyApplication
import com.nrojiani.githuborgsearch.model.Organization
import com.nrojiani.githuborgsearch.viewmodel.ViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_org.*
import kotlinx.android.synthetic.main.card_org.view.*
import kotlinx.android.synthetic.main.fragment_org_details.*
import kotlinx.android.synthetic.main.fragment_search.*
import java.lang.RuntimeException
import javax.inject.Inject

/**
 * Fragment which displays the top 3 (most-starred) repos for
 * an organization.
 */
class OrgDetailsFragment : Fragment() {

    private val TAG by lazy { this::class.java.simpleName }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var orgDetailsViewModel: OrgDetailsViewModel

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

        orgDetailsViewModel = ViewModelProviders.of(activity!!, viewModelFactory)
            .get(OrgDetailsViewModel::class.java)

        arguments?.let {
            val selectedOrg = OrgDetailsFragmentArgs
                .fromBundle(it).selectedOrg
            Log.e(TAG, "onViewCreated: Fragment arg selectedOrg: $selectedOrg")
            orgDetailsViewModel.setSelectedOrganization(selectedOrg)


            // Fetch repos for the Organization
            orgDetailsViewModel.fetchReposForOrg(selectedOrg.name)

            // Show org card
            showOrgCardResult(selectedOrg)
        } ?: Log.e(TAG, "onViewCreated: arguments (Bundle.arguments) null")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // TODO
    }


    /**
     * TODO - extract duplicated
     * Show the Organization search result in a CardView.
     */
    private fun showOrgCardResult(org: Organization) {
        orgDetailsCardView.apply {
            Picasso.with(context)
                .load(org.avatarUrl)
                .into(orgCardImageView)

            orgCardNameTextView.text = org.name
            orgCardLoginTextView.text = org.login

            if (org.location.isNullOrBlank()) {
                orgCardLocationTextView.isVisible = false
            } else {
                orgCardLocationTextView.text = org.location
                orgCardLocationTextView.isVisible = true
            }

            if (org.blogUrl.isNullOrBlank()) {
                orgCardBlogTextView.isInvisible = true
            } else {
                orgCardBlogTextView.text = org.blogUrl
                orgCardBlogTextView.isVisible = true
            }

            if (org.description.isNullOrBlank()) {
                orgCardDetailsTextView.isVisible = false
            } else {
                orgCardDetailsTextView.text = org.description
                orgCardDetailsTextView.isVisible = true
            }
        }
    }

    companion object {
        private const val REPO_COUNT_TO_SHOW = 3
    }

}