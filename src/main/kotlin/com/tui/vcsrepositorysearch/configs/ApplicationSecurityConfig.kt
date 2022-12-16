package com.tui.vcsrepositorysearch.configs

import com.tui.vcsrepositorysearch.application.APIVersions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.filter.CorsFilter

private val SWAGGER_REQUESTS = arrayOf(
    "/",
    "/v2/api-docs",
    "/swagger-resources",
    "/swagger-resources/**",
    "/configuration/ui",
    "/configuration/security",
    "/swagger-ui/",
    "/swagger-ui/**",
    "/webjars/**"
)

private val AUTH_REQUESTS = SWAGGER_REQUESTS + arrayOf(
    "/",
    "/${APIVersions.API}/${APIVersions.V1}/repositories/**"
)

@Configuration
@EnableWebSecurity
class ApplicationSecurityConfig constructor(
    private val corsFilter: CorsFilter
) {
    @Bean
    fun configure(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf().disable()
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests()
            .antMatchers(*AUTH_REQUESTS).permitAll()
            .anyRequest().authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        return http.build()
    }
}
