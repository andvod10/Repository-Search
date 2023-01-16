package com.tui.vcsrepositorysearch.service.github

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.tui.vcsrepositorysearch.config.WireMockContextInitializer
import com.tui.vcsrepositorysearch.model.Owner
import com.tui.vcsrepositorysearch.model.Repo
import com.tui.vcsrepositorysearch.service.github.exception.TooMuchSortPropertiesException
import com.tui.vcsrepositorysearch.service.github.exception.WrongPaginationException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(initializers = [WireMockContextInitializer::class])
@AutoConfigureWebTestClient
class GithubSearchRepositoryServiceTest {
    @Autowired
    private lateinit var wireMockServer: WireMockServer

    @Autowired
    private lateinit var cacheManager: CacheManager

    @Autowired
    private lateinit var githubSearchRepositoryService: GithubSearchRepositoryService

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

    private val apiResponseRepositoriesFileName = "repositories-api-response.json"
    private val repositoriesApiResponse: String? =
        this::class.java.classLoader.getResource(apiResponseRepositoriesFileName)?.readText()

    @Test
    fun whenGetRepositoryByOwner_thenSucceed() {
        val getRepositoriesUri = "${GithubUri.GET_REPOSITORIES_URI}?q=user:andvod10+fork:false&per_page=2&page=1"
        stubResponse(getRepositoriesUri, repositoriesApiResponse!!)

        val repos = this.githubSearchRepositoryService.getRepositoriesByOwner(
            ownerName = "andvod10",
            withForks = false,
            pageable = PageRequest.of(0, 2)
        )

        assertThat(repos.first.size).isEqualTo(2)
        assertThat(repos.second).isEqualTo(2)
        assertThat(repos.first).contains(
            Repo(
                name = "docker-complex",
                owner = Owner(
                    login = "andvod10",
                    url = "https://api.github.com/users/andvod10"
                )
            )
        )

        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(getRepositoriesUri)))
    }

    @Test
    fun whenGetRepositoryByOwnerWithForks_thenSucceed() {
        val getRepositoriesUri = "${GithubUri.GET_REPOSITORIES_URI}?q=user:andvod10+fork:true&per_page=2&page=1"
        stubResponse(getRepositoriesUri, repositoriesApiResponse!!)

        val repos = this.githubSearchRepositoryService.getRepositoriesByOwner(
            ownerName = "andvod10",
            withForks = true,
            pageable = PageRequest.of(0, 2)
        )

        assertThat(repos.first.size).isEqualTo(2)
        assertThat(repos.second).isEqualTo(2)
        assertThat(repos.first).contains(
            Repo(
                name = "docker-complex",
                owner = Owner(
                    login = "andvod10",
                    url = "https://api.github.com/users/andvod10"
                )
            )
        )

        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(getRepositoriesUri)))
    }

    @Test
    fun whenGetRepositoryByOwnerWithWrongPageSize_thenExceptionThrown() {
        val getRepositoriesUri = "${GithubUri.GET_REPOSITORIES_URI}?q=user:andvod10+fork:true&per_page=2&page=1"

        val pageSize = 120
        assertThatThrownBy {
            this.githubSearchRepositoryService.getRepositoriesByOwner(
                ownerName = "andvod10",
                withForks = true,
                pageable = PageRequest.of(0, pageSize)
            )
        }.isInstanceOf(WrongPaginationException::class.java)
            .hasMessage("Incorrect size $pageSize of one page. Provide page size in range 0-100.")

        wireMockServer.verify(0, WireMock.getRequestedFor(WireMock.urlEqualTo(getRepositoriesUri)))
    }

    @Test
    fun whenGetRepositoryByOwnerWithOneSortOrder_thenSuccess() {
        val getRepositoriesUri = "${GithubUri.GET_REPOSITORIES_URI}?q=user:andvod10+fork:true&per_page=2&page=1&sort=name&order=asc"
        stubResponse(getRepositoriesUri, repositoriesApiResponse!!)

        val repos = this.githubSearchRepositoryService.getRepositoriesByOwner(
            ownerName = "andvod10",
            withForks = true,
            pageable = PageRequest.of(0, 2, Sort.Direction.ASC, "name")
        )

        assertThat(repos.first.size).isEqualTo(2)
        assertThat(repos.second).isEqualTo(2)
        assertThat(repos.first).contains(
            Repo(
                name = "docker-complex",
                owner = Owner(
                    login = "andvod10",
                    url = "https://api.github.com/users/andvod10"
                )
            )
        )
        assertThat(repos.first).contains(
            Repo(
                name = "docker-frontend",
                owner = Owner(
                    login = "andvod10",
                    url = "https://api.github.com/users/andvod10"
                )
            )
        )

        wireMockServer.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo(getRepositoriesUri)))
    }

    @Test
    fun whenGetRepositoryByOwnerWithMoreThenOneSortOrder_thenExceptionThrown() {
        val getRepositoriesUri = "${GithubUri.GET_REPOSITORIES_URI}?q=user:andvod10+fork:true&per_page=2&page=1"

        assertThatThrownBy {
            this.githubSearchRepositoryService.getRepositoriesByOwner(
                ownerName = "andvod10",
                withForks = true,
                pageable = PageRequest.of(0, 2, Sort.Direction.ASC, "name", "login")
            )
        }.isInstanceOf(TooMuchSortPropertiesException::class.java)
            .hasMessage("Only one sort property allowed for GitHub. Provided 2 sorts.")

        wireMockServer.verify(0, WireMock.getRequestedFor(WireMock.urlEqualTo(getRepositoriesUri)))
    }
}
