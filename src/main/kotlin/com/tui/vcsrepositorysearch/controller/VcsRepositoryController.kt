package com.tui.vcsrepositorysearch.controller

import com.tui.vcsrepositorysearch.APIVersion
import com.tui.vcsrepositorysearch.dto.ErrorResponse
import com.tui.vcsrepositorysearch.dto.RepositoryDto
import com.tui.vcsrepositorysearch.dto.mapper.RepositoryPageableModelAssembler
import com.tui.vcsrepositorysearch.dto.RepositoryPageableDtoModel
import com.tui.vcsrepositorysearch.service.RepositoryService
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
                            ArraySchema(schema = Schema(implementation = ErrorResponse::class)))
                ))]
        ),
        ApiResponse(
            responseCode = "404", description = "Any repository with ownerName not found.", content = [
                (Content(
                    mediaType = "application/json", array = (
                            ArraySchema(schema = Schema(implementation = ErrorResponse::class)))
                ))]
        ),
        ApiResponse(
            responseCode = "406",
            description = "Only application/json media type allowed for Response Allow header.",
            content = [
                (Content(
                    mediaType = "application/json", array = (
                            ArraySchema(schema = Schema(implementation = ErrorResponse::class)))
                ))]
        ),
        ApiResponse(
            responseCode = "5XX", description = "Unexpected error. Some problems with connection.", content = [
                (Content(
                    mediaType = "application/json", array = (
                            ArraySchema(schema = Schema(implementation = ErrorResponse::class)))
                ))]
        )]
)
@RestController
@RequestMapping("${APIVersion.API}/${APIVersion.V1}/owner")
class VcsRepositoryController constructor(
    private val repositoryService: RepositoryService,
    private val repositoryPageableModelAssembler: RepositoryPageableModelAssembler,
    private val pagedResourcesAssembler: PagedResourcesAssembler<RepositoryDto>
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
                                ArraySchema(schema = Schema(implementation = RepositoryPageableDtoModel::class)))
                    ))]
            )]
    )
    @GetMapping(
        "{ownerName}/repositories",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getRepositoryByOwnerName(
        @PathVariable("ownerName") ownerName: String,
        @ParameterObject pageable: Pageable
    ): PagedModel<RepositoryPageableDtoModel> {
        val allRepositories = repositoryService.getRepositoriesByOwner(
            ownerName = ownerName,
            pageable = pageable
        )
        return pagedResourcesAssembler.toModel(allRepositories, repositoryPageableModelAssembler)
    }
}
