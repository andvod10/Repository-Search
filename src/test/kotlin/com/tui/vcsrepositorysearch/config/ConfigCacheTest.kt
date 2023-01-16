package com.tui.vcsrepositorysearch.config

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.tui.vcsrepositorysearch.model.Owner
import com.tui.vcsrepositorysearch.model.Repo
import com.tui.vcsrepositorysearch.service.github.GithubSearchBranchService
import com.tui.vcsrepositorysearch.service.github.GithubUri
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@ContextConfiguration(initializers = [WireMockContextInitializer::class])
@AutoConfigureWebTestClient
@TestPropertySource(
    properties = [
        "repository.cache-timeout-sec=2",
        "repository.cache-size=1",
        "repository.cache-enabled=true"
    ]
)
class ConfigCacheTest {
    @Autowired
    private lateinit var githubSearchBranchService: GithubSearchBranchService

    @Autowired
    private lateinit var wireMockServer: WireMockServer

    @Autowired
    private lateinit var cacheManager: CacheManager

    @AfterEach
    fun afterEach() {
        wireMockServer.resetAll()
        cacheManager.cacheNames.stream()
            .map(cacheManager::getCache)
            .forEach {
                it?.clear()
            }
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

    private val apiResponseBranchesDockerFrontendFileName = "branches-api-response-docker-frontend.json"
    private val branchesDockerFrontendApiResponse: String? =
        this::class.java.classLoader.getResource(apiResponseBranchesDockerFrontendFileName)?.readText()

    @Test
    fun testCacheSuccessful() {
        val getBranchesDockerFrontendUri = GithubUri.GET_BRANCHES_URI
            .replace("{userName}", "andvod10")
            .replace("{repoName}", "docker-frontend")
        stubResponse(getBranchesDockerFrontendUri, branchesDockerFrontendApiResponse!!)
        val repo = Repo(
            name = "docker-frontend",
            owner = Owner(
                login = "andvod10",
                url = "url"
            )
        )

        assertThat(cacheManager.getCache("githubBranch")!!.get("docker-frontend")).isNull()
        githubSearchBranchService.getBranches(repo)

        assertThat(cacheManager.getCache("githubBranch")!!.get("docker-frontend")).isNotNull

        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(getBranchesDockerFrontendUri)))
    }

    @Test
    fun testCacheTimeout() {
        val getBranchesDockerFrontendUri = GithubUri.GET_BRANCHES_URI
            .replace("{userName}", "andvod10")
            .replace("{repoName}", "docker-frontend")
        stubResponse(getBranchesDockerFrontendUri, branchesDockerFrontendApiResponse!!)
        val repo = Repo(
            name = "docker-frontend",
            owner = Owner(
                login = "andvod10",
                url = "url"
            )
        )

        assertThat(cacheManager.getCache("githubBranch")!!.get("docker-frontend")).isNull()
        githubSearchBranchService.getBranches(repo)

        Thread.sleep(2500)

        assertThat(cacheManager.getCache("githubBranch")!!.get("docker-frontend")).isNull()

        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(getBranchesDockerFrontendUri)))
    }

    @Test
    fun testCacheSize() {
        val getBranchesDockerFrontendUri = GithubUri.GET_BRANCHES_URI
            .replace("{userName}", "andvod10")
            .replace("{repoName}", "docker-frontend")
        stubResponse(getBranchesDockerFrontendUri, branchesDockerFrontendApiResponse!!)
        val getBranchesDockerFrontendUri2 = GithubUri.GET_BRANCHES_URI
            .replace("{userName}", "andvod10")
            .replace("{repoName}", "docker-frontend2")
        stubResponse(getBranchesDockerFrontendUri2, branchesDockerFrontendApiResponse)
        val getBranchesDockerFrontendUri3 = GithubUri.GET_BRANCHES_URI
            .replace("{userName}", "andvod10")
            .replace("{repoName}", "docker-frontend3")
        stubResponse(getBranchesDockerFrontendUri3, branchesDockerFrontendApiResponse)
        val repo = Repo(
            name = "docker-frontend",
            owner = Owner(
                login = "andvod10",
                url = "url"
            )
        )
        val repo2 = Repo(
            name = "docker-frontend2",
            owner = Owner(
                login = "andvod10",
                url = "url"
            )
        )
        val repo3 = Repo(
            name = "docker-frontend3",
            owner = Owner(
                login = "andvod10",
                url = "url"
            )
        )

        assertThat(cacheManager.getCache("githubBranch")!!.get("docker-frontend")).isNull()
        githubSearchBranchService.getBranches(repo)
        githubSearchBranchService.getBranches(repo2)
        githubSearchBranchService.getBranches(repo3)

        assertThat(cacheManager.getCache("githubBranch")!!.get("docker-frontend")).isNull()

        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(getBranchesDockerFrontendUri)))
    }
}
