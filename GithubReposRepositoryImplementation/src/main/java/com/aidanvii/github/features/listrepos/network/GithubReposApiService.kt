package com.aidanvii.github.features.listrepos.network

import com.aidanvii.github.features.listrepos.entities.GithubReposPageImpl
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

internal interface GithubReposApiService {

    @GET("repositories?q=android&sort=stars&order=desc")
    fun reposPageFor(
        @Query("page") pageIndex: Int,
        @Query("per_page") pageSize: Int
    ): Deferred<GithubReposPageImpl>

}