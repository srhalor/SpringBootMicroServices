package com.fmd.spring_jpa_demo.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Immutable error response object for API errors using Java record.
 * <p>
 * Contains timestamp, HTTP status, error, message, and request path.
 */
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
    /**
     * Convenience constructor for building ApiError from HttpStatus, message, and path.
     *
     * @param status  the HTTP status
     * @param message the error message
     * @param path    the request path
     */
    public ApiError(HttpStatus status, String message, String path) {
        this(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, path);
    }
}
