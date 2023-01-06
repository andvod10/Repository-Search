package com.tui.vcsrepositorysearch.data.entity.github

data class GithubRepositoriesCache(
    val userName: String,
    val totalCount: Int,
    val repositories: List<GithubRepo>
)
