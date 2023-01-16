package com.tui.vcsrepositorysearch.dto

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus

data class ErrorResponse<T>(
    val status: HttpStatus,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val code: Int? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val message: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var data: T? = null
)
