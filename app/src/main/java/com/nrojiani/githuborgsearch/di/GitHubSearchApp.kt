package com.nrojiani.githuborgsearch.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin

/**
 * Android Application class
 */
class GitHubSearchApp : Application() {

    override fun onCreate() {
        super.onCreate()

        /* Start Koin DI container:
           https://start.insert-koin.io/#/getting-started/koin-for-android */
        startKoin {
            androidLogger()

            androidContext(this@GitHubSearchApp)

            // https://doc.insert-koin.io/#/koin-android/fragment-factory?id=fragment-factory
            // setup a KoinFragmentFactory instance
            fragmentFactory()

            modules(NetworkModule, AppModule)
        }
    }
}
