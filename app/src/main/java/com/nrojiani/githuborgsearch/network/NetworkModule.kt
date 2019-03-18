package com.nrojiani.githuborgsearch.network

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * Module which provides all network-related dependencies.
 */
@Module
abstract class NetworkModule {

    @Module
    companion object {
        private const val BASE_URL = "https://api.github.com/"

        @JvmStatic
        @Provides
        @Singleton
        fun provideRetrofit(): Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        @JvmStatic
        @Provides
        @Singleton
        fun provideGitHubService(retrofit: Retrofit): GitHubService =
            retrofit.create(GitHubService::class.java)


        /** TODO - combine? Where is provideRetrofit even used?
         *
         *
         * ```kotlin
         * @Provides
         * @Singleton
         * fun provideGitHubService(): GitHubService {
         *     Log.d(TAG, "createService: creating GitHubService")
         *     return Retrofit.Builder()
         *          .baseUrl(BASE_URL)
         *          .addConverterFactory(MoshiConverterFactory.create())
         *          .build()
         *          .create(GitHubService::class.java)
         * ```
         */
    }
}