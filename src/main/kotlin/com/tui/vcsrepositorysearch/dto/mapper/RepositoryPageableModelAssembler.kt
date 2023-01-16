package com.tui.vcsrepositorysearch.dto.mapper

import com.tui.vcsrepositorysearch.dto.RepositoryDto
import com.tui.vcsrepositorysearch.dto.RepositoryPageableDtoModel
import org.springframework.beans.BeanUtils
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.stereotype.Component

@Component
class RepositoryPageableModelAssembler : RepresentationModelAssemblerSupport<RepositoryDto, RepositoryPageableDtoModel>(
    RepositoryDto::class.java, RepositoryPageableDtoModel::class.java
) {
    override fun toModel(entity: RepositoryDto): RepositoryPageableDtoModel {
        val model = RepositoryPageableDtoModel()
        BeanUtils.copyProperties(entity, model)

        return model
    }
}
