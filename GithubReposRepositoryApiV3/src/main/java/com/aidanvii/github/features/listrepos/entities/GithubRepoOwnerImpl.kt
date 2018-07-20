package com.aidanvii.github.features.listrepos.entities

import com.squareup.moshi.Json

internal data class GithubRepoOwnerImpl(

    @Json(name = "login")
    override val name: String,

    @Json(name = "avatar_url")
    override val avatarUrl: String

) : GithubRepoOwner