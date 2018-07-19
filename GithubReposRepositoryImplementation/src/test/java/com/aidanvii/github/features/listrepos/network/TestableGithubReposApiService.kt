package com.aidanvii.github.features.listrepos.network

import com.aidanvii.github.features.listrepos.entities.GithubRepoImpl
import com.aidanvii.github.features.listrepos.entities.GithubRepoOwnerImpl
import com.aidanvii.github.features.listrepos.entities.GithubReposPageImpl
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.async

internal class TestableGithubReposApiService(
    val totalRepos: Int
) : GithubReposApiService {

    var errorCountdown = 0

    override fun reposPageFor(pageIndex: Int, pageSize: Int): Deferred<GithubReposPageImpl> {
        return async(Unconfined) {
            if (errorCountdown-- == 0) {
                val startIndex = startingIndexOfPage(pageIndex, pageSize)
                val endIndex = endIndexOfPageFromStartingIndex(startIndex, pageSize)
                GithubReposPageImpl(
                    totalRepos = totalRepos,
                    reposInPage = (startIndex..endIndex).map { index ->
                        GithubRepoImpl(
                            id = index,
                            name = "Toolbox",
                            stargazersCount = 5,
                            language = "Kotlin",
                            owner = GithubRepoOwnerImpl(
                                name = "Aidanvii7",
                                avatarUrl = "someUrl"
                            )
                        )
                    }
                )
            } else throw Throwable("error occurred")
        }
    }

    private fun startingIndexOfPage(pageIndex: Int, pageSize: Int): Int = (pageSize * (pageIndex - 1))

    private fun endIndexOfPageFromStartingIndex(pageStartingIndex: Int, pageSize: Int) = pageStartingIndex + pageSize - 1
}
