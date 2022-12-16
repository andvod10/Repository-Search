package com.tui.vcsrepositorysearch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.web.config.EnableSpringDataWebSupport

@SpringBootApplication
class VcsRepositorySearchApplication

fun main(args: Array<String>) {
	runApplication<VcsRepositorySearchApplication>(*args)
}
