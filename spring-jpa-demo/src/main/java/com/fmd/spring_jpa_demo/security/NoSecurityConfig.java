package com.fmd.spring_jpa_demo.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration that disables all security when security.enabled is false.
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "com.fmd.spring-jpa-demo.security", name = "enabled", havingValue = "false", matchIfMissing = true)
public class NoSecurityConfig {

    /**
     * Configures the security filter chain to permit all requests and disable CSRF protection.
     * <p>
     * This configuration is used when security is disabled in the application.
     *
     * @param http the HttpSecurity to modify
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain permitAllSecurityFilterChain(HttpSecurity http) throws Exception {
        log.debug("Configuring NoSecurityConfig as security is disabled");
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        // Allow frames for H2 console
        http.headers(headers ->
                headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
        );
        return http.build();
    }

    /**
     * Provides the AuthenticationManager bean.
     * <p>
     * This bean is required for authentication-related operations in the application.
     * In this configuration, it simply delegates to the default AuthenticationManager
     * provided by Spring Security's AuthenticationConfiguration.
     *
     * @param config the AuthenticationConfiguration instance provided by Spring
     * @return the AuthenticationManager instance
     * @throws Exception if retrieval of the AuthenticationManager fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        log.debug("Providing AuthenticationManager bean");
        // Return the AuthenticationManager from the configuration
        return config.getAuthenticationManager();
    }
}
