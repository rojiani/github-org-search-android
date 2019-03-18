package com.nrojiani.githuborgsearch.model

import com.squareup.moshi.Json

/**
 * Models data about a GitHub repository.
 */
data class Repo(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String,
    val owner: Owner,
    @Json(name = "stargazers_count") val stars: Long,
    @Json(name = "forks_count") val forks: Long
)
