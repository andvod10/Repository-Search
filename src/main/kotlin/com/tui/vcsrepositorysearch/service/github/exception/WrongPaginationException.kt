package com.tui.vcsrepositorysearch.service.github.exception

class WrongPaginationException(size: Int, maxPerPageSize: Int) :
    RuntimeException("Incorrect size $size of one page. Provide page size in range 0-$maxPerPageSize.")
