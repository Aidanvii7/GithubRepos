package com.aidanvii.github.features.listrepos.presentation

import android.databinding.Bindable
import android.support.v7.widget.LinearLayoutManager
import com.aidanvii.github.features.listrepos.GithubReposRepository
import com.aidanvii.toolbox.adapterviews.recyclerview.BindingRecyclerViewBinder
import com.aidanvii.toolbox.arch.viewmodel.ViewModelFactory
import com.aidanvii.toolbox.databinding.ObservableArchViewModel
import com.aidanvii.toolbox.databinding.bindable
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

internal class GithubReposListViewModel(
    private val githubReposRepository: GithubReposRepository,
    uiContext: CoroutineContext = UI,
    workerContext: CoroutineContext = CommonPool
) : ObservableArchViewModel() {

    @get:Bindable
    var showLoader: Boolean by bindable(false)
        private set

    @get:Bindable
    var githubRepoAdapterItems: List<GithubRepoAdapterItem> by bindable(emptyList())
        private set

    val binder = BindingRecyclerViewBinder<GithubRepoAdapterItem>(
        layoutManagerFactory = { LinearLayoutManager(it) },
        areContentsTheSame = { oldItem, newItem -> oldItem.githubRepo == newItem.githubRepo },
        uiContext = uiContext,
        workerContext = workerContext
    )

    private val githubReposJob = async(workerContext) {
        for (githubRepos in githubReposRepository.githubRepos) {
            githubRepos.map { GithubRepoAdapterItem(it) }.let { adapterItems ->
                launch(uiContext) { githubRepoAdapterItems = adapterItems }
            }
        }
    }

    private val isRefreshingJob = async(workerContext) {
        for (isRefreshing in githubReposRepository.isRefreshing) {
            launch(uiContext) { showLoader = isRefreshing }
        }
    }

    fun loadAround(index: Int) = githubReposRepository.loadAround(index)

    fun refresh() = githubReposRepository.refresh()

    override fun onCleared() {
        githubReposJob.cancel()
        isRefreshingJob.cancel()
    }

    class Factory(
        private val githubReposRepository: GithubReposRepository
    ) : ViewModelFactory.TypedFactory<GithubReposListViewModel> {
        override fun create() = GithubReposListViewModel(githubReposRepository)
    }
}