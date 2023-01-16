package com.tui.vcsrepositorysearch.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Repo(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("owner")
    val owner: Owner
)
