package com.nrojiani.githuborgsearch.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nrojiani.githuborgsearch.R
import kotlinx.android.synthetic.main.fragment_search.*

/**
 * TODO
 */
class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO handle if text entered and submitted without button,
        // TODO remove button

        // Sending data from one fragment to another fragment
        searchButton.setOnClickListener {

            val orgName = searchEditText.text.toString()

            if (orgName.isEmpty()) {
                searchEditText.error = "Please enter a orgName"
            } else {
                // NOTE: MainFragmentDirections object is generated (by the safe args plugin)
                // and thus the projects needs to be built before it can be accessed
                val action =
                    SearchFragmentDirections.actionSearchFragmentToOrgDetailsFragment(orgName)
                findNavController().navigate(action)

                // TODO
                // For buttons, you can also use the Navigation class’s createNavigateOnClickListener()
                // convenience method to navigate to a destination, as shown in the following example:
                // button.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.orgDetailsFragment, null))
                // Navigation.createNavigateOnClickListener(R.id.destinationFragment, null)
            }
        }
    }
}
