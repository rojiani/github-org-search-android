package com.nrojiani.githuborgsearch.controllers.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.nrojiani.githuborgsearch.R
import com.saurabharora.customtabs.CustomTabFallback
import com.saurabharora.customtabs.extensions.launchWithFallback

/**
 * Handles opening a URL. The delegate attempts to open the
 * URL in Chrome Custom Tabs, falling back to WebView if no Chromium-based
 * browsers are installed on the device.
 */
class WebContentDelegate(private val delegatingActivity: Activity) {

    fun openWebContent(url: String) {
        openChromeCustomTab(Uri.parse(url))
    }

    private fun openChromeCustomTab(uri: Uri) {
        Log.d(TAG, "openChromeCustomTab($uri)")
        val customTabsIntent: CustomTabsIntent = buildCustomTabsIntent()

        customTabsIntent.launchWithFallback(
            activity = delegatingActivity,
            uri = uri,
            fallback = object : CustomTabFallback {
                override fun openUri(activity: Activity, uri: Uri) {
                    openWebView(uri)
                }
            }
        )
    }

    private fun openWebView(uri: Uri) {
        Log.d(TAG, "openWebView($uri)")
        val webViewIntent = Intent(delegatingActivity, WebViewActivity::class.java)
        webViewIntent.putExtra(WebViewActivity.EXTRA_URL, uri.toString())
        delegatingActivity.startActivity(webViewIntent)
    }

    private fun buildCustomTabsIntent(): CustomTabsIntent {
        val intentBuilder = CustomTabsIntent.Builder().apply {
            // set Toolbar colors
            setToolbarColor(ContextCompat.getColor(delegatingActivity, R.color.colorPrimary))
            setSecondaryToolbarColor(
                ContextCompat.getColor(
                    delegatingActivity,
                    R.color.colorPrimaryDark
                )
            )

            // show the page title (as well as the URL)
            setShowTitle(true)

            // Animations
            setStartAnimations(
                delegatingActivity,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            setExitAnimations(
                delegatingActivity,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
        }

        return intentBuilder.build()
    }

    companion object {
        private const val TAG = "WebContentDelegate"
    }
}
