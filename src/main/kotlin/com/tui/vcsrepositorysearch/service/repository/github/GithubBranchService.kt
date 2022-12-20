package com.tui.vcsrepositorysearch.service.repository.github;

import com.tui.vcsrepositorysearch.data.entity.GithubBranch
import org.kohsuke.github.GHRepository
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

interface GithubBranchService {
    fun retrieveBranchFromGitHubByRepository(repository: GHRepository): GithubBranch
}

@Service
class GithubBranchServiceImpl: GithubBranchService {
    private val log = LoggerFactory.getLogger(GithubBranchServiceImpl::class.java)

    @Cacheable(
        value = ["githubBranch"],
        key = "#repository.name"
    )
    override fun retrieveBranchFromGitHubByRepository(repository: GHRepository): GithubBranch {
        log.debug("Direct call to Github Branch performing...")
        val githubBranch = GithubBranch(
            repositoryName = repository.name,
            branches = repository.branches.values.toList()
        )
        log.debug("Direct call to Github Branch performed.")
        return githubBranch
    }
}
