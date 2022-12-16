package com.tui.vcsrepositorysearch.application.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.tui.vcsrepositorysearch.application.dto.RsBranch
import org.springframework.hateoas.RepresentationModel

data class RsRepositoryModel (
    @JsonProperty("name")
    var name: String = "",
    @JsonProperty("owner_login")
    var ownerLogin: String = "",
    @JsonProperty("branches")
    var branches: List<RsBranch> = listOf()
): RepresentationModel<RsRepositoryModel>()
