package com.nrojiani.githuborgsearch.ui.shared

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.ui.search.SearchFragment

class MainActivity : AppCompatActivity() {

    private val TAG by lazy { this::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate")

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, SearchFragment())
                .commit()
        }
    }
}
