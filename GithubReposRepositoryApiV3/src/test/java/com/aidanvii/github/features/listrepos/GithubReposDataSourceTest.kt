package com.aidanvii.github.features.listrepos

import com.aidanvii.github.features.listrepos.entities.GithubRepo
import com.aidanvii.github.features.listrepos.network.StubGithubReposApiService
import com.aidanvii.github.testutils.spied
import com.aidanvii.toolbox.paging.PagedList
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should equal`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class GithubReposDataSourceTest {

    val maxRepos = 30
    val pageSize = 10
    val maxRetries = 5
    val spiedApiService = StubGithubReposApiService(maxRepos).spied()

    val tested = GithubReposDataSource(
        githubReposApiService = spiedApiService,
        ioScheduler = Schedulers.trampoline(),
        maxRetries = maxRetries,
        delayBetweenRetriesMillis = 0
    )

    var latest = emptyList<GithubRepo?>()

    val pagedList = PagedList(
        dataSource = tested,
        pageSize = pageSize
    ).apply {
        observableList.subscribeBy { latest = it }
    }

    @Nested
    inner class `When api service errors` {

        @BeforeEach
        fun before() {
            spiedApiService.errorCountdown = 10
        }

        @Nested
        inner class `When page load requested` {

            @BeforeEach
            fun before() {
                pagedList.loadAroundPage(1, growIfNecessary = true)
            }

            @Test
            fun `requests data from api service by given retry amount plus initial`() {
                verify(spiedApiService, times(maxRetries + 1)).reposPageFor(1, pageSize)
            }

            @Test
            fun `pageList grows only to match the pageSize`() {
                pagedList.size `should be equal to` pageSize
            }

            @Test
            fun `page is not populated`() {
                for (index in 0 until pageSize) {
                    pagedList.elementStateFor(index) `should equal` PagedList.ElementState.EMPTY
                }
            }
        }
    }

    @Nested
    inner class `When api service succeeds` {

        @BeforeEach
        fun before() {
            spiedApiService.errorCountdown = 0
        }

        @Nested
        inner class `When page load requested` {

            @BeforeEach
            fun before() {
                pagedList.loadAroundPage(1, growIfNecessary = true)
            }

            @Test
            fun `requests data from api service once`() {
                verify(spiedApiService).reposPageFor(1, pageSize)
            }

            @Test
            fun `pageList grows to match totalRepos count in response`() {
                pagedList.size `should be equal to` maxRepos
            }

            @Test
            fun `only requested page is populated`() {
                for (index in 0..9) {
                    pagedList.elementStateFor(index) `should equal` PagedList.ElementState.LOADED
                }
                for (index in 10 until maxRepos) {
                    pagedList.elementStateFor(index) `should equal` PagedList.ElementState.EMPTY
                }
            }
        }
    }
}