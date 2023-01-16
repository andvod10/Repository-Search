package com.tui.vcsrepositorysearch.service

import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder

@Component
class CustomWebClient constructor(
    private val webClient: WebClient,
) {
    fun retrieveRequest(
        pathUri: String,
        pathParams: Array<String> = arrayOf(),
        queryParams: MultiValueMap<String, String> = LinkedMultiValueMap()
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
