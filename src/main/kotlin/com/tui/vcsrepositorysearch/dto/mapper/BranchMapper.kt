package com.tui.vcsrepositorysearch.dto.mapper

import com.tui.vcsrepositorysearch.dto.BranchDto
import com.tui.vcsrepositorysearch.model.Branch
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

@Mapper
interface BranchMapper {
    @Mapping(source = "name", target = "name")
    @Mapping(source = "commit.sha", target = "lastCommitSha")
    fun branchToBranchDto(branch: Branch): BranchDto

    companion object {
        val INSTANCE: BranchMapper = Mappers.getMapper(BranchMapper::class.java)
    }
}
