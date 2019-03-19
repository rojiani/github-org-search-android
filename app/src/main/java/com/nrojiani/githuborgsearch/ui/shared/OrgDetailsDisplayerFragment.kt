package com.nrojiani.githuborgsearch.ui.shared

import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.model.Organization
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_org.view.*

/**
 * A Fragment that can display a CardView widget for a GitHub Organization.
 */
internal interface OrgDetailsDisplayerFragment {

    /**
     * Default implementation.
     * Show the Organization search result in a CardView within a fragment.
     */
    fun <T : Fragment> T.showOrgCardView(org: Organization) {
        val orgCardViewResId = when (this::class.java.simpleName) {
            "SearchFragment" -> R.id.searchOrgCardView
            "OrgDetailsFragment" -> R.id.detailsOrgCardView
            else -> throw RuntimeException("Unknown Fragment")
        }
        val orgCardView = activity
            ?.findViewById<MaterialCardView>(orgCardViewResId)
            ?: throw RuntimeException("Org CardView resolution failed")


        orgCardView.isVisible = true
        orgCardView.apply {
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
}
