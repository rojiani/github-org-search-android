package com.nrojiani.githuborgsearch.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Organization(
    val name: String = "(name missing)",
    val login: String,
    @Json(name = "avatar_url") val avatarUrl: String,
    @Json(name = "blog") val blogUrl: String? = "",
    val location: String? = "",
    val description: String? = ""
) : Parcelable
