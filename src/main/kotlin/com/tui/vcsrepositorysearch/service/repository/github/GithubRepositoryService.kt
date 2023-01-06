package com.tui.vcsrepositorysearch.service.repository.github;

import com.tui.vcsrepositorysearch.data.entity.GithubRepo
import com.tui.vcsrepositorysearch.data.entity.GithubRepos
import com.tui.vcsrepositorysearch.data.entity.GithubRepositoriesCache
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
        pageable: Pageable? = null,
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
    private val githubWebClient: GithubWebClient
) : GithubRepositoryService {
    private val log = LoggerFactory.getLogger(GithubRepositoryServiceImpl::class.java)

    @Cacheable(
        value = ["githubRepository"],
        key = "#user"
    )
    override fun retrieveRepositoryFromGitHubByUser(
        user: String,
        pageable: Pageable?,
        withForks: Boolean
    ): GithubRepositoriesCache {
        log.debug("Direct call to Github Repository performing...")
        val perPage = pageable?.pageSize ?: githubDefaultPageSize
        val page = pageable?.pageNumber ?: 0

        val queryParams = LinkedMultiValueMap<String, String>()
        queryParams.add("q", buildQParam(user, withForks))
        queryParams.add("per_page", perPage.toString())
        queryParams.add("page", page.toString())

        val githubRepos = retrieveRepositoriesByWebClient(queryParams)

        return GithubRepositoriesCache(
            userName = user,
            repositories = githubRepos?.items ?: listOf()
        )
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
