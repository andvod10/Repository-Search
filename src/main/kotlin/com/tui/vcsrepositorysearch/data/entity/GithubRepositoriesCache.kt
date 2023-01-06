package com.tui.vcsrepositorysearch.data.entity

data class GithubRepositoriesCache(
    val userName: String,
    val repositories: List<GithubRepo>
)
