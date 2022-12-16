package com.tui.vcsrepositorysearch.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.ApiKey
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.Contact
import springfox.documentation.service.SecurityReference
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class Swagger2Config {
    @Bean
    fun apiDocket(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .securityContexts(listOf(securityContext()))
            .securitySchemes(listOf(apiKey()))
            .select()
            .apis(RequestHandlerSelectors.basePackage(("com.tui.vcsrepositorysearch")))
            .build()
            .apiInfo(getApiInfo())
    }

    private fun apiKey(): ApiKey {
        return ApiKey("JWT", "Authorization", "header")
    }

    private fun securityContext(): SecurityContext {
        return SecurityContext.builder().securityReferences(defaultAuth()).build()
    }

    private fun defaultAuth(): List<SecurityReference> {
        val authorizationScope = AuthorizationScope("global", "access to any API")
        val authorizationScopes = arrayOf(authorizationScope)
        return listOf(SecurityReference("JWT", authorizationScopes))
    }

    private fun getApiInfo(): ApiInfo {
        return ApiInfo(
            "Application API",
            "Rest API",
            "1.0",
            "TERMS OF SERVICE URL",
            Contact("Andrii Vodvud", "", "andrii.vodvud@smcebi.edu.pl"),
            "Apache 2.0",
            "http://www.apache.org/licenses/LICENSE-2.0",
            listOf()
        )
    }
}
