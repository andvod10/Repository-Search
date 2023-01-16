package com.tui.vcsrepositorysearch.service.github

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.tui.vcsrepositorysearch.config.WireMockContextInitializer
import com.tui.vcsrepositorysearch.model.Branch
import com.tui.vcsrepositorysearch.model.Commit
import com.tui.vcsrepositorysearch.model.Owner
import com.tui.vcsrepositorysearch.model.Repo
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

@SpringBootTest
@ContextConfiguration(initializers = [WireMockContextInitializer::class])
@AutoConfigureWebTestClient
class GithubSearchBranchServiceTest {
    @Autowired
    private lateinit var wireMockServer: WireMockServer

    @Autowired
    private lateinit var cacheManager: CacheManager

    @Autowired
    private lateinit var githubSearchBranchService: GithubSearchBranchService

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
    fun whenGetRepositoryByOwner_thenSucceed() {
        val getBranchesDockerFrontendUri = GithubUri.GET_BRANCHES_URI
            .replace("{userName}", "andvod10")
            .replace("{repoName}", "docker-frontend")
        stubResponse(getBranchesDockerFrontendUri, branchesDockerFrontendApiResponse!!)

        val branches = this.githubSearchBranchService.getBranches(
            Repo(
                name = "docker-frontend",
                owner = Owner(
                    login = "andvod10",
                    url = "https://api.github.com/users/andvod10"
                )
            )
        )

        assertThat(branches.size).isEqualTo(2)
        assertThat(branches).contains(
            Branch(
                name = "dependabot/npm_and_yarn/ajv-6.12.6",
                commit = Commit(
                    sha = "274f7919c9d45dbdbb9271885c76abe85cd85049",
                    url = "https://api.github.com/repos/andvod10/docker-frontend/commits/274f7919c9d45dbdbb9271885c76abe85cd85049"
                )
            )
        )
        assertThat(branches).contains(
            Branch(
                name = "dependabot/npm_and_yarn/async-2.6.4",
                commit = Commit(
                    sha = "e61f564d70517081b898d45b64d96aa25ce9cb9c",
                    url = "https://api.github.com/repos/andvod10/docker-frontend/commits/e61f564d70517081b898d45b64d96aa25ce9cb9c"
                )
            )
        )
        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(getBranchesDockerFrontendUri)))
    }
}
