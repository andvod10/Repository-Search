package com.tui.vcsrepositorysearch.service

import com.tui.vcsrepositorysearch.dto.RepositoryDto
import com.tui.vcsrepositorysearch.dto.mapper.BranchMapper
import com.tui.vcsrepositorysearch.dto.mapper.RepositoryMapper
import com.tui.vcsrepositorysearch.model.Repo
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import kotlin.system.measureTimeMillis

@Service
class RepositoryService constructor(
    private val searchRepositoryService: SearchRepositoryService,
    private val searchBranchService: SearchBranchService,
) {
    private val log = LoggerFactory.getLogger(RepositoryService::class.java)

    fun getRepositoriesByOwner(
        ownerName: String,
        withForks: Boolean = false,
        pageable: Pageable
    ): Page<RepositoryDto> {
        val repos = runBlocking {
            loadRepositoriesConcurrent(ownerName, withForks, pageable)
        }
        return PageImpl(repos.first, pageable, repos.second)
    }

    //Retrieving repositories by coroutine concurrency
    private suspend fun loadRepositoriesConcurrent(
        ownerName: String,
        withForks: Boolean,
        pageable: Pageable
    ): Pair<List<RepositoryDto>, Long> = coroutineScope {
        var repos: Pair<List<Repo>, Long>
        val reposRetrievingMillis = measureTimeMillis {
            repos = searchRepositoryService.getRepositoriesByOwner(
                ownerName = ownerName,
                withForks = withForks,
                pageable = pageable
            )
        }
        log.debug("Time spent on retrieving repositories $reposRetrievingMillis")

        val branchMapper = BranchMapper.INSTANCE
        val repositoryMapper = RepositoryMapper.INSTANCE

        val result: List<RepositoryDto>
        val branchesRetrievingMillis = measureTimeMillis {
            val deferreds: List<Deferred<RepositoryDto>> = repos.first.map { repo ->
                async(Dispatchers.Default) {
                    val branches = searchBranchService.getBranches(repo).map { branchMapper.branchToBranchDto(it) }
                    repositoryMapper.repoToRepositoryDto(repo = repo, branches = branches)
                }
            }
            result = deferreds.awaitAll()
        }
        log.debug("Time spent on retrieving branches $branchesRetrievingMillis")
        return@coroutineScope Pair(result, repos.second)
    }
}
