package com.tui.vcsrepositorysearch.service

import com.tui.vcsrepositorysearch.model.Repo
import org.springframework.data.domain.Pageable

interface SearchRepositoryService {
    fun getRepositoriesByOwner(
        ownerName: String,
        withForks: Boolean = false,
        pageable: Pageable
    ): Pair <List<Repo>, Long>
}
