package com.nrojiani.githuborgsearch.web.chromecustomtabs

import android.content.ComponentName
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import java.lang.ref.WeakReference

/**
 * Implementation for the [CustomTabsServiceConnection] that avoids leaking the
 * [ServiceConnectionCallback]
 *
 * [ServiceConnectionCallback]: Abstract [ServiceConnection] to use while binding to a [CustomTabsService].
 * Any client implementing this is responsible for handling changes related with the lifetime of
 * the connection like rebinding on disconnect.
 */
class ServiceConnection(connectionCallback: ServiceConnectionCallback) :
    CustomTabsServiceConnection() {

    // A weak reference to the ServiceConnectionCallback to avoid leaking it.
    private val connectionCallback: WeakReference<ServiceConnectionCallback> =
        WeakReference(connectionCallback)

    override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
        connectionCallback.get()?.onServiceConnected(client)
    }

    override fun onServiceDisconnected(name: ComponentName) {
        connectionCallback.get()?.onServiceDisconnected()
    }
}