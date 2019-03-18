package com.nrojiani.githuborgsearch.ui.orgdetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nrojiani.githuborgsearch.R
import kotlinx.android.synthetic.main.fragment_org_details.*

/**
 * TODO
 */
class OrgDetailsFragment : Fragment() {

    private val TAG by lazy { this::class.java.simpleName }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_org_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val orgNameArg = OrgDetailsFragmentArgs.fromBundle(it).orgName
            orgNameTextView.text = orgNameArg

            // TODO remove
            Toast.makeText(activity, "orgName argument: $orgNameArg", Toast.LENGTH_SHORT).show()
        } ?: Log.e(TAG, "onViewCreated: arguments (Bundle.arguments) null")
    }

}