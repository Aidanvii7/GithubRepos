package com.aidanvii.github.features.listrepos.entities

import com.squareup.moshi.Json

internal data class GithubRepoImpl(

    @Json(name = "id")
    override val id: Int,

    @Json(name = "name")
    override val name: String,

    @Json(name = "stargazers_count")
    override val stargazersCount: Int,

    @Json(name = "language")
    override val language: String?,

    @Json(name = "owner")
    override val owner: GithubRepoOwnerImpl

) : GithubRepo
