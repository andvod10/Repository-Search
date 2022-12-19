package com.tui.vcsrepositorysearch

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@OpenAPIDefinition(
    info = Info(
        title = "Repository Search API",
        version = "0.0.1",
        description = "Interface for retrieving info from VCS. Current version support GitHub."
    )
)
@SecurityScheme(
    name = "api_key",
    type = SecuritySchemeType.APIKEY,
    `in` = SecuritySchemeIn.HEADER,
    description = "Enter the token with the `Bearer: ` prefix, e.g. 'Bearer abcde12345;."
)
@SpringBootApplication
class VcsRepositorySearchApplication

fun main(args: Array<String>) {
    runApplication<VcsRepositorySearchApplication>(*args)
}
