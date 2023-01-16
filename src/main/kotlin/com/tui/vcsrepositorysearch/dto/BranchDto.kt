package com.tui.vcsrepositorysearch.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class BranchDto (
    @JsonProperty("name")
    val name: String,
    @JsonProperty("last_commit_sha")
    val lastCommitSha: String
)
