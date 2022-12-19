package com.tui.vcsrepositorysearch.application.controllers

import com.tui.vcsrepositorysearch.application.APIVersions
import com.tui.vcsrepositorysearch.application.dto.RsBranch
import com.tui.vcsrepositorysearch.application.dto.RsErrorResponse
import com.tui.vcsrepositorysearch.application.dto.RsRepository
import com.tui.vcsrepositorysearch.application.modelmappers.RepositoryModelAssembler
import com.tui.vcsrepositorysearch.application.models.RsRepositoryModel
import com.tui.vcsrepositorysearch.service.repository.RepositoryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springdoc.api.annotations.ParameterObject
import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.MediaType
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler

@ApiResponses(
    value = [
        ApiResponse(
            responseCode = "400", description = "Bad request. ownerName must exist.", content = [
                (Content(
                    mediaType = "application/json", array = (
                            ArraySchema(schema = Schema(implementation = RsErrorResponse::class)))
                ))]
        ),
        ApiResponse(
            responseCode = "404", description = "Any repository with ownerName not found.", content = [
                (Content(
                    mediaType = "application/json", array = (
                            ArraySchema(schema = Schema(implementation = RsErrorResponse::class)))
                ))]
        ),
        ApiResponse(
            responseCode = "406",
            description = "Only application/json media type allowed for Response Allow header.",
            content = [
                (Content(
                    mediaType = "application/json", array = (
                            ArraySchema(schema = Schema(implementation = RsErrorResponse::class)))
                ))]
        ),
        ApiResponse(
            responseCode = "5XX", description = "Unexpected error. Some problems with connection.", content = [
                (Content(
                    mediaType = "application/json", array = (
                            ArraySchema(schema = Schema(implementation = RsErrorResponse::class)))
                ))]
        )]
)
@RestController
@RequestMapping("${APIVersions.API}/${APIVersions.V1}/repositories")
class RepositoryVCSController constructor(
    private val repositoryService: RepositoryService,
    private val repositoryModelAssembler: RepositoryModelAssembler,
    private val pagedResourcesAssembler: PagedResourcesAssembler<RsRepository>
) {
    @Operation(
        summary = "Returns a list of users repository.",
        description = "Returns the list of repositories, that wasn't forked."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "A JSON array of repositories short info and latest commits of branch.",
                content = [
                    (Content(
                        mediaType = "application/json", array = (
                                ArraySchema(schema = Schema(implementation = RsRepositoryModel::class)))
                    ))]
            )]
    )
    @GetMapping(
        "owner/{ownerName}",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getRepositoryByOwnerName(
        @PathVariable("ownerName") ownerName: String,
        @ParameterObject pageable: Pageable
    ): PagedModel<RsRepositoryModel> {
        val allRepositories = repositoryService.getRepositoriesByOwnerWithNotForks(
            ownerName = ownerName,
            pageable = pageable
        )
        return pagedResourcesAssembler.toModel(allRepositories, repositoryModelAssembler)
    }

    @Operation(
        summary = "Returns a users repository with branches.",
        description = "Returns the repository, that wasn't forked."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "A JSON array of repository short info and latest commits of branch.",
                content = [
                    (Content(
                        mediaType = "application/json", array = (
                                ArraySchema(schema = Schema(implementation = RsRepository::class)))
                    ))]
            )]
    )
    @GetMapping(
        "{repositoryName}/owner/{ownerName}",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getRepository(
        @PathVariable("repositoryName") repositoryName: String,
        @PathVariable("ownerName") ownerName: String
    ): RsRepository {
        return repositoryService.getRepositoriesByName(repositoryName, ownerName)
    }

    @Operation(
        summary = "Returns branches of users repository.",
        description = "Returns the list of branches for repository, that wasn't forked."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "A JSON array of latest commits of branches.", content = [
                    (Content(
                        mediaType = "application/json", array = (
                                ArraySchema(schema = Schema(implementation = Array<RsBranch>::class)))
                    ))]
            )]
    )
    @GetMapping(
        "{repositoryName}/owner/{ownerName}/branches",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getRepositoryBranches(
        @PathVariable("repositoryName") repositoryName: String,
        @PathVariable("ownerName") ownerName: String
    ): List<RsBranch> {
        return repositoryService.getRepositoryBranches(repositoryName, ownerName)
    }
}
