package com.nrojiani.githuborgsearch.controllers.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.nrojiani.githuborgsearch.R


/**
 * This Activity is used as a fallback when there is no browser installed that supports
 * Chrome Custom Tabs.
 */
class WebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressLoading: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val url = intent.getStringExtra(EXTRA_URL)

        webView = findViewById(R.id.webview_content)
        progressLoading = findViewById(R.id.progress_loading)

        configureWebView()

        webView.loadUrl(url)

        title = url
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onBackPressed() =
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configureWebView() {
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                when (newProgress) {
                    100 -> progressLoading.isVisible = false
                    else -> {
                        if (progressLoading.isGone) {
                            progressLoading.isVisible = true
                        }
                        progressLoading.progress = newProgress
                    }
                }
                progressLoading.progress = newProgress
                super.onProgressChanged(view, newProgress)
            }
        }
    }

    /** Private webview client to handle visibility of progress */
    private inner class WebViewClient : android.webkit.WebViewClient() {
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            progressLoading.isVisible = true
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView, url: String) {
            progressLoading.isVisible = false
            super.onPageFinished(view, url)
        }
    }

    companion object {
        const val EXTRA_URL = "extra.url"
    }
}
