package com.fmd.spring_jpa_demo.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filter to log specified request headers into MDC for logging purposes.
 * This filter should be ordered with the highest precedence to ensure it runs before other filters.
 * To use this filter, you need to enable it in your application properties by setting: 'header.logging.enabled=true'.
 * It reads the headers to log from the application properties under `request.header.logging.fields`.
 * <p>
 * Example configuration in application.properties:
 * <pre>
 *     request.header.logging.enabled=true
 *     request.header.logging.fields=Authorization,User-Agent
 * </pre>
 *
 * @author Shailesh Halor
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(prefix = "request.header.logging", name = "enabled", havingValue = "true")
public class HeaderLoggingFilter extends OncePerRequestFilter {

    /**
     * List of header fields to log. This should be configured in application properties.
     * Example: request.header.logging.fields=Authorization,User-Agent
     */
    @Value("${request.header.logging.fields:}")
    private List<String> headers;

    /**
     * This filter is designed to log specified headers into MDC for logging purposes.
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

        try {
            if (null == headers || headers.isEmpty() || headers.stream().allMatch(String::isBlank)) {
                log.warn("No headers configured for logging. Skipping header logging.");
            } else {
                log.trace("Adding headers to MDC: {}", headers);
                for (String header : headers) {
                    String value = request.getHeader(header);
                    if (StringUtils.hasText(value)) {
                        MDC.put(header, value);
                    }
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            log.trace("Removing headers from MDC: {}", headers);
            for (String header : headers) {
                MDC.remove(header);
            }
        }
    }

}
