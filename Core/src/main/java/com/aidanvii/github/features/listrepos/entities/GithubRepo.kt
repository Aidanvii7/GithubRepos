package com.aidanvii.github.features.listrepos.entities

interface GithubRepo {
    val id: Int
    val name: String
    val stargazersCount: Int
    val language: String?
    val owner: GithubRepoOwner
}