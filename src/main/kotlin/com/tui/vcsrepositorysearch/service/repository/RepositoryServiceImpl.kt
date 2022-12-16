package com.tui.vcsrepositorysearch.service.repository

import com.tui.vcsrepositorysearch.application.dto.RsBranch
import com.tui.vcsrepositorysearch.application.dto.RsRepository
import com.tui.vcsrepositorysearch.data.mappers.toResponse
import com.tui.vcsrepositorysearch.service.exception.EntityNotFoundException
import com.tui.vcsrepositorysearch.service.repository.github.GithubBranchService
import com.tui.vcsrepositorysearch.service.repository.github.GithubRepositoryService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class RepositoryServiceImpl constructor(
    private val githubRepositoryService: GithubRepositoryService,
    private val githubBranchService: GithubBranchService
) : RepositoryService {
    override fun getRepositoriesByOwnerWithNotForks(
        ownerName: String,
        pageable: Pageable
    ): Page<RsRepository> {
        val repos = this.githubRepositoryService.retrieveRepositoryFromGitHubByUser(
            user = ownerName
        ).repositories
        val chunk = repos.chunked(pageable.pageSize)[pageable.pageNumber]
        val sortedChunk = if (pageable.sort.getOrderFor(RsRepository::name.name)?.isAscending == true) {
            chunk.sortedBy { it.name }
        } else {
            chunk.sortedByDescending { it.name }
        }

        return PageImpl(sortedChunk.parallelStream()
            .map { it.toResponse(githubBranchService.retrieveBranchFromGitHubByRepository(it)) }
            .toList(), pageable, repos.size.toLong())
    }

    override fun getRepositoriesByName(repositoryName: String, ownerName: String): RsRepository {
        val repository = this.githubRepositoryService.retrieveRepositoryByName(repositoryName, ownerName)
        return if (repository != null) {
            val branches = githubBranchService.retrieveBranchFromGitHubByRepository(repository)
            repository.toResponse(branches)
        } else {
            throw EntityNotFoundException(repositoryName)
        }
    }

    override fun getRepositoryBranches(repositoryName: String, ownerName: String): List<RsBranch> {
        val repo = this.githubRepositoryService.retrieveRepositoryFromGitHubByUser(
            user = ownerName
        ).repositories
            .firstOrNull { it.name == repositoryName }

        return if (repo == null) {
            listOf()
        } else {
            githubBranchService.retrieveBranchFromGitHubByRepository(repo).branches
                .map { it.toResponse() }
        }
    }
}
