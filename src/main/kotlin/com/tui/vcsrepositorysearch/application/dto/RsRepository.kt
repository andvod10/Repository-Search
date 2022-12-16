package com.tui.vcsrepositorysearch.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

open class RsRepository (
    @JsonProperty("name")
    val name: String = "",
    @JsonProperty("owner_login")
    val ownerLogin: String = "",
    @JsonProperty("branches")
    val branches: List<RsBranch> = listOf()
)
