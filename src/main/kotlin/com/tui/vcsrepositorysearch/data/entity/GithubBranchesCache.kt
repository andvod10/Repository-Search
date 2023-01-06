package com.tui.vcsrepositorysearch.data.entity

data class GithubBranchesCache(
    val repositoryName: String,
    val branches: List<GithubBranch>
)
