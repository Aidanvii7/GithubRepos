package com.aidanvii.github.features.listrepos.entities

import com.squareup.moshi.Json

internal data class GithubReposPageImpl(

    @Json(name = "total_count")
    override val totalRepos: Int,

    @Json(name = "items")
    override val reposInPage: List<GithubRepoImpl>

) : GithubReposPage