package com.tui.vcsrepositorysearch.data.mappers

import com.tui.vcsrepositorysearch.application.dto.RsBranch
import com.tui.vcsrepositorysearch.application.dto.RsRepository
import com.tui.vcsrepositorysearch.data.entity.github.GithubBranchesCache
import com.tui.vcsrepositorysearch.data.entity.github.GithubBranch
import com.tui.vcsrepositorysearch.data.entity.github.GithubRepo

fun GithubRepo.toResponse(githubBranchesCache: GithubBranchesCache): RsRepository {
    return RsRepository(
        name = this.name,
        ownerLogin = this.owner.login,
        branches = githubBranchesCache.branches.map { branch -> branch.toResponse() }
    )
}

fun GithubBranch.toResponse(): RsBranch {
    return RsBranch(
        name = this.name,
        lastCommitSha = this.commit.sha
    )
}
