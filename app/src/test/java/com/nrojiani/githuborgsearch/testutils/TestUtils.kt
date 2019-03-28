@file:JvmName("TestUtils")

package com.nrojiani.githuborgsearch.testutils

import java.io.File


/** Convert a JSON file to a String. */
fun readJsonFile(file: File): String = buildString {
    file.useLines { lines ->
        lines.forEach { append("$it") }
    }
}
