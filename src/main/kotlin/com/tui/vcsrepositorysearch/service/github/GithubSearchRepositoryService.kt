package com.tui.vcsrepositorysearch.service.github

import com.tui.vcsrepositorysearch.model.Repo
import com.tui.vcsrepositorysearch.model.Repos
import com.tui.vcsrepositorysearch.service.SearchRepositoryService
import com.tui.vcsrepositorysearch.service.CustomWebClient
import com.tui.vcsrepositorysearch.service.github.exception.TooMuchSortPropertiesException
import com.tui.vcsrepositorysearch.service.github.exception.WrongPaginationException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.time.Duration

@Service
class GithubSearchRepositoryService constructor(
    @Value("\${github.duration-of-single-execution-millis}")
    private val githubDurationOfSingleExecutionMillis: Long,
    @Value("\${github.max-count-of-cacheable-items}")
    val githubMaxCountOfCacheableItems: Long,
    private val webClient: CustomWebClient
) : SearchRepositoryService {
    private val log = LoggerFactory.getLogger(GithubSearchRepositoryService::class.java)

    companion object {
        //This is upper bound items per page set by GitHub
        //If any higher value will be provided, than perPage param will be treated as 100
        const val maxPerPageValue = 100
    }

    @Throws(
        WrongPaginationException::class,
        TooMuchSortPropertiesException::class,
    )
    @Cacheable(
        value = ["githubRepository"],
        key = "{ #ownerName, #pageable }",
        unless = "#result.first.size > #root.target.githubMaxCountOfCacheableItems"
    )
    override fun getRepositoriesByOwner(
        ownerName: String,
        withForks: Boolean,
        pageable: Pageable,
    ): Pair<List<Repo>, Long> {
        log.debug("Direct call to Github Repository performing...")

        if (pageable.pageSize > maxPerPageValue) {
            throw WrongPaginationException(pageable.pageSize, maxPerPageValue)
        }

        // Increment page number, because Spring Pageable starts from 0, GitHub pagination starts from 1
        val pageNumber = pageable.pageNumber + 1
        val qParam = buildQParam(ownerName, withForks)
        val sortParams = buildSortParams(pageable)
        val queryParams = LinkedMultiValueMap<String, String>()
        queryParams.add("q", qParam)
        queryParams.add("per_page", pageable.pageSize.toString())
        queryParams.add("page", pageNumber.toString())
        queryParams.addAll(sortParams)

        val result = retrieveRepositoriesByWebClient(queryParams)

        log.debug("Direct call to Github Repository performed.")
        return Pair(result?.items ?: emptyList(), result?.totalCount ?: 0)
    }

    private fun retrieveRepositoriesByWebClient(queryParams: MultiValueMap<String, String>): Repos? {
        val responseSpec = this.webClient.retrieveRequest(
            pathUri = GithubUri.GET_REPOSITORIES_URI,
            queryParams = queryParams
        )
        return responseSpec.bodyToMono(Repos::class.java)
            .block(Duration.ofMillis(githubDurationOfSingleExecutionMillis))
    }

    private fun buildQParam(user: String, withForks: Boolean): String {
        return "user:$user+fork:$withForks"
    }

    private fun buildSortParams(pageable: Pageable): LinkedMultiValueMap<String, String> {
        val sortProperties = pageable.sort.toList()

        //GitHub overwrites sort param if provided more then one
        if (sortProperties.size > 1) {
            throw TooMuchSortPropertiesException(pageable.sort.toList().size)
        }

        val result = LinkedMultiValueMap<String, String>()
        sortProperties.firstNotNullOfOrNull { sort ->
            result.add("sort", sort.property)
            result.add("order", sort.direction.name.lowercase())
        }

        return result
    }
}
