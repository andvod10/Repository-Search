package com.tui.vcsrepositorysearch.service.repository

import com.tui.vcsrepositorysearch.application.dto.RsBranch
import com.tui.vcsrepositorysearch.application.dto.RsRepository
import com.tui.vcsrepositorysearch.data.entity.github.GithubRepo
import com.tui.vcsrepositorysearch.data.mappers.toResponse
import com.tui.vcsrepositorysearch.service.exception.EntityNotFoundException
import com.tui.vcsrepositorysearch.service.repository.github.usecase.GithubBranchService
import com.tui.vcsrepositorysearch.service.repository.github.usecase.GithubRepositoryService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.cache.CacheManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import kotlin.system.measureTimeMillis

@Service
class RepositoryServiceImpl constructor(
    private val githubRepositoryService: GithubRepositoryService,
    private val githubBranchService: GithubBranchService,
    private val cacheManager: CacheManager
) : RepositoryService {
    private val log = LoggerFactory.getLogger(RepositoryServiceImpl::class.java)

    override fun getRepositoriesByOwnerWithNotForks(
        ownerName: String,
        pageable: Pageable
    ): Page<RsRepository> {
        val repos = runBlocking {
            //Choose one of concurrency case:
            //loadRepositoriesBlocking (blocking), loadRepositoriesConcurrent (non-blocking)
            loadRepositoriesConcurrent(ownerName, pageable)
        }
        return PageImpl(repos, pageable, repos.size.toLong())
    }

    //Retrieving repositories by coroutine concurrency
    private suspend fun loadRepositoriesConcurrent(
        ownerName: String,
        pageable: Pageable
    ): List<RsRepository> = coroutineScope {
        val repos = githubRepositoryService.retrieveRepositoryFromGitHubByUser(
            user = ownerName,
            pageable = pageable
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

    //Retrieving repositories in blocking thread concurrency
    private fun loadRepositoriesBlocking(
        ownerName: String,
        pageable: Pageable
    ): List<RsRepository> {
        val repos = this.githubRepositoryService.retrieveRepositoryFromGitHubByUser(
            user = ownerName,
            pageable = pageable
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
        val repository = this.githubRepositoryService.retrieveRepositoryByName(repositoryName, ownerName)
        return if (repository != null) {
            githubBranchService.retrieveBranchFromGitHubByRepository(repository).branches
                .map { it.toResponse() }
        } else {
            throw EntityNotFoundException(repositoryName)
        }
    }

    private fun getChunkOfList(list: List<GithubRepo>, pageable: Pageable): List<GithubRepo> {
        return try {
            list.chunked(pageable.pageSize)[pageable.pageNumber]
        } catch (ex: IndexOutOfBoundsException) {
            listOf()
        }
    }

    private fun sortListByPageable(list: List<GithubRepo>, pageable: Pageable): List<GithubRepo> {
        return if (pageable.sort.getOrderFor(RsRepository::name.name)?.isAscending == true) {
            list.sortedBy { it.name }
        } else {
            list.sortedByDescending { it.name }
        }
    }
}
