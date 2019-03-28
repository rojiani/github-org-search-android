package com.nrojiani.githuborgsearch.controllers.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.controllers.fragments.SearchFragment
import com.saurabharora.customtabs.CustomTabFallback
import com.saurabharora.customtabs.extensions.launchWithFallback

class MainActivity : AppCompatActivity() {

    private val TAG by lazy { this::class.java.simpleName }

    // TODO remove
    companion object DebugConstants {
        const val HAS_CHROMIUM = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, SearchFragment())
                .commit()
        }
    }

    /**********************************************************
     * TODO - Move Web stuff into Delegate class
     **********************************************************/

    fun openWebContent(url: String) {
        openChromeCustomTab(Uri.parse(url))
    }

    private fun openChromeCustomTab(uri: Uri) {
        Log.d(TAG, "openChromeCustomTab($uri)")
        val customTabsIntent: CustomTabsIntent = buildCustomTabsIntent()

        customTabsIntent.launchWithFallback(
            activity = this,
            uri = uri,
            fallback = object : CustomTabFallback {
                override fun openUri(activity: Activity, uri: Uri) {
                    Log.d("CustomTabFallback", "openUri(activity: $activity, uri: $uri) callback")
                    openWebView(uri)
                }
            }
        )
    }

    private fun openWebView(uri: Uri) {
        Log.d(TAG, "openWebView($uri)")
        val webViewIntent = Intent(this, WebViewActivity::class.java)
        webViewIntent.putExtra(WebViewActivity.EXTRA_URL, uri.toString())
        startActivity(webViewIntent)
    }

    private fun buildCustomTabsIntent(): CustomTabsIntent {
        val intentBuilder = CustomTabsIntent.Builder()
        // configure CustomTabsIntent
        intentBuilder.apply {
            // set Toolbar colors
            setToolbarColor(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
            setSecondaryToolbarColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.colorPrimaryDark
                )
            )

            // show the page title (as well as the URL)
            setShowTitle(true)

// TODO: requires ChromeTabActionReceiver (removed)
//            // add menu items
//            addMenuItem(
//                getString(R.string.title_menu_1),
//                createPendingIntent(ChromeTabActionReceiver.ACTION_MENU_ITEM_1)
//            )
//            addMenuItem(
//                getString(R.string.title_menu_2),
//                createPendingIntent(ChromeTabActionReceiver.ACTION_MENU_ITEM_2)
//            )
//
//            // set action button
//            // TODO - meaningful action button
//            setActionButton(
//                BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher),
//                "Action Button",
//                createPendingIntent(ChromeTabActionReceiver.ACTION_ACTION_BUTTON)
//            )

            // Animations
            setStartAnimations(
                this@MainActivity,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            setExitAnimations(
                this@MainActivity,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
        }

        return intentBuilder.build()
    }

// TODO: requires ChromeTabActionReceiver (removed)
//    /**
//     * Creates a pending intent to send a broadcast to the [ChromeTabActionReceiver].
//     * @param actionSource The
//     * @return
//     */
//    private fun createPendingIntent(actionSource: Int): PendingIntent {
//        Log.d(TAG, "createPendingIntent")
//        val actionIntent = Intent(this, ChromeTabActionReceiver::class.java)
//        actionIntent.putExtra(ChromeTabActionReceiver.KEY_ACTION_SOURCE, actionSource)
//        return PendingIntent.getBroadcast(this, actionSource, actionIntent, 0)
//    }
}
