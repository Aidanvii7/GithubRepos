package com.aidanvii.github.features.listrepos

import android.support.annotation.IntRange
import com.aidanvii.github.features.listrepos.entities.GithubRepo
import kotlinx.coroutines.experimental.channels.ReceiveChannel

interface GithubReposRepository {
    fun loadAround(@IntRange(from = 0) index: Int)
    fun refresh()
    val githubRepos: ReceiveChannel<List<GithubRepo?>>
    val isRefreshing: ReceiveChannel<Boolean>
}