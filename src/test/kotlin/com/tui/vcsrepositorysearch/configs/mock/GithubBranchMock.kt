package com.tui.vcsrepositorysearch.configs.mock

import com.tui.vcsrepositorysearch.data.entity.GithubBranch
import org.kohsuke.github.GHBranch
import org.kohsuke.github.GitHub
import org.springframework.util.ResourceUtils

class GithubBranchMock {
    fun getGithubBranchesByUserAndRepository(): GithubBranch {
        val branches = deserializeTestBranches()
        return GithubBranch(
            repositoryName = "docker-frontend",
            branches = branches
        )
    }

    private fun deserializeTestBranches(): List<GHBranch> {
        val file = ResourceUtils.getFile("classpath:mock_branches.json")
        return GitHub.getMappingObjectReader().readValue(file.readText(), Array<GHBranch>::class.java).toList()
    }
}
