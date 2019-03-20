package com.nrojiani.githuborgsearch.web.chromecustomtabs

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

/**
 * Empty service used by the custom tab to bind to, raising the application's importance.
 * TODO: currently unused - see Google sample usage
 */
class KeepAliveService : Service() {

    override fun onBind(intent: Intent?): IBinder? =
        binder

    companion object {
        val binder: Binder = Binder()
    }

}