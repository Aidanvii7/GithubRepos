package com.aidanvii.github.features.listrepos

import com.aidanvii.github.features.listrepos.entities.GithubRepo
import com.aidanvii.github.features.listrepos.network.GithubReposApiService
import com.aidanvii.toolbox.paging.PagedList
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.schedulers.Schedulers
import org.amshove.kluent.mock
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.IOException

internal class GithubReposDataSourceTest {

    val mockApiService = mock<GithubReposApiService>()
    val pageSize = 10
    val maxRetries = 5

    val tested = GithubReposDataSource(
        githubReposApiService = mockApiService,
        ioScheduler = Schedulers.trampoline(),
        maxRetries = maxRetries,
        delayBetweenRetriesMillis = 0
    )

    val pagedList = PagedList(
        dataSource = tested,
        pageSize = pageSize
    )

    @Nested
    inner class `When api service will error once then succeed` {

        @BeforeEach
        fun before() {
            whenever(mockApiService.reposPageFor(any(), any())).then { throw IOException() }
        }

        @Nested
        inner class `When page load requested` {

            @BeforeEach
            fun before() {
                pagedList.loadAroundPage(1, growIfNecessary = true)
            }

            @Test
            fun `requests data from api service by given retry amount plus initial`() {
                verify(mockApiService, times(maxRetries + 1)).reposPageFor(1, pageSize)
            }
        }
    }
}