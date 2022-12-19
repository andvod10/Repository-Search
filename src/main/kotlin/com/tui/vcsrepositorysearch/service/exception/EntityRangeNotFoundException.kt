package com.tui.vcsrepositorysearch.service.exception

class EntityRangeNotFoundException(size: Int) : RuntimeException("Entities in range not found. Size = $size")
