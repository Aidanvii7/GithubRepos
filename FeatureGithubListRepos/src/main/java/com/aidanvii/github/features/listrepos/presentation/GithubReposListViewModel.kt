package com.aidanvii.github.features.listrepos.presentation

import android.databinding.Bindable
import android.support.v7.widget.LinearLayoutManager
import com.aidanvii.github.features.listrepos.GithubReposRepository
import com.aidanvii.github.utils.logger.logD
import com.aidanvii.toolbox.adapterviews.recyclerview.BindingRecyclerViewBinder
import com.aidanvii.toolbox.arch.viewmodel.ViewModelFactory
import com.aidanvii.toolbox.databinding.ObservableArchViewModel
import com.aidanvii.toolbox.databinding.bindable
import com.aidanvii.toolbox.delegates.coroutines.job.cancelOnReassign
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

internal class GithubReposListViewModel(
    private val githubReposRepository: GithubReposRepository,
    private val uiContext: CoroutineContext = UI,
    private val workerContext: CoroutineContext = CommonPool
) : ObservableArchViewModel() {

    private val _githubReposJob
        get(): Job = async(workerContext) {
            for (githubRepos in githubReposRepository.githubRepos) {
                logD("onNext githubRepo: $githubRepos")
                githubRepos.map { GithubRepoAdapterItem(it) }.let { adapterItems ->
                    launch(uiContext) { githubRepoAdapterItems = adapterItems }
                }
            }
            logD("githubRepo complete!")
        }

    private var githubReposJob by cancelOnReassign(_githubReposJob)

    private val _isRefreshingJob
        get(): Job = async(workerContext) {
            for (isRefreshing in githubReposRepository.isRefreshing) {
                logD("onNext isRefreshing: $isRefreshing")
                launch(uiContext) { showLoader = isRefreshing }
            }
            logD("isRefreshing complete!")
        }

    private var isRefreshingJob by cancelOnReassign(_isRefreshingJob)

    val binder = BindingRecyclerViewBinder<GithubRepoAdapterItem>(
        layoutManagerFactory = { LinearLayoutManager(it) },
        areContentsTheSame = { oldItem, newItem -> oldItem.githubRepo == newItem.githubRepo }
    )

    @get:Bindable
    var githubRepoAdapterItems: List<GithubRepoAdapterItem> by bindable(emptyList())
        private set

    @get:Bindable
    var showLoader: Boolean by bindable(false)
        private set

    fun loadAround(index: Int) = githubReposRepository.loadAround(index)

    fun refresh() = githubReposRepository.refresh()

    override fun onCleared() {
        githubReposJob = null
        isRefreshingJob = null
    }

    class Factory(
        private val githubReposRepository: GithubReposRepository
    ) : ViewModelFactory.TypedFactory<GithubReposListViewModel> {
        override fun create() = GithubReposListViewModel(githubReposRepository)
    }
}