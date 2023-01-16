package com.tui.vcsrepositorysearch.service.github

import com.tui.vcsrepositorysearch.model.Branch
import com.tui.vcsrepositorysearch.model.Repo
import com.tui.vcsrepositorysearch.service.SearchBranchService
import com.tui.vcsrepositorysearch.service.CustomWebClient
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import reactor.core.scheduler.Schedulers

@Service
class GithubSearchBranchService constructor(
    private val webClient: CustomWebClient
): SearchBranchService {
    private val log = LoggerFactory.getLogger(GithubSearchBranchService::class.java)

    @Cacheable(
        value = ["githubBranch"],
        key = "#repository.name"
    )
    override fun getBranches(repository: Repo): List<Branch> {
        log.debug("Direct call to Github Branch performing...")
        val pathParams = arrayOf(repository.owner.login, repository.name)
        val githubBranches = retrieveBranchesByWebClient(pathParams = pathParams)

        log.debug("Direct call to Github Branch performed.")
        return githubBranches
    }

    //Retrieving branches for each repository concurrently
    //Should be tried integrate webclient with coroutine or can be used non-blocking stream
    private fun retrieveBranchesByWebClient(pathParams: Array<String>): List<Branch> {
        val responseSpec = this.webClient.retrieveRequest(
            pathUri = GithubUri.GET_BRANCHES_URI,
            pathParams = pathParams
        )
        val branches = mutableListOf<Branch>()
        responseSpec.bodyToFlux(Branch::class.java)
            .parallel()
            .runOn(Schedulers.boundedElastic())
            .doOnNext(branches::add)
            .sequential()
            .blockLast()

        return branches.toList()
    }
}
