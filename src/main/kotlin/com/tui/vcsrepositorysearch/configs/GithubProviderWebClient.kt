package com.tui.vcsrepositorysearch.configs

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration {
    @Bean
    @Primary
    fun githubWebClient(@Value("\${github.base-url}") githubBaseUrl: String): WebClient {
        return WebClient.builder().baseUrl(githubBaseUrl)
            .build()
    }
}
