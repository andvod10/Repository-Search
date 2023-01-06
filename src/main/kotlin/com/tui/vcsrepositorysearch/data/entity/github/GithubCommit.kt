package com.tui.vcsrepositorysearch.data.entity.github

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class GithubCommit(
    @JsonProperty("sha")
    val sha: String,
    @JsonProperty("url")
    val url: String
)