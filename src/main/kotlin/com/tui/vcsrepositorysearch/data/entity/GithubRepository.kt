package com.tui.vcsrepositorysearch.data.entity

import org.kohsuke.github.GHRepository

data class GithubRepository(
    val userName: String,
    val repositories: List<GHRepository>
)
