package com.tui.vcsrepositorysearch.controller

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.tui.vcsrepositorysearch.APIVersion
import com.tui.vcsrepositorysearch.config.WireMockContextInitializer
import com.tui.vcsrepositorysearch.service.github.GithubUri
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@ContextConfiguration(initializers = [WireMockContextInitializer::class])
@AutoConfigureWebTestClient
@AutoConfigureMockMvc
class VcsRepositoryIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

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
    private val apiResponseBranchesDockerComplexFileName = "branches-api-response-docker-complex.json"
    private val branchesDockerComplexApiResponse: String? =
        this::class.java.classLoader.getResource(apiResponseBranchesDockerComplexFileName)?.readText()
    private val apiResponseRepositoriesFileName = "repositories-api-response.json"
    private val repositoriesApiResponse: String? =
        this::class.java.classLoader.getResource(apiResponseRepositoriesFileName)?.readText()
    private val apiResponseRepositoriesWithPaginationFileName = "repositories-api-response-with-pagination.json"
    private val repositoriesWithPaginationApiResponse: String? =
        this::class.java.classLoader.getResource(apiResponseRepositoriesWithPaginationFileName)?.readText()
    private val apiResponseRepositoriesWithWrongPaginationFileName = "repositories-api-response-with-wrong-pagination.json"
    private val repositoriesWithWrongPaginationApiResponse: String? =
        this::class.java.classLoader.getResource(apiResponseRepositoriesWithWrongPaginationFileName)?.readText()

    @Test
    fun whenReadAllRepositoriesByName_thenStatusSucceed() {
        val getBranchesDockerFrontendUri = GithubUri.GET_BRANCHES_URI
            .replace("{userName}", "andvod10")
            .replace("{repoName}", "docker-frontend")
        stubResponse(getBranchesDockerFrontendUri, branchesDockerFrontendApiResponse!!)
        val getBranchesDockerComplexUri = GithubUri.GET_BRANCHES_URI
            .replace("{userName}", "andvod10")
            .replace("{repoName}", "docker-complex")
        stubResponse(getBranchesDockerComplexUri, branchesDockerComplexApiResponse!!)
        val getRepositoriesUri = "${GithubUri.GET_REPOSITORIES_URI}?q=user:andvod10+fork:false&per_page=20&page=1"
        stubResponse(getRepositoriesUri, repositoriesApiResponse!!)

        mockMvc.perform(
            get("$OWNER_PATH/andvod10/repositories")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    """
                    {
                      "_embedded" : {
                        "repositoryPageableDtoModels" : [ {
                          "name" : "docker-frontend",
                          "owner_login" : "andvod10",
                          "branches" : [ {
                            "name" : "dependabot/npm_and_yarn/ajv-6.12.6",
                            "last_commit_sha" : "274f7919c9d45dbdbb9271885c76abe85cd85049"
                          }, {
                            "name" : "dependabot/npm_and_yarn/async-2.6.4",
                            "last_commit_sha" : "e61f564d70517081b898d45b64d96aa25ce9cb9c"
                          } ]
                        }, {
                          "name" : "docker-complex",
                          "owner_login" : "andvod10",
                          "branches" : [ {
                            "name" : "dependabot/npm_and_yarn/client/axios-0.21.1",
                            "last_commit_sha" : "29721c7508a3aa47ca763fc76d76723ef3ac6a4c"
                          }, {
                            "name" : "master",
                            "last_commit_sha" : "b594106375c2cae459164cbfa848fea0ebd3a29c"
                          } ]
                        } ]
                      },
                      "_links" : {
                        "self" : {
                          "href" : "http://localhost/api/v1/owner/andvod10/repositories?page=0&size=20"
                        }
                      },
                      "page" : {
                        "size" : 20,
                        "totalElements" : 2,
                        "totalPages" : 1,
                        "number" : 0
                      }
                    }
            """, false
                )
            )
        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(getBranchesDockerComplexUri)))
        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(getBranchesDockerFrontendUri)))
        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(getRepositoriesUri)))
    }

    @Test
    fun whenReadAllRepositoriesByName_withPagination_thenStatusSucceed() {
        val getBranchesDockerFrontendUri = GithubUri.GET_BRANCHES_URI
            .replace("{userName}", "andvod10")
            .replace("{repoName}", "docker-frontend")
        stubResponse(getBranchesDockerFrontendUri, branchesDockerFrontendApiResponse!!)
        val getRepositoriesUri = "${GithubUri.GET_REPOSITORIES_URI}?q=user:andvod10+fork:false&per_page=1&page=2"
        stubResponse(getRepositoriesUri, repositoriesWithPaginationApiResponse!!)

        mockMvc.perform(
            get("$OWNER_PATH/andvod10/repositories")
                .accept(MediaType.APPLICATION_JSON)
                .queryParam("size", "1")
                .queryParam("page", "1")
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    """
                {
                  "_embedded" : {
                    "repositoryPageableDtoModels" : [ {
                      "name" : "docker-frontend",
                      "owner_login" : "andvod10",
                      "branches" : [ {
                        "name" : "dependabot/npm_and_yarn/ajv-6.12.6",
                        "last_commit_sha" : "274f7919c9d45dbdbb9271885c76abe85cd85049"
                      }, {
                        "name" : "dependabot/npm_and_yarn/async-2.6.4",
                        "last_commit_sha" : "e61f564d70517081b898d45b64d96aa25ce9cb9c"
                      } ]
                    } ]
                  },
                  "_links" : {
                    "first" : {
                      "href" : "http://localhost/api/v1/owner/andvod10/repositories?page=0&size=1"
                    },
                    "prev" : {
                      "href" : "http://localhost/api/v1/owner/andvod10/repositories?page=0&size=1"
                    },
                    "self" : {
                      "href" : "http://localhost/api/v1/owner/andvod10/repositories?page=1&size=1"
                    },
                    "last" : {
                      "href" : "http://localhost/api/v1/owner/andvod10/repositories?page=1&size=1"
                    }
                  },
                  "page" : {
                    "size" : 1,
                    "totalElements" : 2,
                    "totalPages" : 2,
                    "number" : 1
                  }
                }
            """, false
                )
            )
        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(getBranchesDockerFrontendUri)))
        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(getRepositoriesUri)))
    }

    @Test
    fun whenReadAllRepositoriesByName_sorted_thenStatusSucceed() {
        val getBranchesDockerFrontendUri = GithubUri.GET_BRANCHES_URI
            .replace("{userName}", "andvod10")
            .replace("{repoName}", "docker-frontend")
        stubResponse(getBranchesDockerFrontendUri, branchesDockerFrontendApiResponse!!)
        val getBranchesDockerComplexUri = GithubUri.GET_BRANCHES_URI
            .replace("{userName}", "andvod10")
            .replace("{repoName}", "docker-complex")
        stubResponse(getBranchesDockerComplexUri, branchesDockerComplexApiResponse!!)
        val getRepositoriesUri = "${GithubUri.GET_REPOSITORIES_URI}?q=user:andvod10+fork:false&per_page=20&page=1&sort=name&order=asc"
        stubResponse(getRepositoriesUri, repositoriesApiResponse!!)

        mockMvc.perform(
            get("$OWNER_PATH/andvod10/repositories")
                .accept(MediaType.APPLICATION_JSON)
                .queryParam("sort", "name,asc")
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    """
                    {
                      "_embedded" : {
                        "repositoryPageableDtoModels" : [ {
                          "name" : "docker-complex",
                          "owner_login" : "andvod10",
                          "branches" : [ {
                            "name" : "dependabot/npm_and_yarn/client/axios-0.21.1",
                            "last_commit_sha" : "29721c7508a3aa47ca763fc76d76723ef3ac6a4c"
                          }, {
                            "name" : "master",
                            "last_commit_sha" : "b594106375c2cae459164cbfa848fea0ebd3a29c"
                          } ]
                        }, {
                          "name" : "docker-frontend",
                          "owner_login" : "andvod10",
                          "branches" : [ {
                            "name" : "dependabot/npm_and_yarn/ajv-6.12.6",
                            "last_commit_sha" : "274f7919c9d45dbdbb9271885c76abe85cd85049"
                          }, {
                            "name" : "dependabot/npm_and_yarn/async-2.6.4",
                            "last_commit_sha" : "e61f564d70517081b898d45b64d96aa25ce9cb9c"
                          } ]
                        } ]
                      },
                      "_links" : {
                        "self" : {
                          "href" : "http://localhost/api/v1/owner/andvod10/repositories?page=0&size=20&sort=name,asc"
                        }
                      },
                      "page" : {
                        "size" : 20,
                        "totalElements" : 2,
                        "totalPages" : 1,
                        "number" : 0
                      }
                    }
            """, false
                )
            )
        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(getBranchesDockerComplexUri)))
        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(getBranchesDockerFrontendUri)))
        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(getRepositoriesUri)))
    }

    @Test
    fun whenReadAllRepositoriesByName_withWrongPagination_thenStatusNotFound() {
        val getRepositoriesUri = "${GithubUri.GET_REPOSITORIES_URI}?q=user:andvod10+fork:false&per_page=2&page=3"
        stubResponse(getRepositoriesUri, repositoriesWithWrongPaginationApiResponse!!)

        mockMvc.perform(
            get("$OWNER_PATH/andvod10/repositories")
                .accept(MediaType.APPLICATION_JSON)
                .queryParam("size", "2")
                .queryParam("page", "2")
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    """
                    {
                      "_links" : {
                        "first" : {
                          "href" : "http://localhost/api/v1/owner/andvod10/repositories?page=0&size=2"
                        },
                        "prev" : {
                          "href" : "http://localhost/api/v1/owner/andvod10/repositories?page=1&size=2"
                        },
                        "self" : {
                          "href" : "http://localhost/api/v1/owner/andvod10/repositories?page=2&size=2"
                        },
                        "last" : {
                          "href" : "http://localhost/api/v1/owner/andvod10/repositories?page=0&size=2"
                        }
                      },
                      "page" : {
                        "size" : 2,
                        "totalElements" : 0,
                        "totalPages" : 0,
                        "number" : 2
                      }
                    }
                """, false
                )
            )
        wireMockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(getRepositoriesUri)))
    }

    @Test
    fun whenReadAllRepositoriesByName_withWrongPagination_thenStatusNotAcceptable() {
        mockMvc.perform(
            get("$OWNER_PATH/andvod10/repositories")
                .accept(MediaType.APPLICATION_XML)
        ).andExpect(status().isNotAcceptable)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    """
                    {"status":"NOT_ACCEPTABLE","code":406,"message":"Could not find acceptable representation"}
                """, true
                )
            )
    }

    @Test
    fun whenUsedWrongPath_thenStatusIsForbidden() {
        mockMvc.perform(get("$OWNER_PATH+WRONG_PATH"))
            .andExpect(status().isForbidden)
            .andReturn()
    }

    companion object {
        private const val OWNER_PATH = "/${APIVersion.API}/${APIVersion.V1}/owner"
    }
}
