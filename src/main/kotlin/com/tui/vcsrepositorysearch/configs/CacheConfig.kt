package com.tui.vcsrepositorysearch.configs

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.RemovalCause
import com.github.benmanes.caffeine.cache.RemovalListener
import com.tui.vcsrepositorysearch.data.entity.GithubRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.cache.support.NoOpCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@EnableCaching
@Configuration
class CacheConfig constructor(
    @Value("\${repository.timeout-sec}")
    private val cacheTimeout: Long,
    @Value("\${repository.cache-size}")
    private val cacheSize: Long,
    @Value("\${repository.cache-enabled}")
    private val cacheEnabled: Boolean
) {
    private val log = LoggerFactory.getLogger(CacheConfig::class.java)

    @Bean
    fun cacheManager(): CacheManager {
        return object : ConcurrentMapCacheManager() {
            override fun createConcurrentMapCache(name: String): Cache {
                return if (cacheEnabled) {
                    createCacheWithExperience(name)
                } else {
                    NoOpCache(name)
                }
            }
        }
    }

    private fun createCacheWithExperience(name: String): Cache {
        return ConcurrentMapCache(
            name,
            Caffeine.newBuilder()
                .expireAfterWrite(cacheTimeout, TimeUnit.SECONDS)
                .removalListener(createRemovalListener())
                .maximumSize(cacheSize)
                .build<Any, Any>()
                .asMap(),
            true
        )
    }

    private fun createRemovalListener(): RemovalListener<Any, Any> {
        return RemovalListener<Any, Any> { key, value, cause ->
            if (cause == RemovalCause.EXPIRED || cause == RemovalCause.EXPLICIT) {
                when (value) {
                    is GithubRepository ->
                        log.debug(
                            inlineLogMessage(
                                "Cache entry with key $key of type ${value::class.java.name}" +
                                        " has been cleared by cause $cause"
                            )
                        )
                }
            }
        }
    }

    private fun inlineLogMessage(text: String): String {
        return text
    }
}
