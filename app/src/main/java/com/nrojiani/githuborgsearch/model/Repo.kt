package com.nrojiani.githuborgsearch.model

import com.squareup.moshi.Json

/**
 * Models data about a GitHub repository.
 */
data class Repo(
    val id: Long,
    val name: String,
    @Json(name = "full_name") val fullName: String,
    val description: String? = "",
    @Json(name = "stargazers_count") val stars: Long,
    @Json(name = "forks_count") val forks: Long
)
