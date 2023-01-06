package com.tui.vcsrepositorysearch.data.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class GithubRepo(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("owner")
    val owner: GithubOwner
)
