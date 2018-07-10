package com.aidanvii.github.features.listrepos.presentation

import com.aidanvii.github.features.listrepos.entities.GithubRepo
import com.aidanvii.github.features.listRepos.R
import com.aidanvii.toolbox.adapterviews.databinding.BindableAdapterItem

data class GithubRepoAdapterItem(
    val githubRepo: GithubRepo?
) : BindableAdapterItem.Base() {

    override val layoutId: Int get() = R.layout.card_repo

    override val lazyBindableItem = lazy { githubRepo?.let { GithubRepoViewModel(githubRepo) } }

    override val isEmpty: Boolean get() = githubRepo == null
}