package com.aidanvii.github.features.listrepos.presentation

import com.aidanvii.github.features.listrepos.GithubReposRepository
import com.aidanvii.github.features.listrepos.di.StubGithubReposRepositoryModule

class GithubReposListViewModelV3Test : GithubReposListViewModelTest() {

    val githubReposRepositoryModule = StubGithubReposRepositoryModule(
        totalRepos = totalRepos,
        pageSize = pageSize,
        prefetchDistance = prefetchDistance,
        initialPagesToLoad = initialPagesToLoad
    )

    override val githubReposRepository: GithubReposRepository
        get() = githubReposRepositoryModule.invoke()

    override fun finishOutstandingPageRequests() {
        githubReposRepositoryModule.triggerActions()
    }
}