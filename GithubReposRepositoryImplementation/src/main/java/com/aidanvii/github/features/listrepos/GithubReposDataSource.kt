package com.aidanvii.github.features.listrepos

import android.support.annotation.IntRange
import com.aidanvii.github.features.listrepos.entities.GithubRepo
import com.aidanvii.github.features.listrepos.entities.GithubRepoImpl
import com.aidanvii.github.features.listrepos.entities.GithubRepoOwnerImpl
import com.aidanvii.github.features.listrepos.entities.GithubReposPageImpl
import com.aidanvii.github.features.listrepos.network.GithubReposApiService
import com.aidanvii.github.utils.logger.logD
import com.aidanvii.github.utils.logger.logE
import com.aidanvii.toolbox.paging.BaseDataSource
import com.aidanvii.toolbox.paging.PagedList
import io.reactivex.Maybe
import io.reactivex.Scheduler
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import kotlin.coroutines.experimental.coroutineContext
import kotlin.math.min

internal class GithubReposDataSource(
    private val githubReposApiService: GithubReposApiService,
    private val ioScheduler: Scheduler
) : BaseDataSource<GithubRepo>() {

    override fun loadPage(
        pageBuilder: PagedList.DataSource.Page.Builder<GithubRepo>
    ): Maybe<PagedList.DataSource.Page<GithubRepo>> =
        Maybe.create<PagedList.DataSource.Page<GithubRepo>> { emitter ->
            runBlocking {
                pageBuilder.run {
                    try {
                        fetchPageWith(
                            pageIndex = pageNumber,
                            pageSize = pageSize
                        ).let { fetchedPage ->
                            build(fetchedPage).also {
                                logD("loadPage: loaded: $fetchedPage")
                                emitter.onSuccess(it)
                            }
                        }
                    } catch (throwable: Throwable) {
                        logE("loadPage: error: $throwable")
                        emitter.onComplete()
                    }
                }
            }
        }.subscribeOn(ioScheduler)

    private suspend fun fetchPageWith(
        pageIndex: Int,
        pageSize: Int
    ): List<GithubRepoImpl> =
        getGithubReposPageWithRetry(
            pageIndex = pageIndex,
            pageSize = pageSize,
            remainingRetries = 2,
            delayBetweenRetriesMillis = 5000
        ).let { githubReposPage ->
            // TODO try to remove limit of 500
            publishDataCount(min(githubReposPage.totalRepos, 500))
            githubReposPage.reposInPage
        }

    private tailrec suspend fun getGithubReposPageWithRetry(
        pageIndex: Int,
        pageSize: Int,
        @IntRange(from = 0) remainingRetries: Int,
        delayBetweenRetriesMillis: Int
    ): GithubReposPageImpl {
        var error: Throwable? = null
        try {
            return runBlocking(coroutineContext) {
                githubReposApiService.reposPageFor(pageIndex, pageSize).await()
            }
        } catch (throwable: Throwable) {
            logE("getGithubReposPageWithRetry: error: $throwable")
            error = throwable
        }

        return if (error != null && remainingRetries == 0) {
            throw error
        } else {
            delay(delayBetweenRetriesMillis)
            getGithubReposPageWithRetry(
                pageIndex = pageIndex,
                pageSize = pageSize,
                remainingRetries = remainingRetries - 1,
                delayBetweenRetriesMillis = delayBetweenRetriesMillis
            )
        }
    }
}