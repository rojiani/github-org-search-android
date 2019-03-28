package com.nrojiani.githuborgsearch.util

import androidx.test.espresso.IdlingResource

/**
 * Source: [GitHub: googlesamples/android-architecture - todo-mvvm-live-kotlin](https://github.com/googlesamples/android-architecture/tree/todo-mvvm-live-kotlin)
 * Contains a static reference to [IdlingResource], only available in the 'mock' build type.
 */
object EspressoIdlingResource {

    private val RESOURCE = "GLOBAL"

    @JvmField val countingIdlingResource = SimpleCountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        countingIdlingResource.decrement()
    }
}
