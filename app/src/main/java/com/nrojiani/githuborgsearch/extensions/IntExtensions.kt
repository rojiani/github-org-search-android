@file:JvmName("IntExtensions")

/**
 * Extension functions for type Int.
 *
 * **Note**: If this were a "real" project, it would be a good idea to extract all
 * extension functions into a separate library.
 */
package com.nrojiani.githuborgsearch.extensions

import java.text.NumberFormat
import java.util.*

/**
 * Format an integer using the default [Locale] (unless explicitly provided).
 *
 * ```
 * 97732.formatted()                    // "97,732" (if default locale is Locale.US)
 * 97732.formatted(Locale.GERMANY)      // "97.732"
 * 97732.formatted(Locale.FRENCH)       // "97 732"
 * ```
 * @param locale The [Locale] to use, or [Locale.getDefault] if not specified.
 * @see NumberFormat.format
 */
fun Int.formatted(locale: Locale = Locale.getDefault()): String =
    NumberFormat.getNumberInstance(locale).format(this)
