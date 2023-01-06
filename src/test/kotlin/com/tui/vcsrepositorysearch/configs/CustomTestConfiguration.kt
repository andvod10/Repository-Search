package com.tui.vcsrepositorysearch.configs

import com.tui.vcsrepositorysearch.configs.mock.GithubBranchMock
import com.tui.vcsrepositorysearch.configs.mock.GithubRepositoryMock
import com.tui.vcsrepositorysearch.data.entity.github.GithubBranchesCache
import com.tui.vcsrepositorysearch.data.entity.github.GithubRepo
import com.tui.vcsrepositorysearch.data.entity.github.GithubRepositoriesCache
import com.tui.vcsrepositorysearch.service.repository.github.usecase.GithubBranchService
import com.tui.vcsrepositorysearch.service.repository.github.usecase.GithubRepositoryService
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.domain.Pageable

@TestConfiguration
class CustomTestConfiguration {
    @Bean
    @Primary
    fun testGithubRepositoryService(): GithubRepositoryService {
        return object : GithubRepositoryService {
            override fun retrieveRepositoryFromGitHubByUser(
                user: String,
                pageable: Pageable,
                withForks: Boolean
            ): GithubRepositoriesCache {
                return GithubRepositoryMock().getGithubRepositoryListByUser()
            }

            override fun retrieveRepositoryByName(repositoryName: String, ownerName: String): GithubRepo {
                return GithubRepositoryMock().getGithubRepository()
            }
        }
    }

    @Bean
    @Primary
    fun testGithubBranchService(): GithubBranchService {
        return object : GithubBranchService {
            override fun retrieveBranchFromGitHubByRepository(repository: GithubRepo): GithubBranchesCache {
                return GithubBranchMock().getGithubBranchesByUserAndRepository()
            }
        }
    }
}
