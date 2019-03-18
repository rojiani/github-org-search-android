package com.nrojiani.githuborgsearch.network

import com.nrojiani.githuborgsearch.model.Repo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface GitHubService {

    @Headers("Accept: application/vnd.github.v3+json", "Content-Type: application/json")
    @GET("orgs/{org}/repos")
    fun getRepositoriesForOrg(@Path("org") org: String): Call<List<Repo>>

    @Headers("Accept: application/vnd.github.v3+json", "Content-Type: application/json")
    @GET("repos/{owner}/{name}")
    fun getRepo(
        @Path("owner") repoOwner: String,
        @Path("name") repoName: String
    ): Call<Repo>

}