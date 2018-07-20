package com.aidanvii.github.features.listrepos

import com.aidanvii.github.features.listrepos.network.GithubReposApiService
import com.aidanvii.github.features.listrepos.entities.GithubRepo
import com.aidanvii.github.utils.logger.logD
import com.aidanvii.toolbox.paging.PagedList
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.BroadcastChannel
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import kotlin.coroutines.experimental.CoroutineContext

internal class GithubReposRepositoryImpl(
    githubReposApiService: GithubReposApiService,
    pageSize: Int,
    prefetchDistance: Int,
    initialPagesToLoad: IntArray,
    maxRetries: Int,
    delayBetweenRetriesMillis: Long,
    ioScheduler: Scheduler = Schedulers.io(),
    private val launchContext: CoroutineContext = CommonPool
) : GithubReposRepository {

    private val githubReposChannel = BroadcastChannel<List<GithubRepo?>>(CONFLATED)
    private val isRefreshingChannel = BroadcastChannel<Boolean>(CONFLATED)

    // TODO either replace with architecture components PagedList or replace RxJava dependency in PagedList with coroutine channels
    private val pagedList = PagedList(
        dataSource = GithubReposDataSource(
            githubReposApiService = githubReposApiService,
            ioScheduler = ioScheduler,
            maxRetries = maxRetries,
            delayBetweenRetriesMillis = delayBetweenRetriesMillis
        ),
        pageSize = pageSize,
        prefetchDistance = prefetchDistance,
        loadInitialPages = initialPagesToLoad,
        publishChangesOnInit = false
    )

    init {
        publishIsRefreshing(true)
        pagedList.observableList
            .subscribeBy {
                logD("onNext: $it")
                runBlocking(launchContext) {
                    githubReposChannel.send(it)
                    publishIsRefreshing(false)
                }
            }
    }

    override fun loadAround(index: Int) {
        pagedList.loadAround(index)
    }

    override fun refresh() {
        if (pagedList.lastIndex >= 0) {
            publishIsRefreshing(true)
            pagedList.invalidateLoadedAsDirty(refreshElementsInRange = true)
        }
    }

    override val githubRepos: ReceiveChannel<List<GithubRepo?>> get() = githubReposChannel.openSubscription()

    override val isRefreshing: ReceiveChannel<Boolean> get() = isRefreshingChannel.openSubscription()

    private fun publishIsRefreshing(isRefreshing: Boolean) {
        runBlocking(launchContext) {
            isRefreshingChannel.send(isRefreshing)
        }
    }
}