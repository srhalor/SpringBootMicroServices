package com.fmd.spring_jpa_demo.security;

/**
 * Exception thrown when JWT payload parsing fails.
 */
public class JwtParseException extends RuntimeException {
    /**
     * Constructs a new JwtParseException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public JwtParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
