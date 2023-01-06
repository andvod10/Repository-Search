package com.tui.vcsrepositorysearch.data.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class GithubRepos(
    @JsonProperty("total_count")
    val totalCount: Int,
    @JsonProperty("incomplete_results")
    val incompleteResults: Boolean,
    @JsonProperty("items")
    val items: List<GithubRepo>
)
