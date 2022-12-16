package com.tui.vcsrepositorysearch.application.modelmappers

import com.tui.vcsrepositorysearch.application.controllers.RepositoryVCSController
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import com.tui.vcsrepositorysearch.application.dto.RsRepository
import com.tui.vcsrepositorysearch.application.models.RsRepositoryModel
import org.springframework.beans.BeanUtils
import org.springframework.stereotype.Component

import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn

@Component
class RepositoryModelAssembler : RepresentationModelAssemblerSupport<RsRepository, RsRepositoryModel>(
    RsRepository::class.java, RsRepositoryModel::class.java
) {
    override fun toModel(entity: RsRepository): RsRepositoryModel {
        val model = RsRepositoryModel()
        BeanUtils.copyProperties(entity, model)

        val selfLink = linkTo(methodOn(RepositoryVCSController::class.java).getRepository(model.name, model.ownerLogin))
            .withSelfRel()
        model.add(selfLink)

        val branchesLink = linkTo(methodOn(RepositoryVCSController::class.java).getRepositoryBranches(model.name, model.ownerLogin))
            .withRel("branches")
        model.add(branchesLink)

        return model
    }
}
