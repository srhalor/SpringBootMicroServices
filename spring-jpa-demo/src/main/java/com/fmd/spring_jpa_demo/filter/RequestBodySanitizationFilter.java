package com.fmd.spring_jpa_demo.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that intercepts incoming HTTP requests with JSON bodies, recursively sanitizes all string values,
 * and passes the sanitized request to the rest of the filter chain. Non-JSON requests are passed through unchanged.
 * <p>
 * This filter is intended to prevent malicious input by sanitizing JSON request bodies before they reach controllers.
 * </p>
 *
 * @author Shailesh Halor
 */
@Slf4j
@Component
@Order
@ConditionalOnProperty(prefix = "request.body.sanitization", name = "enabled", havingValue = "true")
public class RequestBodySanitizationFilter extends OncePerRequestFilter {

    /**
     * Filters incoming requests, sanitizing JSON request bodies if present.
     *
     * <p>If the request is an HTTP request with a JSON content type, the request body is wrapped and sanitized
     * before passing it along the filter chain. Otherwise, the request is passed through unchanged.</p>
     *
     * @param request     the incoming HttpServletRequest
     * @param response    the outgoing HttpServletResponse
     * @param filterChain the filter chain
     * @throws IOException      if an I/O error occurs during filtering
     * @throws ServletException if a servlet error occurs during filtering
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        var contentType = request.getContentType();
        // Use Spring's StringUtils for null/empty check and case-insensitive contains
        if (StringUtils.hasText(contentType) && contentType.toLowerCase().contains("application/json")) {
            log.trace("JSON request detected. Wrapping and sanitizing request body.");
            // Wrap and sanitize the request body
            var sanitizedRequest = new SanitizedRequestWrapper(request);
            // Pass the sanitized request to the next filter in the chain
            filterChain.doFilter(sanitizedRequest, response);
        } else {
            log.trace("Non-JSON request or missing content type. Passing through without sanitization.");
            // Pass non-JSON requests through unchanged
            filterChain.doFilter(request, response);
        }
    }
}
