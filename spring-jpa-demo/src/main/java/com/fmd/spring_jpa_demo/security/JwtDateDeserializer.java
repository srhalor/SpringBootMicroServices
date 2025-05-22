package com.fmd.spring_jpa_demo.security;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Date;

/**
 * Custom deserializer for JWT date fields (iat, exp) that is in seconds since epoch.
 */
public class JwtDateDeserializer extends JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        // Parse the numeric value (seconds since epoch) and convert to java.util.Date
        long seconds = p.getLongValue();
        return new Date(seconds * 1000L);
    }
}

