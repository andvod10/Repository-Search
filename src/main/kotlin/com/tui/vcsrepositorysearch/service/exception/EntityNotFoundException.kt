package com.tui.vcsrepositorysearch.service.exception

class EntityNotFoundException(message: String) : RuntimeException("Entity $message not found.")
