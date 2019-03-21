package com.nrojiani.githuborgsearch.web.chromecustomtabs

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.util.Log


/**
 * Helper class for Custom Tabs.
 * Adapted from [GitHub: GoogleChrome/custom-tabs-client - CustomTabsHelper.java](https://github.com/GoogleChrome/custom-tabs-client/blob/master/shared/src/main/java/org/chromium/customtabsclient/shared/CustomTabsHelper.java)
 */
object CustomTabsHelper {
    private const val TAG = "CustomTabsHelper"

    private enum class ChromePackage(val rdns: String) {
        STABLE("com.android.chrome"),
        BETA("com.chrome.beta"),
        DEV("com.chrome.dev"),
        CANARY("com.chrome.canary"),
        LOCAL("com.google.android.apps.chrome")
    }

    //  TODO remove if not needed
    private const val EXTRA_CUSTOM_TABS_KEEP_ALIVE = "android.support.customtabs.extra.KEEP_ALIVE"
    private const val ACTION_CUSTOM_TABS_CONNECTION =
        "android.support.customtabs.action.CustomTabsService"

    private var packageNameToUse: String? = null

//  TODO remove if not needed
//    /**
//     * @return All possible chrome package names that provide custom tabs feature.
//     */
//    val packages: Array<String>
//        get() = arrayOf("", ChromePackage.STABLE.rdns, ChromePackage.BETA.rdns, ChromePackage.DEV.rdns, ChromePackage.LOCAL.rdns)
//        // TODO: why ""?
//
//
//    fun addKeepAliveExtra(context: Context, intent: Intent) {
//        val keepAliveIntent = Intent().setClassName(
//            context.packageName,
//            KeepAliveService::class.java.canonicalName
//        )
//
//        // the param intent, not keepAliveIntent
//        intent.putExtra(EXTRA_CUSTOM_TABS_KEEP_ALIVE, keepAliveIntent)
//    }

    /**
     * Goes through all apps that handle VIEW intents and have a warmup service. Picks
     * the one chosen by the user if there is one, otherwise makes a best effort to return a
     * valid package name.
     *
     * This is **not** threadsafe.
     *
     * @param context [Context] to use for accessing [PackageManager].
     * @return The package name recommended to use for connecting to custom tabs related components.
     */
    fun getPackageNameToUse(context: Context): String? {
        // DEBUG force use webview
        // if (!MainActivity.HAS_CHROMIUM) return null


        if (packageNameToUse != null) return packageNameToUse

        val pm = context.packageManager
        // Get default VIEW intent handler.
        val activityIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"))
        val defaultViewHandlerInfo = pm.resolveActivity(activityIntent, 0)
        var defaultViewHandlerPackageName: String? = null
        if (defaultViewHandlerInfo != null) {
            defaultViewHandlerPackageName = defaultViewHandlerInfo.activityInfo.packageName
        }

        // Get all apps that can handle VIEW intents.
        val resolvedActivityList: List<ResolveInfo> = pm.queryIntentActivities(activityIntent, 0)
        val packagesSupportingCustomTabs = ArrayList<String>()
        resolvedActivityList.forEach { info ->
            val serviceIntent = Intent()
            serviceIntent.action =
                ACTION_CUSTOM_TABS_CONNECTION
            serviceIntent.setPackage(info.activityInfo.packageName)
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info.activityInfo.packageName)
            }
        }

        packageNameToUse = when {
            packagesSupportingCustomTabs.isEmpty() -> null
            packagesSupportingCustomTabs.size == 1 -> packagesSupportingCustomTabs[0]
            !defaultViewHandlerPackageName.isNullOrBlank()
                    && !hasSpecializedHandlerIntents(
                context,
                activityIntent
            )
                    && packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName)
                            -> defaultViewHandlerPackageName
            packagesSupportingCustomTabs.contains(CustomTabsHelper.ChromePackage.STABLE.rdns) -> CustomTabsHelper.ChromePackage.STABLE.rdns
            packagesSupportingCustomTabs.contains(CustomTabsHelper.ChromePackage.BETA.rdns) -> CustomTabsHelper.ChromePackage.BETA.rdns
            packagesSupportingCustomTabs.contains(CustomTabsHelper.ChromePackage.DEV.rdns) -> CustomTabsHelper.ChromePackage.DEV.rdns
            packagesSupportingCustomTabs.contains(CustomTabsHelper.ChromePackage.LOCAL.rdns) -> CustomTabsHelper.ChromePackage.LOCAL.rdns
            else -> packageNameToUse
        }

        return packageNameToUse
    }

    /**
     * Used to check whether there is a specialized handler for a given intent.
     * @param intent The intent to check with.
     * @return Whether there is a specialized handler for the given intent.
     */
    private fun hasSpecializedHandlerIntents(context: Context, intent: Intent): Boolean {
        try {
            val pm = context.packageManager
            val handlers = pm.queryIntentActivities(
                intent,
                PackageManager.GET_RESOLVED_FILTER
            )
            if (handlers == null || handlers.size == 0) {
                return false
            }
            for (resolveInfo in handlers) {
                val filter = resolveInfo.filter ?: continue
                if (filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0) continue
                if (resolveInfo.activityInfo == null) continue
                return true
            }
        } catch (e: RuntimeException) {
            Log.e(TAG, "Runtime exception while getting specialized handlers")
        }

        return false
    }
}
