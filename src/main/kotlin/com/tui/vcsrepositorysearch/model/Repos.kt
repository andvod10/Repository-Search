package com.tui.vcsrepositorysearch.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Repos(
    @JsonProperty("total_count")
    val totalCount: Long,
    @JsonProperty("incomplete_results")
    val incompleteResults: Boolean,
    @JsonProperty("items")
    val items: List<Repo>
)
