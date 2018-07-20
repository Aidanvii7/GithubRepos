package com.aidanvii.github.features.listrepos.di

import com.aidanvii.github.features.listrepos.network.GithubReposApiService
import com.aidanvii.github.features.listrepos.GithubReposRepository
import com.aidanvii.github.features.listrepos.GithubReposRepositoryImpl
import com.aidanvii.toolbox.Provider
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class GithubReposRepositoryModule : Provider<GithubReposRepository> {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                })
            .build()
    }

    private val moshi: Moshi by lazy {
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit
            .Builder()
            .client(okHttpClient)
            .baseUrl("https://api.github.com/search/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    private val githubReposApiService: GithubReposApiService by lazy {
        retrofit.create(GithubReposApiService::class.java)
    }

    override fun invoke(): GithubReposRepository = GithubReposRepositoryImpl(
        githubReposApiService = githubReposApiService,
        pageSize = 30,
        prefetchDistance = 5,
        initialPagesToLoad = intArrayOf(1),
        maxRetries = 2,
        delayBetweenRetriesMillis = 5000
    )
}