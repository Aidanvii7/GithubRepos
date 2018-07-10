package com.aidanvii.github.features.listrepos.presentation

import android.databinding.Bindable
import android.support.v7.widget.LinearLayoutManager
import com.aidanvii.github.features.listrepos.GithubReposRepository
import com.aidanvii.github.utils.logger.logD
import com.aidanvii.toolbox.adapterviews.databinding.BindableAdapterItem
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

class GithubReposListViewModel(
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

    private val areSame: (
        oldItem: BindableAdapterItem,
        newItem: BindableAdapterItem,
        areGithubRepoAdapterItemsTheSame: (GithubRepoAdapterItem, GithubRepoAdapterItem) -> Boolean
    ) -> Boolean = { oldItem, newItem, areGithubRepoAdapterItemsTheSame ->
        when {
            oldItem === newItem -> true
            oldItem is GithubRepoAdapterItem && newItem is GithubRepoAdapterItem -> areGithubRepoAdapterItemsTheSame(oldItem, newItem)
            else -> false
        }
    }

    val binder = BindingRecyclerViewBinder<BindableAdapterItem>(
        hasMultipleViewTypes = true,
        layoutManagerFactory = { LinearLayoutManager(it) },
        areContentsTheSame = { oldItem, newItem ->
            areSame(oldItem, newItem) { oldGithubRepoAdapterItem, newGithubRepoAdapterItem ->
                oldGithubRepoAdapterItem.githubRepo == newGithubRepoAdapterItem.githubRepo
            }
        }
    )

    @get:Bindable
    var githubRepoAdapterItems: List<BindableAdapterItem> by bindable(emptyList())
        private set

    @get:Bindable
    var showLoader: Boolean by bindable(false)
        private set

    fun loadAround(index: Int) {
        githubReposRepository.loadAround(index)
    }

    fun refresh() {
        githubReposRepository.refresh()
    }

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