package com.fmd.spring_jpa_demo.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the application. Sets up JWT authentication filter and stateless session management.
 */
@Slf4j
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * Configures the security filter chain, disables CSRF, sets stateless session, and adds JWT filter.
     *
     * @param http the HttpSecurity to modify
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.debug("Configuring security filter chain");

        // Disable CSRF protection as JWT is used
        http.csrf(AbstractHttpConfigurer::disable);

        // Set session management to stateless (no HTTP session)
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Configure custom authentication entry point for handling authentication/access denied errors
        http.exceptionHandling(exception -> {
            exception.authenticationEntryPoint(customAuthenticationEntryPoint);
            exception.accessDeniedHandler(customAccessDeniedHandler);
        });

        // Configure authorization rules for HTTP requests
        http.authorizeHttpRequests(auth -> {
            // Allow unauthenticated access to /auth/** endpoints
            auth.requestMatchers("/auth/**").permitAll();
            auth.requestMatchers("/actuator/**").permitAll();
            auth.requestMatchers("/h2-console/**").permitAll();
            // Allow all OPTIONS requests (CORS preflight)
            auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
            // Require authentication for all other requests
            auth.anyRequest().authenticated();
        });

        // Add the JWT authentication filter before the default UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // Build and return the configured SecurityFilterChain
        return http.build();
    }

    /**
     * Provides the AuthenticationManager bean.
     *
     * @param config the AuthenticationConfiguration
     * @return the AuthenticationManager
     * @throws Exception if retrieval fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        log.debug("Providing AuthenticationManager bean");
        // Return the AuthenticationManager from the configuration
        return config.getAuthenticationManager();
    }
}
