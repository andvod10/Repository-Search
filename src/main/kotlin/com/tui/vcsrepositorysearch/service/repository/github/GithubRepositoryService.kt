package com.tui.vcsrepositorysearch.service.repository.github;

import com.tui.vcsrepositorysearch.data.entity.GithubRepository
import org.kohsuke.github.GHFork
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

interface GithubRepositoryService {
    fun retrieveRepositoryFromGitHubByUser(user: String, withForks: Boolean = false): GithubRepository
    fun retrieveRepositoryByName(repositoryName: String, ownerName: String): GHRepository?
}

@Service
class GithubRepositoryServiceImpl constructor(
    @Value("\${github.default-page-size}")
    private val githubDefaultPageSize: Int
) : GithubRepositoryService {
    private val log = LoggerFactory.getLogger(GithubRepositoryServiceImpl::class.java)

    @Cacheable(
        value = ["githubRepository"],
        key = "#user"
    )
    override fun retrieveRepositoryFromGitHubByUser(user: String, withForks: Boolean): GithubRepository {
        log.debug("Direct call to Github Repository performing...")
        val githubConnection = GitHub.connectAnonymously()
        val fork = if (withForks) {
            GHFork.PARENT_AND_FORKS
        } else {
            GHFork.PARENT_ONLY
        }
        val searchBuilder = githubConnection.searchRepositories()
            .fork(fork)
            .user(user)
            .list()
            .withPageSize(githubDefaultPageSize)
        return GithubRepository(
            userName = user,
            repositories = searchBuilder.toList()
        )
    }

    override fun retrieveRepositoryByName(repositoryName: String, ownerName: String): GHRepository? {
        val githubConnection = GitHub.connectAnonymously()
        val searchBuilder = githubConnection.searchRepositories()
            .user(ownerName)
            .q(repositoryName)
            .list()
        return searchBuilder.firstOrNull()
    }
}
