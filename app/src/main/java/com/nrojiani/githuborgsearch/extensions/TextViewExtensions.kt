@file:JvmName("TextViewExtensions")


package com.nrojiani.githuborgsearch.extensions

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible


/**
 * Extension functions for TextView.
 *
 * **Note**: If this were a "real" project, it would be a good idea to extract all
 * extension functions into a separate library.
 */

/**
 * If the [text] is valid, make the TextView visible and set its text.
 * Otherwise, set visibility to [View.GONE]
 */
fun TextView.displayTextOrHide(text: String?) {
    if (text.isNullOrBlank()) {
        isVisible = false
    } else {
        this.text = text
        isVisible = true
    }
}