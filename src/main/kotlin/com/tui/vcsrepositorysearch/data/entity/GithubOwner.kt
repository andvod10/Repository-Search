package com.tui.vcsrepositorysearch.data.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class GithubOwner(
    @JsonProperty("login")
    val login: String,
    @JsonProperty("url")
    val url: String
)
