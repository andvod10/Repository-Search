package com.tui.vcsrepositorysearch.service.repository.github

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
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
    @Value("\${github.version-api}")
    private val githubVersionApi: String
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
            .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header("X-GitHub-Api-Version", githubVersionApi)
            .retrieve()
    }
}
