package com.tui.vcsrepositorysearch.service.repository.github.usecase

import com.tui.vcsrepositorysearch.data.entity.github.GithubRepo
import com.tui.vcsrepositorysearch.data.entity.github.GithubRepos
import com.tui.vcsrepositorysearch.data.entity.github.GithubRepositoriesCache
import com.tui.vcsrepositorysearch.service.repository.github.GithubUri
import com.tui.vcsrepositorysearch.service.repository.github.GithubWebClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.time.Duration

interface GithubRepositoryService {
    fun retrieveRepositoryFromGitHubByUser(
        user: String,
        pageable: Pageable,
        withForks: Boolean = false
    ): GithubRepositoriesCache

    fun retrieveRepositoryByName(repositoryName: String, ownerName: String): GithubRepo?
}

@Service
class GithubRepositoryServiceImpl constructor(
    @Value("\${github.default-page-size}")
    private val githubDefaultPageSize: Int,
    @Value("\${github.duration-of-single-execution-millis}")
    private val githubDurationOfSingleExecutionMillis: Long,
    @Value("\${github.max-count-of-cacheable-items}")
    val githubMaxCountOfCacheableItems: Long,
    private val githubWebClient: GithubWebClient
) : GithubRepositoryService {
    private val log = LoggerFactory.getLogger(GithubRepositoryServiceImpl::class.java)

    @Cacheable(
        value = ["githubRepository"],
        key = "#user",
        unless="#result.totalCount > #root.target.githubMaxCountOfCacheableItems"
    )
    override fun retrieveRepositoryFromGitHubByUser(
        user: String,
        pageable: Pageable,
        withForks: Boolean
    ): GithubRepositoriesCache {
        log.debug("Direct call to Github Repository performing...")

        val qParam = buildQParam(user, withForks)
        val queryParams = LinkedMultiValueMap<String, String>()
        queryParams.add("q", qParam)
        queryParams.add("per_page", pageable.pageSize.toString())
        queryParams.add("page", pageable.pageNumber.toString())

        val githubRepos = retrieveRepositoriesByWebClient(queryParams)
        val totalCount = githubRepos?.totalCount ?: 0

        val repos = if (totalCount > githubMaxCountOfCacheableItems) {
            githubRepos?.items ?: listOf()
        } else {
            collectAllReposByUser(totalCount, qParam)
        }
        return GithubRepositoriesCache(
            userName = user,
            totalCount = totalCount,
            repositories = repos
        )
    }

    private fun collectAllReposByUser(totalCount: Int, q: String): List<GithubRepo> {
        val queryParams = LinkedMultiValueMap<String, String>()

        var page = 0
        val repos = mutableListOf<GithubRepo>()
        while (totalCount > githubDefaultPageSize * page) {
            page++

            queryParams.clear()
            queryParams.add("q", q)
            queryParams.add("per_page", githubDefaultPageSize.toString())
            queryParams.add("page", page.toString())

            repos.addAll(retrieveRepositoriesByWebClient(queryParams)?.items ?: listOf())
        }
        return repos
    }

    override fun retrieveRepositoryByName(repositoryName: String, ownerName: String): GithubRepo? {
        val pathParams = arrayOf(ownerName, repositoryName)
        return retrieveSingleRepositoryByWebClient(pathParams = pathParams)
    }

    private fun retrieveRepositoriesByWebClient(queryParams: MultiValueMap<String, String>): GithubRepos? {
        val responseSpec = this.githubWebClient.retrieveRequest(
            pathUri = GithubUri.GET_REPOSITORIES_URI,
            queryParams = queryParams
        )
        return responseSpec.bodyToMono(GithubRepos::class.java)
            .block(Duration.ofMillis(githubDurationOfSingleExecutionMillis))
    }

    private fun buildQParam(user: String, withForks: Boolean): String {
        return "user:$user+fork:$withForks"
    }

    private fun retrieveSingleRepositoryByWebClient(pathParams: Array<String>): GithubRepo? {
        val responseSpec = this.githubWebClient.retrieveRequest(
            pathUri = GithubUri.GET_SINGLE_REPOSITORY_URI,
            pathParams = pathParams
        )
        return responseSpec.bodyToMono(GithubRepo::class.java)
            .block(Duration.ofMillis(githubDurationOfSingleExecutionMillis))
    }
}
