package com.aidanvii.github.features.listrepos.presentation

import com.aidanvii.github.features.listrepos.GithubReposRepository
import kotlinx.coroutines.experimental.Unconfined
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be false`
import org.amshove.kluent.`should be null`
import org.amshove.kluent.`should be true`
import org.amshove.kluent.`should not be null`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

abstract class GithubReposListViewModelTest {

    val totalRepos: Int = 120
    val pageSize = 30
    val prefetchDistance = 5
    val initialPagesToLoad = intArrayOf(1)

    abstract val githubReposRepository: GithubReposRepository
    abstract fun finishOutstandingPageRequests()

    @Nested
    internal inner class `When initialised` {

        val tested = GithubReposListViewModel(
            githubReposRepository = githubReposRepository,
            uiContext = Unconfined,
            workerContext = Unconfined
        )

        @Test
        fun `githubRepoAdapterItems is empty`() {
            tested.githubRepoAdapterItems.size `should be equal to` 0
        }

        @Test
        fun `showLoader is true`() {
            tested.showLoader.`should be true`()
        }

        @Nested
        internal inner class `When initial page loads` {

            @BeforeEach
            fun before() {
                finishOutstandingPageRequests()
            }

            @Test
            fun `githubRepoAdapterItems size matches the total repos available`() {
                tested.githubRepoAdapterItems.size `should be equal to` totalRepos
            }

            @Test
            fun `githubRepoAdapterItems has first page loaded`() {
                for (loadedIndex in 0 until pageSize) {
                    tested.githubRepoAdapterItems[loadedIndex].githubRepo.`should not be null`()
                    tested.githubRepoAdapterItems[loadedIndex].githubRepo!!.id `should be equal to` loadedIndex
                }
            }

            @Test
            fun `githubRepoAdapterItems has placeholders for not loaded pages`() {
                for (loadedIndex in pageSize until totalRepos) {
                    tested.githubRepoAdapterItems[loadedIndex].githubRepo.`should be null`()
                }
            }

            @Test
            fun `showLoader is false`() {
                tested.showLoader.`should be false`()
            }
        }
    }
}