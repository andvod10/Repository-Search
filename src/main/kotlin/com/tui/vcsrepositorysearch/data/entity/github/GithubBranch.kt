package com.tui.vcsrepositorysearch.data.entity.github

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class GithubBranch(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("commit")
    val commit: GithubCommit
)
