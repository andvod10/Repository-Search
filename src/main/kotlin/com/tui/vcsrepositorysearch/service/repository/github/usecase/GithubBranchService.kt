package com.tui.vcsrepositorysearch.service.repository.github.usecase

import com.tui.vcsrepositorysearch.data.entity.github.GithubBranch
import com.tui.vcsrepositorysearch.data.entity.github.GithubBranchesCache
import com.tui.vcsrepositorysearch.data.entity.github.GithubRepo
import com.tui.vcsrepositorysearch.service.repository.github.GithubUri
import com.tui.vcsrepositorysearch.service.repository.github.GithubWebClient
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import reactor.core.scheduler.Schedulers

interface GithubBranchService {
    fun retrieveBranchFromGitHubByRepository(repository: GithubRepo): GithubBranchesCache
}

@Service
class GithubBranchServiceImpl constructor(
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

        val githubBranchesCache = GithubBranchesCache(
            repositoryName = repository.name,
            branches = githubBranches
        )
        log.debug("Direct call to Github Branch performed.")
        return githubBranchesCache
    }

    //Retrieving branches for each repository concurrently
    //Should be tried integrate webclient with coroutine or can be used non-blocking stream
    private fun retrieveBranchesByWebClient(pathParams: Array<String>): List<GithubBranch> {
        val responseSpec = this.githubWebClient.retrieveRequest(
            pathUri = GithubUri.GET_BRANCHES_URI,
            pathParams = pathParams
        )
        val branches = mutableListOf<GithubBranch>()
        responseSpec.bodyToFlux(GithubBranch::class.java)
            .parallel()
            .runOn(Schedulers.boundedElastic())
            .doOnNext(branches::add)
            .sequential()
            .blockLast()

        return branches.toList()
    }
}
