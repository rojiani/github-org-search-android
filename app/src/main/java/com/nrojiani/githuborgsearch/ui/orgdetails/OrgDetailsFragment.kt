package com.nrojiani.githuborgsearch.ui.orgdetails

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.di.MyApplication
import com.nrojiani.githuborgsearch.viewmodel.ViewModelFactory
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

        orgDetailsViewModel = ViewModelProviders.of(activity!!, viewModelFactory)
            .get(OrgDetailsViewModel::class.java)

//        arguments?.let {
//            val orgNameArg = OrgDetailsFragmentArgs.fromBundle(it).orgName
//            orgNameTextView.text = orgNameArg
//
//            // TODO remove Toast
//            Toast.makeText(activity, "orgName argument: $orgNameArg", Toast.LENGTH_SHORT).show()
//        } ?: Log.e(TAG, "onViewCreated: arguments (Bundle.arguments) null")
    }

}