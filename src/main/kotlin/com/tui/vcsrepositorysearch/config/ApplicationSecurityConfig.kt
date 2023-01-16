package com.tui.vcsrepositorysearch.config

import com.tui.vcsrepositorysearch.APIVersion
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

private val SWAGGER_REQUESTS = arrayOf(
    "/v3/api-docs",
    "/v3/api-docs/**",
    "/v3/api-docs.yaml",
    "/swagger-resources",
    "/swagger-resources/**",
    "/configuration/ui",
    "/configuration/security",
    "/swagger-ui/",
    "/swagger-ui.html",
    "/swagger-ui/**",
    "/repository-search.yaml",
    "/webjars/**"
)

private val AUTH_REQUESTS = SWAGGER_REQUESTS + arrayOf(
    "/",
    "/profile",
    "/actuator/**",
    "/${APIVersion.API}/${APIVersion.V1}/owner/**"
)

@Configuration
@EnableWebSecurity
class ApplicationSecurityConfig {
    @Bean
    fun configure(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf().disable()
            .authorizeHttpRequests()
            .antMatchers(*AUTH_REQUESTS).permitAll()
            .anyRequest().authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        return http.build()
    }
}
