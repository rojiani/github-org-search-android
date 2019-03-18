package com.nrojiani.githuborgsearch.model

import com.squareup.moshi.Json

// TODO remove if unnecessary
data class Owner(
    val login: String,
    @Json(name = "avatar_url") val avatarUrl: String,
    @Json(name = "html_url") val url: String
)

/*
"avatar_url": "https://avatars0.githubusercontent.com/u/221409?v=4",
"html_url": "https://github.com/nytimes",
 */