package com.aidanvii.github.features.listrepos.di

import com.aidanvii.github.features.listrepos.GithubReposRepository
import com.aidanvii.github.features.listrepos.presentation.GithubReposListViewModel
import com.aidanvii.toolbox.Provider

class ListGithubReposModule(
    private val githubReposRepositoryProvider: Provider<GithubReposRepository>
) {
    val githubReposListViewModelFactory: GithubReposListViewModel.Factory by lazy {
        GithubReposListViewModel.Factory(githubReposRepositoryProvider())
    }
}