package com.fmd.spring_jpa_demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filter for JWT authentication. Extracts and validates JWT from the Authorization header,
 * and sets the authentication in the security context if valid.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Filters each request to check for a valid JWT token in the Authorization header.
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        log.debug("Processing JWT authentication filter");

        // Retrieve the Authorization header from the request
        String authHeader = request.getHeader("Authorization");

        // Check for null or empty Authorization header
        if (!StringUtils.hasText(authHeader)) {
            log.warn("Authorization header is null or empty");
            filterChain.doFilter(request, response);
            return;
        }

        // Extract token and username from Authorization header
        String token = null;
        String username = null;

        // If the Authorization header starts with 'Bearer ', extract the token and username
        if (authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            log.debug("JWT token extracted from Authorization header");
            // Extract the username from the JWT token using JwtUtil
            username = JwtUtil.extractUsername(token);
        }

        // If username is present, authentication is not already set, and token is valid, set authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null
                && JwtUtil.isTokenValid(token)) {
            log.debug("JWT token is valid, setting authentication for user: {}", username);
            // Create an authentication token and set it in the security context
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    new User(username, "", Collections.emptyList()), null, Collections.emptyList());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } else if (username != null) {
            // If username is present but token is invalid or authentication is already set
            log.warn("JWT token is invalid or authentication already set for user: {}", username);
        }

        // Continue the filter chain for the next filter or resource
        filterChain.doFilter(request, response);
    }
}
