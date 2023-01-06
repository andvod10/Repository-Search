package com.tui.vcsrepositorysearch.data.mappers

import com.tui.vcsrepositorysearch.application.dto.RsBranch
import com.tui.vcsrepositorysearch.application.dto.RsRepository
import com.tui.vcsrepositorysearch.data.entity.GithubBranchesCache
import com.tui.vcsrepositorysearch.data.entity.GithubBranch
import com.tui.vcsrepositorysearch.data.entity.GithubRepo

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
