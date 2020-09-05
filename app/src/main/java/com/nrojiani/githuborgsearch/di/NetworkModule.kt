@file:JvmName("NetworkModule")

package com.nrojiani.githuborgsearch.di

import com.nrojiani.githuborgsearch.network.GitHubService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.picasso.Picasso
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val GITHUB_API_BASE_URL = "https://api.github.com/"

/**
 * Module which provides all network-related dependencies.
 */
val NetworkModule = module {

    /**
     * Retrofit
     * - Dependencies: none
     */
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(GITHUB_API_BASE_URL)
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                )
            ).build()
    }

    /**
     * GitHubService
     * - Dependencies: Retrofit
     */
    single<GitHubService> {
        val retrofit: Retrofit = get()
        retrofit.create(GitHubService::class.java)
    }

    /**
     * Picasso
     * - Dependencies: Context (via Koin appModule)
     */
    single<Picasso> {
        // Inject Android Context
        Picasso.Builder(androidContext()).build()
    }
}
