package com.tui.vcsrepositorysearch.configs.mock

import com.fasterxml.jackson.databind.ObjectMapper
import com.tui.vcsrepositorysearch.data.entity.GithubBranchesCache
import com.tui.vcsrepositorysearch.data.entity.GithubBranch
import org.springframework.util.ResourceUtils

class GithubBranchMock {
    private val objectMapper: ObjectMapper = ObjectMapper()

    fun getGithubBranchesByUserAndRepository(): GithubBranchesCache {
        val branches = deserializeTestBranches()
        return GithubBranchesCache(
            repositoryName = "docker-frontend",
            branches = branches
        )
    }

    private fun deserializeTestBranches(): List<GithubBranch> {
        val file = ResourceUtils.getFile("classpath:mock_branches.json")
        return objectMapper.readValue(file.readText(), Array<GithubBranch>::class.java).toList()
    }
}
