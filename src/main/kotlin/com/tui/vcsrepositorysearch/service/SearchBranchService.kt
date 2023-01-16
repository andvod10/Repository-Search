package com.tui.vcsrepositorysearch.service;

import com.tui.vcsrepositorysearch.model.Branch
import com.tui.vcsrepositorysearch.model.Repo

interface SearchBranchService {
    fun getBranches(repository: Repo): List<Branch>
}
