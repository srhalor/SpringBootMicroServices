package com.fmd.spring_jpa_demo.security;

/**
 * Exception thrown when JWT payload validation fails (e.g., missing subject, expiration, or expired token).
 */
public class JwtValidationException extends RuntimeException {
    /**
     * Constructs a new JwtValidationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public JwtValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new JwtValidationException with the specified detail message.
     *
     * @param message the detail message
     */
    public JwtValidationException(String message) {
        super(message);
    }
}

