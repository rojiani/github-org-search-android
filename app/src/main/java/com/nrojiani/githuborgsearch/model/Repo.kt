package com.nrojiani.githuborgsearch.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

/**
 * Models data about a GitHub repository.
 */
@Parcelize
data class Repo(
    val id: Long,
    val name: String,
    @Json(name = "html_url") val repoUrl: String,
    val description: String? = "",
    @Json(name = "stargazers_count") val stars: Int,
    @Json(name = "forks_count") val forks: Int,
    val language: String? = ""
) : Parcelable
