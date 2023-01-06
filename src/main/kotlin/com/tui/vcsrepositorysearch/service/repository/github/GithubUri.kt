package com.tui.vcsrepositorysearch.service.repository.github

object GithubUri {
    const val GET_REPOSITORIES_URI = "/search/repositories"
    const val GET_SINGLE_REPOSITORY_URI = "/repos/{userName}/{repoName}"
    const val GET_BRANCHES_URI = "/repos/{userName}/{repoName}/branches"
}
