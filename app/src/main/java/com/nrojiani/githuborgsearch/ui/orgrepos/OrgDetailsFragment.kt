package com.nrojiani.githuborgsearch.ui.orgrepos

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.di.MyApplication
import kotlinx.android.synthetic.main.fragment_org_details.*

/**
 * Fragment which displays the top 3 (most-starred) repos for
 * an organization.
 */
class OrgDetailsFragment : Fragment() {

    private val TAG by lazy { this::class.java.simpleName }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MyApplication.getApplicationComponent(context).inject(this)
    }

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