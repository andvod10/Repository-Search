package com.tui.vcsrepositorysearch.application

import com.tui.vcsrepositorysearch.configs.CustomTestConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@Import(CustomTestConfiguration::class)
@ExtendWith(SpringExtension::class)
class RepositoryIntegrationTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun whenReadAllRepositoriesByName_thenStatusSucceed() {
        mockMvc.perform(
            get("$REPOSITORY_PATH/owner/andvod10")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    """
                    {
                      "_embedded" : {
                        "rsRepositoryModels" : [ {
                          "name" : "docker-frontend",
                          "owner_login" : "andvod10",
                          "branches" : [ {
                            "name" : "dependabot/npm_and_yarn/ajv-6.12.6",
                            "last_commit_sha" : "274f7919c9d45dbdbb9271885c76abe85cd85049"
                          }, {
                            "name" : "dependabot/npm_and_yarn/async-2.6.4",
                            "last_commit_sha" : "e61f564d70517081b898d45b64d96aa25ce9cb9c"
                          } ],
                          "_links" : {
                            "self" : {
                              "href" : "http://localhost/api/v1/repositories/docker-frontend/owner/andvod10"
                            },
                            "branches" : {
                              "href" : "http://localhost/api/v1/repositories/docker-frontend/owner/andvod10/branches"
                            }
                          }
                        }, {
                          "name" : "docker-complex",
                          "owner_login" : "andvod10",
                          "branches" : [ {
                            "name" : "dependabot/npm_and_yarn/ajv-6.12.6",
                            "last_commit_sha" : "274f7919c9d45dbdbb9271885c76abe85cd85049"
                          }, {
                            "name" : "dependabot/npm_and_yarn/async-2.6.4",
                            "last_commit_sha" : "e61f564d70517081b898d45b64d96aa25ce9cb9c"
                          } ],
                          "_links" : {
                            "self" : {
                              "href" : "http://localhost/api/v1/repositories/docker-complex/owner/andvod10"
                            },
                            "branches" : {
                              "href" : "http://localhost/api/v1/repositories/docker-complex/owner/andvod10/branches"
                            }
                          }
                        } ]
                      },
                      "_links" : {
                        "self" : {
                          "href" : "http://localhost/api/v1/repositories/owner/andvod10?page=0&size=20"
                        }
                      },
                      "page" : {
                        "size" : 20,
                        "totalElements" : 2,
                        "totalPages" : 1,
                        "number" : 0
                      }
                    }
            """, true
                )
            ).andReturn()
    }

    @Test
    fun whenReadAllRepositoriesByName_withPagination_thenStatusSucceed() {
        mockMvc.perform(
            get("$REPOSITORY_PATH/owner/andvod10")
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
                    "rsRepositoryModels" : [ {
                      "name" : "docker-frontend",
                      "owner_login" : "andvod10",
                      "branches" : [ {
                        "name" : "dependabot/npm_and_yarn/ajv-6.12.6",
                        "last_commit_sha" : "274f7919c9d45dbdbb9271885c76abe85cd85049"
                      }, {
                        "name" : "dependabot/npm_and_yarn/async-2.6.4",
                        "last_commit_sha" : "e61f564d70517081b898d45b64d96aa25ce9cb9c"
                      } ],
                      "_links" : {
                        "self" : {
                          "href" : "http://localhost/api/v1/repositories/docker-frontend/owner/andvod10"
                        },
                        "branches" : {
                          "href" : "http://localhost/api/v1/repositories/docker-frontend/owner/andvod10/branches"
                        }
                      }
                    } ]
                  },
                  "_links" : {
                    "first" : {
                      "href" : "http://localhost/api/v1/repositories/owner/andvod10?page=0&size=1"
                    },
                    "prev" : {
                      "href" : "http://localhost/api/v1/repositories/owner/andvod10?page=0&size=1"
                    },
                    "self" : {
                      "href" : "http://localhost/api/v1/repositories/owner/andvod10?page=1&size=1"
                    },
                    "last" : {
                      "href" : "http://localhost/api/v1/repositories/owner/andvod10?page=1&size=1"
                    }
                  },
                  "page" : {
                    "size" : 1,
                    "totalElements" : 2,
                    "totalPages" : 2,
                    "number" : 1
                  }
                }
            """, true
                )
            ).andReturn()
    }

    @Test
    fun whenReadAllRepositoriesByName_sorted_thenStatusSucceed() {
        mockMvc.perform(
            get("$REPOSITORY_PATH/owner/andvod10")
                .accept(MediaType.APPLICATION_JSON)
                .queryParam("sort", "name,asc")
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    """
                    {
                      "_embedded" : {
                        "rsRepositoryModels" : [ {
                          "name" : "docker-complex",
                          "owner_login" : "andvod10",
                          "branches" : [ {
                            "name" : "dependabot/npm_and_yarn/ajv-6.12.6",
                            "last_commit_sha" : "274f7919c9d45dbdbb9271885c76abe85cd85049"
                          }, {
                            "name" : "dependabot/npm_and_yarn/async-2.6.4",
                            "last_commit_sha" : "e61f564d70517081b898d45b64d96aa25ce9cb9c"
                          } ],
                          "_links" : {
                            "self" : {
                              "href" : "http://localhost/api/v1/repositories/docker-complex/owner/andvod10"
                            },
                            "branches" : {
                              "href" : "http://localhost/api/v1/repositories/docker-complex/owner/andvod10/branches"
                            }
                          }
                        }, {
                          "name" : "docker-frontend",
                          "owner_login" : "andvod10",
                          "branches" : [ {
                            "name" : "dependabot/npm_and_yarn/ajv-6.12.6",
                            "last_commit_sha" : "274f7919c9d45dbdbb9271885c76abe85cd85049"
                          }, {
                            "name" : "dependabot/npm_and_yarn/async-2.6.4",
                            "last_commit_sha" : "e61f564d70517081b898d45b64d96aa25ce9cb9c"
                          } ],
                          "_links" : {
                            "self" : {
                              "href" : "http://localhost/api/v1/repositories/docker-frontend/owner/andvod10"
                            },
                            "branches" : {
                              "href" : "http://localhost/api/v1/repositories/docker-frontend/owner/andvod10/branches"
                            }
                          }
                        } ]
                      },
                      "_links" : {
                        "self" : {
                          "href" : "http://localhost/api/v1/repositories/owner/andvod10?page=0&size=20&sort=name,asc"
                        }
                      },
                      "page" : {
                        "size" : 20,
                        "totalElements" : 2,
                        "totalPages" : 1,
                        "number" : 0
                      }
                    }
            """, true
                )
            ).andReturn()
    }

    @Test
    fun whenReadAllRepositoriesByName_withWrongPagination_thenStatusNotFound() {
        mockMvc.perform(
            get("$REPOSITORY_PATH/owner/andvod10")
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
                          "href" : "http://localhost/api/v1/repositories/owner/andvod10?page=0&size=2"
                        },
                        "prev" : {
                          "href" : "http://localhost/api/v1/repositories/owner/andvod10?page=1&size=2"
                        },
                        "self" : {
                          "href" : "http://localhost/api/v1/repositories/owner/andvod10?page=2&size=2"
                        },
                        "last" : {
                          "href" : "http://localhost/api/v1/repositories/owner/andvod10?page=0&size=2"
                        }
                      },
                      "page" : {
                        "size" : 2,
                        "totalElements" : 0,
                        "totalPages" : 0,
                        "number" : 2
                      }
                    }
                """, true
                )
            ).andReturn()
    }

    @Test
    fun whenReadAllRepositoriesByName_withWrongPagination_thenStatusNotAcceptable() {
        mockMvc.perform(
            get("$REPOSITORY_PATH/owner/andvod10")
                .accept(MediaType.APPLICATION_XML)
        ).andExpect(status().isNotAcceptable)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    """
                    {"status":"NOT_ACCEPTABLE","code":406,"message":"Could not find acceptable representation"}
                """, true
                )
            ).andReturn()
    }

    @Test
    fun whenUsedWrongPath_thenStatusIsForbidden() {
        mockMvc.perform(get("$REPOSITORY_PATH+WRONG_PATH"))
            .andExpect(status().isForbidden)
            .andReturn()
    }

    @Test
    fun whenReadRepositoryByName_thenStatusSucceed() {
        mockMvc.perform(
            get("$REPOSITORY_PATH/docker-complex/owner/andvod10")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    """
                    {
                      "name" : "docker-complex",
                      "owner_login" : "andvod10",
                      "branches" : [ {
                        "name" : "dependabot/npm_and_yarn/ajv-6.12.6",
                        "last_commit_sha" : "274f7919c9d45dbdbb9271885c76abe85cd85049"
                      }, {
                        "name" : "dependabot/npm_and_yarn/async-2.6.4",
                        "last_commit_sha" : "e61f564d70517081b898d45b64d96aa25ce9cb9c"
                      } ]
                    }
                    """, true
                )
            ).andReturn()
    }

    @Test
    fun whenReadBranchesRepositoryByName_thenStatusSucceed() {
        mockMvc.perform(
            get("$REPOSITORY_PATH/docker-complex/owner/andvod10/branches")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    """
                    [ 
                      {
                        "name" : "dependabot/npm_and_yarn/ajv-6.12.6",
                        "last_commit_sha" : "274f7919c9d45dbdbb9271885c76abe85cd85049"
                      }, {
                        "name" : "dependabot/npm_and_yarn/async-2.6.4",
                        "last_commit_sha" : "e61f564d70517081b898d45b64d96aa25ce9cb9c"
                      }
                    ]
                    """, true
                )
            )
            .andReturn()
    }

    companion object {
        private const val REPOSITORY_PATH = "/${APIVersions.API}/${APIVersions.V1}/repositories"
    }
}
