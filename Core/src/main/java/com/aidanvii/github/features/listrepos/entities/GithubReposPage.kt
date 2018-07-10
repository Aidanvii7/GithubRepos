package com.aidanvii.github.features.listrepos.entities

interface GithubReposPage {
    val totalRepos: Int
    val reposInPage: List<GithubRepo>
}