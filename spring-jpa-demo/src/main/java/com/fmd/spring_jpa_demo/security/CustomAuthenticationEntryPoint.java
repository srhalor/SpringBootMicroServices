package com.fmd.spring_jpa_demo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fmd.spring_jpa_demo.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom AuthenticationEntryPoint to return a JSON response on authentication failure.
 * <p>
 * This class implements Spring Security's AuthenticationEntryPoint interface and is used to handle
 * authentication errors by returning a structured JSON response containing error details.
 *
 * <p>On authentication failure, it responds with HTTP 401 (Unauthorized) and a JSON body
 * containing an {@link ApiError} object with the error message and request URI.</p>
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /**
     * Handles authentication failures by sending a JSON response with error details.
     *
     * @param request       the HttpServletRequest
     * @param response      the HttpServletResponse
     * @param authException the exception that caused the authentication failure
     * @throws IOException if an input or output exception occurs
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // Build ApiError object for the response
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED,
                "Authentication failed: " + authException.getMessage(),
                request.getRequestURI());
        // Write ApiError as JSON
        ObjectMapper mapper = new ObjectMapper();
        // Register JavaTimeModule to handle LocalDateTime serialization
        mapper.registerModule(new JavaTimeModule());
        // Disable writing dates as timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        response.getWriter().write(mapper.writeValueAsString(apiError));
    }
}

