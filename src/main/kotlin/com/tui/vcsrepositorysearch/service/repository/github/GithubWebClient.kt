package com.tui.vcsrepositorysearch.service.repository.github

import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder

interface GithubWebClient {
    fun retrieveRequest(
        pathUri: String,
        pathParams: Array<String> = arrayOf(),
        queryParams: MultiValueMap<String, String> = LinkedMultiValueMap()
    ): WebClient.ResponseSpec
}

@Component
class GithubWebClientImpl constructor(
    private val webClient: WebClient,
): GithubWebClient {
    override fun retrieveRequest(
        pathUri: String,
        pathParams: Array<String>,
        queryParams: MultiValueMap<String, String>
    ): WebClient.ResponseSpec {
        return webClient.get()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder
                    .path(pathUri)
                    .queryParams(queryParams)
                    .build(*pathParams)
            }
            .retrieve()
    }
}
