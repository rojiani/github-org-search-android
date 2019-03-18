package com.nrojiani.githuborgsearch.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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
            .addConverterFactory(MoshiConverterFactory.create(
                Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
            ))
            .build()

        @JvmStatic
        @Provides
        @Singleton
        fun provideGitHubService(retrofit: Retrofit): GitHubService =
            retrofit.create(GitHubService::class.java)


        /** TODO - combine?
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