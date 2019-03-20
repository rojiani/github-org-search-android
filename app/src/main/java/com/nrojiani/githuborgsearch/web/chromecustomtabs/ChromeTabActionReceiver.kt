package com.nrojiani.githuborgsearch.web.chromecustomtabs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast


/**
 * Adapted from [ActionBroadcastReceiver](http://tinyurl.com/yymvu3z8)
 * from the [Google Custom Chrome Tabs Sample Client](https://github.com/GoogleChrome/custom-tabs-client)
 */
class ChromeTabActionReceiver : BroadcastReceiver() {

    private val TAG by lazy { this::class.java.simpleName }

    companion object {
        const val KEY_ACTION_SOURCE = "org.chromium.customtabsdemos.ACTION_SOURCE"
        const val ACTION_ACTION_BUTTON = 3
    }

    // responds to CCT menu selections
    override fun onReceive(context: Context, intent: Intent) {
        // TODO ------- debugging only
        val broadcastInfo = buildString {
            append("Action: ${intent.action}\n")
            append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
            toString().also { log ->
                Log.d(TAG, log)
                Toast.makeText(context, log, Toast.LENGTH_LONG).show()
            }
        }
        Log.d(TAG, "broadcast info: $broadcastInfo")
        // TODO ------- debugging only

//        val url = intent.dataString
//        url?.let {
//            val toastText = getToastText(context, intent.getIntExtra(KEY_ACTION_SOURCE, -1))
//            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
//        }
    }

//    private fun getToastText(context: Context, actionSource: Int): String {
//        val stringResId = when (actionSource) {
//            ACTION_MENU_ITEM_1 -> R.string.toast_menu_1
//            ACTION_MENU_ITEM_2 -> R.string.toast_menu_2
//            ACTION_ACTION_BUTTON -> R.string.text_action_button
//            else -> R.string.unknown_action
//        }
//        return context.getString(stringResId)
//    }
}
