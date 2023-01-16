package com.tui.vcsrepositorysearch.service.github.exception

class TooMuchSortPropertiesException(sortProperties: Int) :
    RuntimeException("Only one sort property allowed for GitHub. Provided $sortProperties sorts.")
