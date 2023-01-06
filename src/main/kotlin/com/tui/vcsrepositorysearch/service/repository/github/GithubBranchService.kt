package com.tui.vcsrepositorysearch.service.repository.github;

import com.tui.vcsrepositorysearch.data.entity.GithubBranchesCache
import com.tui.vcsrepositorysearch.data.entity.GithubBranch
import com.tui.vcsrepositorysearch.data.entity.GithubRepo
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.Optional

interface GithubBranchService {
    fun retrieveBranchFromGitHubByRepository(repository: GithubRepo): GithubBranchesCache
}

@Service
class GithubBranchServiceImpl constructor(
    @Value("\${github.duration-of-single-execution-millis}")
    private val githubDurationOfSingleExecutionMillis: Long,
    private val githubWebClient: GithubWebClient
): GithubBranchService {
    private val log = LoggerFactory.getLogger(GithubBranchServiceImpl::class.java)

    @Cacheable(
        value = ["githubBranch"],
        key = "#repository.name"
    )
    override fun retrieveBranchFromGitHubByRepository(repository: GithubRepo): GithubBranchesCache {
        log.debug("Direct call to Github Branch performing...")
        val pathParams = arrayOf(repository.owner.login, repository.name)
        val githubBranches = retrieveBranchesByWebClient(pathParams = pathParams)
            .orElse(arrayOf()).toList()

        val githubBranch = GithubBranchesCache(
            repositoryName = repository.name,
            branches = githubBranches
        )
        log.debug("Direct call to Github Branch performed.")
        return githubBranch
    }

    private fun retrieveBranchesByWebClient(pathParams: Array<String>): Optional<Array<GithubBranch>> {
        val responseSpec = this.githubWebClient.retrieveRequest(
            pathUri = GithubUri.GET_BRANCHES_URI,
            pathParams = pathParams
        )
        return responseSpec.bodyToMono(Array<GithubBranch>::class.java)
            .blockOptional(Duration.ofMillis(githubDurationOfSingleExecutionMillis))
    }
}
