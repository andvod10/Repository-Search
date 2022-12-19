package com.tui.vcsrepositorysearch.configs.mock

import com.tui.vcsrepositorysearch.configs.RepositorySearchResultForTests
import com.tui.vcsrepositorysearch.data.entity.GithubRepository
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub
import org.springframework.util.ResourceUtils

class GithubRepositoryMock {
    fun getGithubRepositoryListByUser(): GithubRepository {
        return GithubRepository(
            userName = "andvod10",
            repositories = deserializeTestRepositories().items
        )
    }

    fun getGithubRepository(): GHRepository {
        return deserializeTestRepositories().items.first()
    }

    private fun deserializeTestRepositories(): RepositorySearchResultForTests {
        val file = ResourceUtils.getFile("classpath:mock_repository.json")
        return GitHub.getMappingObjectReader().readValue(file.readText(), RepositorySearchResultForTests::class.java)
    }
}
