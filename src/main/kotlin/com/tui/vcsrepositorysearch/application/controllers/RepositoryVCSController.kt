package com.tui.vcsrepositorysearch.application.controllers

import com.tui.vcsrepositorysearch.application.APIVersions
import com.tui.vcsrepositorysearch.application.dto.RsBranch
import com.tui.vcsrepositorysearch.application.dto.RsRepository
import com.tui.vcsrepositorysearch.application.modelmappers.RepositoryModelAssembler
import com.tui.vcsrepositorysearch.application.models.RsRepositoryModel
import com.tui.vcsrepositorysearch.service.repository.RepositoryService
import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.MediaType
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler

@RestController
@RequestMapping("${APIVersions.API}/${APIVersions.V1}/repositories")
class RepositoryVCSController constructor(
    private val repositoryService: RepositoryService,
    private val repositoryModelAssembler: RepositoryModelAssembler,
    private val pagedResourcesAssembler: PagedResourcesAssembler<RsRepository>
) {
    @GetMapping(
        "ownerName/{ownerName}",
//            consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getRepositoryByOwnerName(
        @PathVariable("ownerName") ownerName: String,
        pageable: Pageable
    ): PagedModel<RsRepositoryModel> {
        val allRepositories = repositoryService.getRepositoriesByOwnerWithNotForks(
            ownerName = ownerName,
            pageable = pageable
        )
        return pagedResourcesAssembler.toModel(allRepositories, repositoryModelAssembler)
    }

    @GetMapping(
        "{repositoryName}/ownerName/{ownerName}",
//            consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getRepository(
        @PathVariable("repositoryName") repositoryName: String,
        @PathVariable("ownerName") ownerName: String
    ): RsRepository {
        return repositoryService.getRepositoriesByName(repositoryName, ownerName)
    }

    @GetMapping(
        "{repositoryName}/ownerName/{ownerName}/branches",
//            consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getRepositoryBranches(
        @PathVariable("repositoryName") repositoryName: String,
        @PathVariable("ownerName") ownerName: String
    ): List<RsBranch> {
        return repositoryService.getRepositoryBranches(repositoryName, ownerName)
    }
}
