package com.tui.vcsrepositorysearch.configs

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.util.concurrent.TimeUnit

@Configuration
class WebClientConfiguration {
    @Bean
    @Primary
    fun githubWebClient(
        @Value("\${github.base-url}")
        githubBaseUrl: String,
        @Value("\${github.version-api}")
        githubVersionApi: String
    ): WebClient {
        val httpClient: HttpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
            .doOnConnected { conn ->
                conn
                    .addHandlerLast(ReadTimeoutHandler(10, TimeUnit.SECONDS))
                    .addHandlerLast(WriteTimeoutHandler(10))
            }

        val connector: ClientHttpConnector = ReactorClientHttpConnector(httpClient)
        return WebClient.builder().baseUrl(githubBaseUrl)
            .clientConnector(connector)
            .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("X-GitHub-Api-Version", githubVersionApi)
            .build()
    }
}
