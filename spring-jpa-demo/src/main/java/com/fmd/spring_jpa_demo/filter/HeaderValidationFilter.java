package com.fmd.spring_jpa_demo.filter;

import com.fmd.spring_jpa_demo.exception.ApiError;
import com.fmd.spring_jpa_demo.security.ErrorResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter to validate mandatory request headers.
 * This filter checks if specified headers are present in the request and not empty.
 * If any mandatory header is missing, it responds with a 400 Bad Request error.
 * To use this filter, you need to enable it in your application properties by setting: 'request.header.validation.enabled=true'.
 * It reads the headers to validate from the application properties under `request.header.validation.fields`.
 * <p>
 * Example configuration in application.properties:
 * <pre>
 *     request.header.validation.enabled=true
 *     request.header.validation.fields=Authorization,User-Agent
 * </pre>
 *
 * @author Shailesh Halor
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@ConditionalOnProperty(prefix = "request.header.validation", name = "enabled", havingValue = "true")
public class HeaderValidationFilter extends OncePerRequestFilter {

    /**
     * List of header fields to validate. This should be configured in application properties.
     * Example: request.header.validation.fields=Authorization,User-Agent
     */
    @Value("${request.header.validation.fields:}")
    private List<String> headers;

    /**
     * This filter is designed to validate specified headers in the request.
     * It should be ordered with the highest precedence to ensure it runs before other filters.
     *
     * @param request     the current HTTP request
     * @param response    the current HTTP response
     * @param filterChain the filter chain to continue processing the request
     * @throws ServletException if an error occurs during filtering
     * @throws IOException      if an I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Check if headers are configured for validation
        if (null == headers || headers.stream().allMatch(String::isBlank)) {
            log.warn("No mandatory header fields configured for validation. Skipping header validation.");
            filterChain.doFilter(request, response);
            return;
        }

        // Validate the presence of mandatory headers
        log.debug("Validating mandatory headers: {}", headers);
        List<String> missingHeaders = new ArrayList<>();
        for (var header : headers) {
            var value = request.getHeader(header);
            // Check if the header is present and not empty otherwise add to missingHeaders
            if (!StringUtils.hasText(value)) {
                missingHeaders.add(header);
            }
        }

        // If any mandatory header is missing, log the error and send a 400 Bad Request response
        if (!missingHeaders.isEmpty()) {
            log.warn("Missing mandatory headers: {}", missingHeaders);
            var errorMessage = "Missing mandatory headers: " + String.join(", ", missingHeaders);
            ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, errorMessage, request.getRequestURI());
            ErrorResponseUtil.writeErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, apiError);
            return;
        }
        filterChain.doFilter(request, response);
    }

}
