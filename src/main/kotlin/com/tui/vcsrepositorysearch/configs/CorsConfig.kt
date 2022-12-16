package com.tui.vcsrepositorysearch.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

import java.util.Collections;

@Configuration
class CorsConfig {
    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.allowedOrigins = Collections.singletonList("http://localhost:5000")
        config.allowedMethods = listOf("GET","POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        config.exposedHeaders = listOf("Authorization", "content-type")
        config.allowedHeaders = listOf("Authorization", "content-type")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }
}
