package com.aidanvii.github.features.listrepos.presentation

import com.aidanvii.github.features.listrepos.entities.GithubRepo

internal class GithubRepoViewModel(private val githubRepo: GithubRepo) {
    val name: String get() = githubRepo.name
    val stargazersCount: String get() = githubRepo.stargazersCount.toString()
    val language: String get() = githubRepo.language ?: ""
    val ownerName: String get() = githubRepo.owner.name
    val ownerAvatarUrl: String get() = githubRepo.owner.avatarUrl
}