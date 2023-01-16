package com.tui.vcsrepositorysearch.service

import com.tui.vcsrepositorysearch.model.Branch
import com.tui.vcsrepositorysearch.model.Commit
import com.tui.vcsrepositorysearch.model.Owner
import com.tui.vcsrepositorysearch.model.Repo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable

@SpringBootTest
@AutoConfigureWebTestClient
class RepositoryServiceTest {
    @InjectMocks
    private lateinit var repositoryService: RepositoryService

    @Mock
    private lateinit var searchBranchService: SearchBranchService

    @Mock
    private lateinit var searchRepositoryService: SearchRepositoryService

    @Test
    fun testCacheSuccessful() {
        val branch = Branch(
            name = "master",
            commit = Commit(
                sha = "sha",
                url = "url"
            )
        )
        val repo = Repo(
            name = "docker-frontend",
            owner = Owner(
                login = "login",
                url = "url"
            )
        )
        val pageable = Pageable.ofSize(1)
        Mockito.`when`(this.searchBranchService.getBranches(repo))
            .thenReturn(listOf(branch))
        Mockito.`when`(
            this.searchRepositoryService.getRepositoriesByOwner(
                ownerName = "login",
                withForks = false,
                pageable = pageable
            )
        ).thenReturn(listOf(repo) to 1)

        val repos = this.repositoryService.getRepositoriesByOwner(
            ownerName = "login",
            withForks = false,
            pageable = pageable
        )

        Mockito.verify(searchBranchService).getBranches(repo)
        Mockito.verify(searchRepositoryService)
            .getRepositoriesByOwner(
                ownerName = "login",
                withForks = false,
                pageable = pageable
            )

        assertThat(repos.totalElements).isEqualTo(1)
        assertThat(repos.size).isEqualTo(1)
        assertThat(repos.content.size).isEqualTo(1)
        assertThat(repos.content.first().name).isEqualTo("docker-frontend")
        assertThat(repos.content.first().ownerLogin).isEqualTo("login")
        assertThat(repos.content.first().branches.size).isEqualTo(1)
        assertThat(repos.content.first().branches.first().name).isEqualTo("master")
        assertThat(repos.content.first().branches.first().lastCommitSha).isEqualTo("sha")
    }
}
