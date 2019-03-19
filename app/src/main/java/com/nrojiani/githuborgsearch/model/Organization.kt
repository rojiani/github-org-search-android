package com.nrojiani.githuborgsearch.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Organization(
    val name: String = "(name missing)",
    val login: String,
    @Json(name = "avatar_url") val avatarUrl: String,
    @Json(name = "html_url") val githubUrl: String,
    @Json(name = "blog") val blogUrl: String? = "",
    val location: String? = "",
    val description: String? = ""
) : Parcelable

//    val id: Int,
//    @Json(name = "blog") val websiteUrl: String,
//    val location: String,
//    val email: String,
//    val description: String,
//    @Json(name = "public_repos") val repoCount: Long

//    val company: Any,
//    val created_at: String,
//    val events_url: String,
//    val followers: Int,
//    val following: Int,
//    val has_organization_projects: Boolean,
//    val has_repository_projects: Boolean,
//    val hooks_url: String,
//    val is_verified: Boolean,
//    val issues_url: String,
//    val login: String,
//    val members_url: String,
//    val node_id: String,
//    val public_gists: Int,
//    val public_members_url: String,
//    val public_repos: Int,
//    val repos_url: String,
//    val type: String,
//    val updated_at: String
