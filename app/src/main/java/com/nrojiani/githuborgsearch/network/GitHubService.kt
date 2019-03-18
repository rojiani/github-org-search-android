package com.nrojiani.githuborgsearch.network

import com.nrojiani.githuborgsearch.model.Organization
import com.nrojiani.githuborgsearch.model.Repo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface GitHubService {

    /**
     * Search for an organization.
     */
    @Headers("Accept: application/vnd.github.v3+json", "Content-Type: application/json")
    @GET("orgs/{org}")
    fun getOrg(@Path("org") org: String): Call<Organization>

    /**
     * Fetch the list of repositories associated with an organization.
     */
    @Headers("Accept: application/vnd.github.v3+json", "Content-Type: application/json")
    @GET("orgs/{org}/repos")
    fun getRepositoriesForOrg(@Path("org") org: String): Call<List<Repo>>

}