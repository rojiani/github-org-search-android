@file:JvmName("TestUtils")

package com.nrojiani.githuborgsearch.testutils

import java.io.File

fun readJsonFile(file: File): String = buildString {
    file.useLines { lines ->
        lines.forEach { append("$it") }
    }
}
