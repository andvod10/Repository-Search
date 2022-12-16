package com.tui.vcsrepositorysearch.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RsBranch (
    @JsonProperty("name")
    val name: String,
    @JsonProperty("last_commit_sha")
    val lastCommitSha: String
)
