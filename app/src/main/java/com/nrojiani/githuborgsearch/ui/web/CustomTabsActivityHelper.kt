package com.nrojiani.githuborgsearch.ui.web

import android.app.Activity
import android.net.Uri
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import com.nrojiani.githuborgsearch.web.chromecustomtabs.CustomTabsHelper
import com.nrojiani.githuborgsearch.web.chromecustomtabs.ServiceConnection
import com.nrojiani.githuborgsearch.web.chromecustomtabs.ServiceConnectionCallback


/**
 * TODO - http://tinyurl.com/y5mkp99n - Lifecycle Aware
 *
 * Helper class to manage connection to the custom tab activity
 *
 * Adapted from the CustomTabActivityHelper in the sample code here:
 * https://github.com/GoogleChrome/custom-tabs-client/blob/master/demos/src/main/java/org/chromium/customtabsdemos/CustomTabActivityHelper.java
 */
class CustomTabActivityHelper : ServiceConnectionCallback {

    /**
     * [CustomTabsSession]:
     * A class to be used for Custom Tabs related communication.
     * Clients that want to launch Custom Tabs can use this class exclusively to handle all related
     * communication.
     */
    private var customTabsSession: CustomTabsSession? = null

    /**
     * [CustomTabsClient]:
     * Class to communicate with a [CustomTabsService] and create [CustomTabsSession] from it.
     */
    private var customTabsClient: CustomTabsClient? = null

    /**
     * [CustomTabsServiceConnection]:
     * Abstract ServiceConnection to use while binding to a CustomTabsService. Any client
     * implementing this is responsible for handling changes related with the lifetime of the
     * connection like rebinding on disconnect.
     */
    private var serviceConnection: CustomTabsServiceConnection? = null

    /** see [ConnectionCallback] */
    private var connectionCallback: ConnectionCallback? = null

//    /**
//     * Creates or retrieves an exiting CustomTabsSession.
//     *
//     * @return a CustomTabsSession.
//     */
//    private val session: CustomTabsSession?
//        get() {
//            customTabsSession = customTabsClient?.newSession(null)
//            return customTabsSession
//        }

    override fun onServiceConnected(client: CustomTabsClient) {
        customTabsClient = client
        customTabsClient?.warmup(0L)
        connectionCallback?.onCustomTabsConnected()
    }

    override fun onServiceDisconnected() {
        customTabsClient = null
        customTabsSession = null
        connectionCallback?.onCustomTabsDisconnected()
    }

    /**
     * Binds the activity to the custom tabs service.
     * @param activity the activity to be bound to the service.
     */
    fun bindCustomTabsService(activity: Activity) {
        if (customTabsClient != null) return

        val packageName = CustomTabsHelper.getPackageNameToUse(activity) ?: return

        serviceConnection = ServiceConnection(this)
        CustomTabsClient.bindCustomTabsService(activity, packageName, serviceConnection)
    }

//    /**
//     * TODO
//     * Unbinds the Activity from the Custom Tabs Service.
//     * @param activity the activity that is connected to the service.
//     */
//    fun unbindCustomTabsService(activity: Activity) {
//        serviceConnection ?: return
//
//        activity.unbindService(serviceConnection)
//        customTabsClient = null
//        customTabsSession = null
//        serviceConnection = null
//    }

//  TODO - Remove if not necessary
//    /**
//     * Register a Callback to be called when connected or disconnected from the Custom Tabs Service.
//     * @param connectionCallback
//     */
//    fun setConnectionCallback(connectionCallback: ServiceConnectionCallback) {
//        this.connectionCallback = connectionCallback
//    }
//    /**
//     * https://github.com/GoogleChrome/custom-tabs-client/blob/master/demos/src/main/java/org/chromium/customtabsdemos/ServiceConnectionActivity.java
//     * https://chromium.googlesource.com/custom-tabs-client/+/master/Using.md - Optimization
//     * Hint about a likely future navigation: Indicates that a given URL may be loaded in the
//     * future. Chrome may perform speculative work to speed up page load time. The application
//     * must call CustomTabsClient.warmup() first. This is triggered by CustomTabsSession.mayLaunchUrl().
//     * @see [CustomTabsSession.mayLaunchUrl]
//     * @return true if call to mayLaunchUrl was accepted.
//     */
//    fun mayLaunchUrl(uri: Uri, extras: Bundle, otherLikelyBundles: List<Bundle>): Boolean {
//        customTabsClient ?: return false
//
//        val session = session ?: return false
//
//        return session.mayLaunchUrl(uri, extras, otherLikelyBundles)
//    }

    companion object {
        /**
         * http://tinyurl.com/y2css2zn
         * http://tinyurl.com/y5mkp99n
         *
         * Utility method for opening a custom tab
         *
         * @param activity The host [Activity]
         * @param customTabsIntent [CustomTabsIntent]
         * @param uri uri to open
         * @param fallback fallback to handle case where custom tab cannot be opened
         */
        fun openCustomTab(
            activity: Activity,
            customTabsIntent: CustomTabsIntent,
            uri: Uri,
            fallback: CustomTabFallback?    // TODO convert to lambda
        ) {
            val packageName = CustomTabsHelper.getPackageNameToUse(activity)

            if (packageName == null) {
                // no package name, means there's no chromium browser.
                // Trigger fallback (WebView)
                fallback?.openUri(activity, uri)
            } else {
                // set package name to use
                customTabsIntent.intent.setPackage(packageName)
                customTabsIntent.launchUrl(activity, uri)
            }
        }
    }


    /**
     * A Callback for when the service is connected or disconnected. Use those callbacks to
     * handle UI changes when the service is connected or disconnected.
     */
    interface ConnectionCallback {
        /**
         * Called when the service is connected.
         */
        fun onCustomTabsConnected()

        /**
         * Called when the service is disconnected.
         */
        fun onCustomTabsDisconnected()
    }

    /**
     * To be used as a fallback to open the Uri when Custom Tabs is not available.
     */
    interface CustomTabFallback {
        /**
         * @param activity The Activity that wants to open the Uri.
         * @param uri The uri to be opened by the fallback.
         */
        fun openUri(activity: Activity, uri: Uri)
    }

}