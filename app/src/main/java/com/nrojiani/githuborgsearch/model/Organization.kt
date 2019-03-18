package com.nrojiani.githuborgsearch.model

import com.squareup.moshi.Json

data class Organization(
    val id: Int,
    val name: String,
    val location: String,
    val email: String,
    @Json(name = "avatar_url") val avatarUrl: String,
    @Json(name = "html_url") val githubUrl: String,
    @Json(name = "blog") val websiteUrl: String,
    val description: String,
    @Json(name = "public_repos") val repoCount: Long

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
)

/* TODO
{
  "login": "nytimes",
  "id": 221409,
  "node_id": "MDEyOk9yZ2FuaXphdGlvbjIyMTQwOQ==",
  "url": "https://api.github.com/orgs/nytimes",
  "repos_url": "https://api.github.com/orgs/nytimes/repos",
  "events_url": "https://api.github.com/orgs/nytimes/events",
  "hooks_url": "https://api.github.com/orgs/nytimes/hooks",
  "issues_url": "https://api.github.com/orgs/nytimes/issues",
  "members_url": "https://api.github.com/orgs/nytimes/members{/member}",
  "public_members_url": "https://api.github.com/orgs/nytimes/public_members{/member}",
  "avatar_url": "https://avatars0.githubusercontent.com/u/221409?v=4",
  "description": "",
  "name": "The New York Times",
  "company": null,
  "blog": "nytimes.com",
  "location": "New York, NY",
  "email": "",
  "is_verified": false,
  "has_organization_projects": true,
  "has_repository_projects": true,
  "public_repos": 67,
  "public_gists": 1,
  "followers": 0,
  "following": 0,
  "html_url": "https://github.com/nytimes",
  "created_at": "2010-03-12T16:14:34Z",
  "updated_at": "2019-03-04T16:35:25Z",
  "type": "Organization"
}
 */