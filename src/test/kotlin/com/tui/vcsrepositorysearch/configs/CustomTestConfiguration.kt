package com.tui.vcsrepositorysearch.configs

import com.tui.vcsrepositorysearch.configs.mock.GithubBranchMock
import com.tui.vcsrepositorysearch.configs.mock.GithubRepositoryMock
import com.tui.vcsrepositorysearch.data.entity.GithubBranch
import com.tui.vcsrepositorysearch.data.entity.GithubRepository
import com.tui.vcsrepositorysearch.service.repository.github.GithubBranchService
import com.tui.vcsrepositorysearch.service.repository.github.GithubRepositoryService
import org.kohsuke.github.GHRepository
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class CustomTestConfiguration {
    @Bean
    @Primary
    fun testGithubRepositoryService(): GithubRepositoryService {
        return object : GithubRepositoryService {
            override fun retrieveRepositoryFromGitHubByUser(user: String, withForks: Boolean): GithubRepository {
               return GithubRepositoryMock().getGithubRepositoryListByUser()
            }

            override fun retrieveRepositoryByName(repositoryName: String, ownerName: String): GHRepository {
                return GithubRepositoryMock().getGithubRepository()
            }
        }
    }

    @Bean
    @Primary
    fun testGithubBranchService(): GithubBranchService {
        return object : GithubBranchService {
            override fun retrieveBranchFromGitHubByRepository(repository: GHRepository): GithubBranch {
                return GithubBranchMock().getGithubBranchesByUserAndRepository()
            }
        }
    }
}
