package com.tui.vcsrepositorysearch.service.config

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.Fault
import com.tui.vcsrepositorysearch.config.WireMockContextInitializer
import com.tui.vcsrepositorysearch.model.Branch
import com.tui.vcsrepositorysearch.model.Repos
import com.tui.vcsrepositorysearch.service.CustomWebClient
import com.tui.vcsrepositorysearch.service.github.GithubUri
import io.netty.handler.timeout.ReadTimeoutException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.domain.Sort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.client.WebClientRequestException
import java.net.SocketException
import java.time.Duration


@SpringBootTest
@ContextConfiguration(initializers = [WireMockContextInitializer::class])
@AutoConfigureWebTestClient
class WebClientConfigurationTest {
    @Autowired
    private lateinit var wireMockServer: WireMockServer

    @Autowired
    private lateinit var webClient: CustomWebClient

    @AfterEach
    fun afterEach() {
        wireMockServer.resetAll()
    }

    private fun stubResponse(url: String, responseBody: String, responseStatus: Int = HttpStatus.OK.value()) {
        wireMockServer.stubFor(
            WireMock.get(url)
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(responseStatus)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responseBody)
                )
        )
    }

    private val apiResponseRepositoriesFileName = "repositories-api-response.json"
    private val repositoriesApiResponse: String? =
        this::class.java.classLoader.getResource(apiResponseRepositoriesFileName)?.readText()
    private val apiResponseBranchesDockerFrontendFileName = "branches-api-response-docker-frontend.json"
    private val branchesDockerFrontendApiResponse: String? =
        this::class.java.classLoader.getResource(apiResponseBranchesDockerFrontendFileName)?.readText()

    @Test
    fun whenRetrieveRequestWithQueryParams_thenResponseSpecSuccessed() {
        val getRepositoriesUri =
            "${GithubUri.GET_REPOSITORIES_URI}?q=user:andvod10+fork:false&per_page=2&page=1&sort=name&order=asc"
        stubResponse(getRepositoriesUri, repositoriesApiResponse!!)

        val queryParams = LinkedMultiValueMap<String, String>()
        queryParams.add("q", "user:andvod10+fork:false")
        queryParams.add("per_page", "2")
        queryParams.add("page", "1")
        queryParams.add("sort", "name")
        queryParams.add("order", Sort.Direction.ASC.name.lowercase())

        val responseSpec = this.webClient.retrieveRequest(
            pathUri = GithubUri.GET_REPOSITORIES_URI,
            queryParams = queryParams
        )

        responseSpec.bodyToMono(Repos::class.java)
            .block(Duration.ofMillis(1000))

        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(getRepositoriesUri)))
    }

    @Test
    fun whenRetrieveRequestWithRequestPath_thenResponseSpecSuccessed() {
        val getBranchesDockerFrontendUri = GithubUri.GET_BRANCHES_URI
            .replace("{userName}", "andvod10")
            .replace("{repoName}", "docker-frontend")
        stubResponse(getBranchesDockerFrontendUri, branchesDockerFrontendApiResponse!!)

        val pathParams = arrayOf("andvod10", "docker-frontend")

        val responseSpec = this.webClient.retrieveRequest(
            pathUri = GithubUri.GET_BRANCHES_URI,
            pathParams = pathParams
        )

        responseSpec.bodyToMono(object : ParameterizedTypeReference<List<Branch>>() {})
            .block(Duration.ofMillis(1000))

        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(getBranchesDockerFrontendUri)))
    }

    @Test
    fun whenRetrieveRequestWithExceedReadTimeout_thenExceptionThrown() {
        val getBranchesDockerFrontendUri = GithubUri.GET_BRANCHES_URI
            .replace("{userName}", "andvod10")
            .replace("{repoName}", "docker-frontend")

        wireMockServer.stubFor(
            WireMock.get(getBranchesDockerFrontendUri)
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(branchesDockerFrontendApiResponse)
                        .withFixedDelay(600)
                )
        )

        val pathParams = arrayOf("andvod10", "docker-frontend")

        val responseSpec = this.webClient.retrieveRequest(
            pathUri = GithubUri.GET_BRANCHES_URI,
            pathParams = pathParams
        )

        assertThatThrownBy {
            responseSpec.bodyToMono(object : ParameterizedTypeReference<List<Branch>>() {})
                .block(Duration.ofMillis(1000))
        }.isInstanceOf(WebClientRequestException::class.java)
            .hasCause(ReadTimeoutException())

        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(getBranchesDockerFrontendUri)))
    }

    @Test
    fun whenRetrieveRequestWithExceedConnectionTimeout_thenExceptionThrown() {
        val getBranchesDockerFrontendUri = GithubUri.GET_BRANCHES_URI
            .replace("{userName}", "andvod10")
            .replace("{repoName}", "docker-frontend")
        wireMockServer.stubFor(
            WireMock.get(getBranchesDockerFrontendUri)
                .willReturn(
                    WireMock.aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)
                )
        )

        val pathParams = arrayOf("andvod10", "docker-frontend")

        val responseSpec = this.webClient.retrieveRequest(
            pathUri = GithubUri.GET_BRANCHES_URI,
            pathParams = pathParams
        )

        assertThatThrownBy {
            responseSpec.bodyToMono(object : ParameterizedTypeReference<List<Branch>>() {})
                .block(Duration.ofMillis(1000))
        }.isInstanceOf(WebClientRequestException::class.java)
            .hasCause(SocketException("Connection reset"))

        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(getBranchesDockerFrontendUri)))
    }
}
