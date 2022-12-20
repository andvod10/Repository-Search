package com.tui.vcsrepositorysearch.service.repository

import com.tui.vcsrepositorysearch.application.dto.RsBranch
import com.tui.vcsrepositorysearch.application.dto.RsRepository
import com.tui.vcsrepositorysearch.data.mappers.toResponse
import com.tui.vcsrepositorysearch.service.exception.EntityNotFoundException
import com.tui.vcsrepositorysearch.service.exception.EntityRangeNotFoundException
import com.tui.vcsrepositorysearch.service.repository.github.GithubBranchService
import com.tui.vcsrepositorysearch.service.repository.github.GithubRepositoryService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.kohsuke.github.GHRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import kotlin.system.measureTimeMillis

@Service
class RepositoryServiceImpl constructor(
    private val githubRepositoryService: GithubRepositoryService,
    private val githubBranchService: GithubBranchService
) : RepositoryService {
    private val log = LoggerFactory.getLogger(RepositoryServiceImpl::class.java)

    override fun getRepositoriesByOwnerWithNotForks(
        ownerName: String,
        pageable: Pageable
    ): Page<RsRepository> {
        val repos = runBlocking {
            loadRepositoriesConcurrent(ownerName, pageable)
        }
        return PageImpl(repos, pageable, repos.size.toLong())
    }

    private suspend fun loadRepositoriesConcurrent(
        ownerName: String,
        pageable: Pageable
    ): List<RsRepository> = coroutineScope {
        val repos = githubRepositoryService.retrieveRepositoryFromGitHubByUser(
            user = ownerName
        ).repositories

        val chunk = getChunkOfList(list = repos, pageable = pageable)
        val sortedChunk = sortListByPageable(list = chunk, pageable = pageable)

        val result: List<RsRepository>
        val millis = measureTimeMillis {
            val deferreds: List<Deferred<RsRepository>> = sortedChunk.map { repo ->
                async(Dispatchers.Default) {
                    repo.toResponse(githubBranchService.retrieveBranchFromGitHubByRepository(repo))
                }
            }
            result = deferreds.awaitAll()
        }
        log.debug("Time spent on retrieving branches $millis")
        return@coroutineScope result
    }

    private fun loadRepositoriesBlocking(
        ownerName: String,
        pageable: Pageable
    ): List<RsRepository> {
        val repos = this.githubRepositoryService.retrieveRepositoryFromGitHubByUser(
            user = ownerName
        ).repositories
        val chunk = getChunkOfList(list = repos, pageable = pageable)
        val sortedChunk = sortListByPageable(list = chunk, pageable = pageable)

        var result: List<RsRepository>
        val millis = measureTimeMillis {
            result = sortedChunk.parallelStream()
                .map { it.toResponse(githubBranchService.retrieveBranchFromGitHubByRepository(it)) }
                .toList()
        }
        log.debug("Time spent on retrieving branches $millis")

        return result
    }

    private fun getChunkOfList(list: List<GHRepository>, pageable: Pageable): List<GHRepository> {
        return try {
            list.chunked(pageable.pageSize)[pageable.pageNumber]
        } catch (ex: IndexOutOfBoundsException) {
            throw EntityRangeNotFoundException(list.size)
        }
    }

    private fun sortListByPageable(list: List<GHRepository>, pageable: Pageable): List<GHRepository> {
        return if (pageable.sort.getOrderFor(RsRepository::name.name)?.isAscending == true) {
            list.sortedBy { it.name }
        } else {
            list.sortedByDescending { it.name }
        }
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
