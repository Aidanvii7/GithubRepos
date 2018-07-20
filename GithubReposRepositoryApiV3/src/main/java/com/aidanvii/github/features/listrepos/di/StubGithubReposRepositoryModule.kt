package com.aidanvii.github.features.listrepos.di

import com.aidanvii.github.features.listrepos.GithubReposRepository
import com.aidanvii.github.features.listrepos.GithubReposRepositoryImpl
import com.aidanvii.github.features.listrepos.network.StubGithubReposApiService
import com.aidanvii.toolbox.Provider
import io.reactivex.schedulers.TestScheduler
import kotlinx.coroutines.experimental.Unconfined

// TODO move to a GithubReposrepositoryV3-test module?
class StubGithubReposRepositoryModule(
    totalRepos: Int,
    val pageSize: Int,
    val prefetchDistance: Int,
    val initialPagesToLoad: IntArray
) : Provider<GithubReposRepository> {

    private val githubReposApiService by lazy {
        StubGithubReposApiService(
            totalRepos = totalRepos
        )
    }

    private val testIoScheduler = TestScheduler()

    fun triggerActions() = testIoScheduler.triggerActions()

    var errorCountdown: Int
        get() = githubReposApiService.errorCountdown
        set(value) {
            githubReposApiService.errorCountdown = value
        }

    override fun invoke(): GithubReposRepository = GithubReposRepositoryImpl(
        githubReposApiService = githubReposApiService,
        pageSize = pageSize,
        prefetchDistance = prefetchDistance,
        initialPagesToLoad = initialPagesToLoad,
        maxRetries = 0,
        delayBetweenRetriesMillis = 0,
        ioScheduler = testIoScheduler,
        launchContext = Unconfined
    )
}