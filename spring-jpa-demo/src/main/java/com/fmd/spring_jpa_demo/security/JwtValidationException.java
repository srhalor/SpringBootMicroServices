package com.fmd.spring_jpa_demo.security;

import lombok.experimental.StandardException;

/**
 * Exception thrown when JWT payload validation fails (e.g., missing subject, expiration, or expired token).
 */
@StandardException
public class JwtValidationException extends RuntimeException {
}

