package com.tui.vcsrepositorysearch.data.mappers

import com.tui.vcsrepositorysearch.application.dto.RsBranch
import com.tui.vcsrepositorysearch.application.dto.RsRepository
import com.tui.vcsrepositorysearch.data.entity.GithubBranch
import org.kohsuke.github.GHBranch
import org.kohsuke.github.GHRepository


fun GHRepository.toResponse(githubBranch: GithubBranch): RsRepository {
    return RsRepository(
        name = this.name,
        ownerLogin = this.owner.login,
        branches = githubBranch.branches.map { branch -> branch.toResponse() }
    )
}

fun GHBranch.toResponse(): RsBranch {
    return RsBranch(
        name = this.name,
        lastCommitSha = this.shA1
    )
}
