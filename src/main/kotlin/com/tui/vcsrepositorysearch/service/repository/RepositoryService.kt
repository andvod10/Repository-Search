package com.tui.vcsrepositorysearch.service.repository

import com.tui.vcsrepositorysearch.application.dto.RsBranch
import com.tui.vcsrepositorysearch.application.dto.RsRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface RepositoryService {
    fun getRepositoriesByOwnerWithNotForks(
        ownerName: String,
        pageable: Pageable
    ): Page<RsRepository>

    fun getRepositoriesByName(repositoryName: String, ownerName: String): RsRepository

    fun getRepositoryBranches(repositoryName: String, ownerName: String): List<RsBranch>
}
