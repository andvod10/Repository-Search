package com.tui.vcsrepositorysearch.dto

import com.fasterxml.jackson.annotation.JsonProperty

open class RepositoryDto (
    @JsonProperty("name")
    var name: String = "",
    @JsonProperty("owner_login")
    var ownerLogin: String = "",
    @JsonProperty("branches")
    var branches: List<BranchDto> = listOf()
)
