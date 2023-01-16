package com.tui.vcsrepositorysearch.dto.mapper

import com.tui.vcsrepositorysearch.dto.BranchDto
import com.tui.vcsrepositorysearch.dto.RepositoryDto
import com.tui.vcsrepositorysearch.model.Branch
import com.tui.vcsrepositorysearch.model.Commit
import com.tui.vcsrepositorysearch.model.Owner
import com.tui.vcsrepositorysearch.model.Repo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers

class RepositoryMapperTest {
    @Test
    fun testConvertRepoToRepositoryDto() {
        //given
        val repo = Repo(
            name = "docker-complex",
            owner = Owner(
                login = "login",
                url = "url"
            )
        )
        val branches = listOf(
            BranchDto(
                name = "master",
                lastCommitSha = "sha"
            )
        )
        //when
        val repositoryDto: RepositoryDto = RepositoryMapper.INSTANCE.repoToRepositoryDto(repo, branches)
        //then
        assertThat(repositoryDto).isNotNull
        assertThat(repositoryDto.name).isEqualTo(repo.name)
        assertThat(repositoryDto.ownerLogin).isEqualTo(repo.owner.login)
        assertThat(repositoryDto.branches).isEqualTo(branches)
    }

    @Test
    fun testConvertBranchToBranchDto() {
        //given
        val branch = Branch(
            name = "master",
            commit = Commit(
                sha = "sha",
                url = "url"
            )
        )
        //when
        val branchDto: BranchDto = BranchMapper.INSTANCE.branchToBranchDto(branch)
        //then
        assertThat(branchDto).isNotNull
        assertThat(branchDto.name).isEqualTo(branch.name)
        assertThat(branchDto.lastCommitSha).isEqualTo(branch.commit.sha)
    }
}
