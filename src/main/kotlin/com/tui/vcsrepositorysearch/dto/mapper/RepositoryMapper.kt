package com.tui.vcsrepositorysearch.dto.mapper

import com.tui.vcsrepositorysearch.dto.BranchDto
import com.tui.vcsrepositorysearch.dto.RepositoryDto
import com.tui.vcsrepositorysearch.model.Repo
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

@Mapper
interface RepositoryMapper {
    @Mapping(source = "repo.name", target = "name")
    @Mapping(source = "repo.owner.login", target = "ownerLogin")
    @Mapping(source = "branches", target = "branches")
    fun repoToRepositoryDto(repo: Repo, branches: List<BranchDto>): RepositoryDto

    companion object {
        val INSTANCE: RepositoryMapper = Mappers.getMapper(RepositoryMapper::class.java)
    }
}
