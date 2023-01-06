package com.tui.vcsrepositorysearch.configs.mock

import com.fasterxml.jackson.databind.ObjectMapper
import com.tui.vcsrepositorysearch.data.entity.GithubRepo
import com.tui.vcsrepositorysearch.data.entity.GithubRepos
import com.tui.vcsrepositorysearch.data.entity.GithubRepositoriesCache
import org.springframework.util.ResourceUtils

class GithubRepositoryMock {
    private val objectMapper: ObjectMapper = ObjectMapper()

    fun getGithubRepositoryListByUser(): GithubRepositoriesCache {
        return GithubRepositoriesCache(
            userName = "andvod10",
            repositories = deserializeTestRepositories().items
        )
    }

    fun getGithubRepository(): GithubRepo {
        return deserializeTestRepositories().items.first()
    }

    private fun deserializeTestRepositories(): GithubRepos {
        val file = ResourceUtils.getFile("classpath:mock_repository.json")
        return objectMapper.readValue(file.readText(), GithubRepos::class.java)
    }
}
