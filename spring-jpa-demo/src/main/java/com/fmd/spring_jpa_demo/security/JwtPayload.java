package com.fmd.spring_jpa_demo.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;

import java.util.Date;
import java.util.List;

/**
 * JWT payload record representing the claims in a JWT token.
 * <p>
 * Includes issuer, issuedAt, expiration, audience, subject, name, and roles.
 */
@Builder
public record JwtPayload(
    // JWT issuer (who issued the token)
    @JsonProperty("iss") String issuer,
    // JWT issued at (timestamp in seconds since epoch)
    @JsonProperty("iat")
    @JsonDeserialize(using = JwtDateDeserializer.class)
    Date issuedAt,
    // JWT expiration (timestamp in seconds since epoch)
    @JsonProperty("exp")
    @JsonDeserialize(using = JwtDateDeserializer.class)
    Date expiration,
    // JWT audience (intended recipient)
    @JsonProperty("aud") String audience,
    // JWT subject (the user or entity the token refers to)
    @JsonProperty("sub") String subject,
    // Name of the user/entity
    @JsonProperty("name") String name,
    // Roles assigned to the user/entity
    @JsonProperty("roles") List<String> roles
) {}
