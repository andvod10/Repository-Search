package com.tui.vcsrepositorysearch.data.entity.github

data class GithubBranchesCache(
    val repositoryName: String,
    val branches: List<GithubBranch>
)
