package com.nrojiani.githuborgsearch.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.nrojiani.githuborgsearch.R

class MainActivity : AppCompatActivity() {

    private val TAG by lazy { this::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate")

        if (savedInstanceState == null) {
            val host = NavHostFragment.create(R.navigation.nav_graph)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, host)
                .setPrimaryNavigationFragment(host)
                .commit()
        }
    }
}
