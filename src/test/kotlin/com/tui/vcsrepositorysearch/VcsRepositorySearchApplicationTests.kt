package com.tui.vcsrepositorysearch

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(classes = [TestConfiguration::class])
class VcsRepositorySearchApplicationTests {

	@Test
	fun contextLoads() {
	}

}
