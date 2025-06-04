package com.fmd.spring_jpa_demo.security;

import lombok.experimental.StandardException;

/**
 * Exception thrown when JWT payload parsing fails.
 */
@StandardException
public class JwtParseException extends RuntimeException {
}
