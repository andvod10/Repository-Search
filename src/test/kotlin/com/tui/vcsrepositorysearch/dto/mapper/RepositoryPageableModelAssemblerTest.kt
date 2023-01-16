package com.tui.vcsrepositorysearch.dto.mapper

import com.tui.vcsrepositorysearch.dto.BranchDto
import com.tui.vcsrepositorysearch.dto.RepositoryDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler

@SpringBootTest
class RepositoryPageableModelAssemblerTest {
    @Autowired
    private lateinit var repositoryPageableModelAssembler: RepositoryPageableModelAssembler

    @Autowired
    private lateinit var pagedResourcesAssembler: PagedResourcesAssembler<RepositoryDto>

    @Test
    fun testConvertRepositoryDtoToRepositoryPageableDtoModel() {
        val branchDto = BranchDto(
            name = "master",
            lastCommitSha = "sha"
        )
        //given
        val repositoriesDto = listOf(
            RepositoryDto(
                name = "docker-complex",
                ownerLogin = "ownerLogin",
                branches = listOf(branchDto)
            ),
            RepositoryDto(
                name = "docker-frontend",
                ownerLogin = "ownerLogin",
                branches = listOf(branchDto)
            )
        )
        val pageable = Pageable.ofSize(2)
        val pagedRepositories = PageImpl(repositoriesDto, pageable, repositoriesDto.size.toLong())
        //when
        val pageAssembled = pagedResourcesAssembler.toModel(pagedRepositories, repositoryPageableModelAssembler)
        //then
        assertThat(pageAssembled).isNotNull
        assertThat(pageAssembled.metadata!!.size).isEqualTo(2)
        assertThat(pageAssembled.metadata!!.totalPages).isEqualTo(1)
        assertThat(pageAssembled.metadata!!.number).isEqualTo(0)
        assertThat(pageAssembled.content.size).isEqualTo(2)
        assertThat(pageAssembled.content.first().name).isEqualTo("docker-complex")
        assertThat(pageAssembled.content.first().ownerLogin).isEqualTo("ownerLogin")
        assertThat(pageAssembled.content.first().branches).isEqualTo(listOf(branchDto))
    }
}
