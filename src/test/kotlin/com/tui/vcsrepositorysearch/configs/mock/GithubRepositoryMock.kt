package com.tui.vcsrepositorysearch.configs.mock

import com.fasterxml.jackson.databind.ObjectMapper
import com.tui.vcsrepositorysearch.data.entity.github.GithubRepo
import com.tui.vcsrepositorysearch.data.entity.github.GithubRepos
import com.tui.vcsrepositorysearch.data.entity.github.GithubRepositoriesCache
import org.springframework.util.ResourceUtils

class GithubRepositoryMock {
    private val objectMapper: ObjectMapper = ObjectMapper()

    fun getGithubRepositoryListByUser(): GithubRepositoriesCache {
        val repos = deserializeTestRepositories()
        return GithubRepositoriesCache(
            userName = "andvod10",
            totalCount = repos.totalCount,
            repositories = repos.items
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
