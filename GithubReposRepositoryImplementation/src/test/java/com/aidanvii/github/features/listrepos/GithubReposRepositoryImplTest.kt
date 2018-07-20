package com.aidanvii.github.features.listrepos

import com.aidanvii.github.features.listrepos.entities.GithubRepo
import com.aidanvii.github.features.listrepos.network.TestableGithubReposApiService
import com.aidanvii.github.testutils.spied
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import io.reactivex.schedulers.TestScheduler
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.async
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be false`
import org.amshove.kluent.`should be null`
import org.amshove.kluent.`should be true`
import org.amshove.kluent.`should not be null`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

internal class GithubReposRepositoryImplTest {

    val maxRepos = 20
    val pageSize = 10
    val prefetchDistance = 3
    val initialPagesToLoad = intArrayOf(1)

    val spiedApiService = TestableGithubReposApiService(maxRepos).spied()
    val testScheduler = TestScheduler()

    var githubRepos = emptyList<GithubRepo?>()
    var isRefreshing = false

    @Nested
    inner class `When initialized` {

        val tested = GithubReposRepositoryImpl(
            githubReposApiService = spiedApiService,
            pageSize = pageSize,
            prefetchDistance = prefetchDistance,
            initialPagesToLoad = initialPagesToLoad,
            maxRetries = 0,
            delayBetweenRetriesMillis = 0,
            ioScheduler = testScheduler,
            launchContext = Unconfined
        )
        private val githubReposJob = async(Unconfined) {
            for (githubRepos in tested.githubRepos) {
                this@GithubReposRepositoryImplTest.githubRepos = githubRepos
            }
        }

        private val isRefreschingJob = async(Unconfined) {
            for (isRefreshing in tested.isRefreshing) {
                this@GithubReposRepositoryImplTest.isRefreshing = isRefreshing
            }
        }

        @Test
        fun `publishes isRefreshing true`() {
            isRefreshing.`should be true`()
        }

        @Test
        fun `doesn't publish populated GithubRepos list`() {
            githubRepos.size `should be equal to` 0
        }

        @Nested
        inner class `When api service responds with result` {

            @BeforeEach
            fun before() {
                testScheduler.triggerActions()
            }

            @Test
            fun `publishes isRefreshing false`() {
                isRefreshing.`should be false`()
            }

            @Test
            fun `publishes GithubRepos list with correct size`() {
                githubRepos.size `should be equal to` maxRepos
            }

            @Test
            fun `only first page is populated`() {
                for (index in 0 until pageSize) {
                    githubRepos[index].let { githubRepo ->
                        githubRepo.`should not be null`()
                        githubRepo!!.id `should be equal to` index
                    }
                }
                for (index in pageSize until maxRepos) {
                    githubRepos[index].`should be null`()
                }
            }

            @Nested
            inner class `When loadAround called with index 0` {

                @BeforeEach
                fun before() {
                    tested.loadAround(0)
                    testScheduler.triggerActions()
                }

                @Test
                fun `isRefreshing is still false`() {
                    isRefreshing.`should be false`()
                }

                @Nested
                inner class `When loadAround called with index within already loaded page and below prefetch distance` {

                    fun before(indexWithinCurrentPage: Int) {
                        reset(spiedApiService)
                        tested.loadAround(indexWithinCurrentPage)
                        testScheduler.triggerActions()
                    }

                    @ParameterizedTest
                    @ValueSource(ints = [1, 2, 3, 4, 5, 6])
                    fun `isRefreshing is still false`(indexWithinCurrentPage: Int) {
                        before(indexWithinCurrentPage)
                        isRefreshing.`should be false`()
                    }

                    @ParameterizedTest
                    @ValueSource(ints = [1, 2, 3, 4, 5, 6])
                    fun `doesn't query api service`(indexWithinCurrentPage: Int) {
                        before(indexWithinCurrentPage)
                        verifyZeroInteractions(spiedApiService)
                    }
                }

                @Nested
                inner class `When loadAround called with index within already loaded page and above prefetch distance` {

                    fun before(indexWithinCurrentPage: Int) {
                        reset(spiedApiService)
                        tested.loadAround(indexWithinCurrentPage)
                        testScheduler.triggerActions()
                    }

                    @ParameterizedTest
                    @ValueSource(ints = [7, 8, 9])
                    fun `isRefreshing is still false`(indexWithinCurrentPage: Int) {
                        before(indexWithinCurrentPage)
                        isRefreshing.`should be false`()
                    }

                    @ParameterizedTest
                    @ValueSource(ints = [7, 8, 9])
                    fun `queries api service`(indexWithinCurrentPage: Int) {
                        before(indexWithinCurrentPage)
                        verify(spiedApiService).reposPageFor(2, pageSize)
                    }
                }
            }
        }
    }
}