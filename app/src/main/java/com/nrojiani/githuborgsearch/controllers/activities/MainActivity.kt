package com.nrojiani.githuborgsearch.controllers.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.controllers.fragments.SearchFragment

class MainActivity : AppCompatActivity() {

    private val TAG by lazy { this::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, SearchFragment())
                .commit()
        }
    }

    fun openWebContent(url: String) {
        Log.d(TAG, "openWebContent")
        val webDelegate = WebContentDelegate(this)
        webDelegate.openWebContent(url)
    }
}
