package com.tui.vcsrepositorysearch.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Commit(
    @JsonProperty("sha")
    val sha: String,
    @JsonProperty("url")
    val url: String
)
