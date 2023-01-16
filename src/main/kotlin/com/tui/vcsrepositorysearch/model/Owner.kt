package com.tui.vcsrepositorysearch.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Owner(
    @JsonProperty("login")
    val login: String,
    @JsonProperty("url")
    val url: String
)
