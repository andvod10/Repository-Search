package com.tui.vcsrepositorysearch.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Branch(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("commit")
    val commit: Commit
)
