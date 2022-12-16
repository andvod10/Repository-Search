package com.tui.vcsrepositorysearch.data.entity

import org.kohsuke.github.GHBranch

data class GithubBranch(
    val repositoryName: String,
    val branches: List<GHBranch>
)
