package com.fmd.spring_jpa_demo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

/**
 * Utility class for handling JWT operations such as extracting username, validating tokens,
 * and parsing JWT payloads. This class is a Spring singleton bean.
 */
@Slf4j
@UtilityClass
public class JwtUtil {

    /**
     * Extracts the username (subject) from the JWT token.
     *
     * @param token the JWT token
     * @return the username (subject) or null if not found
     */
    public String extractUsername(String token) {
        log.debug("Extracting username from token");
        // Extract the username (subject) from the JWT payload if present
        return Optional.ofNullable(token)
                .map(JwtUtil::extractPayload)
                .map(JwtPayload::subject)
                .orElse(null);
    }

    /**
     * Validates the JWT token by checking its expiration.
     *
     * @param token the JWT token
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token) {
        log.debug("Validating token");
        // Validate the token by checking if the expiration date is after the current date
        return Optional.ofNullable(token)
                .map(JwtUtil::extractPayload)
                .map(JwtPayload::expiration)
                .map(exp -> exp.isAfter(Instant.now()))
                .orElse(false);
    }

    /**
     * Extracts the payload from the JWT token and parses it into a JwtPayload object.
     *
     * @param token the JWT token
     * @return the parsed JwtPayload
     * @throws IllegalArgumentException if the token is invalid
     */
    private JwtPayload extractPayload(String token) {
        log.debug("Extracting payload from token");
        // Split the token, decode the payload, and parse it into a JwtPayload object
        return Optional.ofNullable(token)
                .map(t -> t.split("\\."))
                .filter(parts -> parts.length == 3)
                .map(parts -> parts[1])
                .map(Base64.getUrlDecoder()::decode)
                .map(String::new)
                .map(JwtUtil::parsePayload)
                .orElseThrow(() -> new IllegalArgumentException("Invalid JWT token"));

    }

    /**
     * Parses the JWT payload string into a JwtPayload object.
     *
     * @param payloadString the JWT payload as a JSON string
     * @return the JwtPayload object
     * @throws JwtParseException if parsing fails
     */
    private JwtPayload parsePayload(String payloadString) {
        try {
            log.debug("Parsing JWT payload");

            // Create an ObjectMapper instance for JSON parsing
            ObjectMapper mapper = new ObjectMapper();
            // Register the JavaTimeModule to handle Java 8 date/time types
            mapper.registerModule(new JavaTimeModule());
            // Disable writing dates as timestamps
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            // Parse the JSON string into a JwtPayload object using Jackson
            JwtPayload jwtPayload = mapper.readValue(payloadString, JwtPayload.class);
            log.trace("Parsed JWT payload: {}", jwtPayload);

            return jwtPayload;
        } catch (Exception e) {
            // Log and throw a custom exception if parsing fails
            log.error("Failed to parse JWT payload", e);
            throw new JwtParseException("Failed to parse JWT payload", e);
        }
    }
}
