package com.fmd.spring_jpa_demo.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * Utility class for sanitizing log messages and all string fields in a JSON string using Jackson.
 * <p>
 * Provides methods to sanitize strings and recursively sanitize all string fields in JSON objects/arrays.
 * </p>
 *
 * @author Shailesh Halor
 */
@Slf4j
@UtilityClass
public class SanitizerUtil {
    /**
     * Sanitizes the input string by removing control characters such as CR, LF,
     * and other non-printable characters.
     *
     * @param input the string to sanitize
     * @return a sanitized version of the input string
     */
    public static String sanitize(String input) {
        // Use Spring's StringUtils to check for null or empty
        if (!StringUtils.hasText(input)) return input;
        // Remove all control characters (including CR, LF, tab, form feed, vertical tab, etc.)
        return input.replaceAll("\\p{Cntrl}", "");
    }

    /**
     * Sanitizes all string fields in the given JSON string using Jackson.
     *
     * <p>Recursively traverses the JSON structure and sanitizes all string values.</p>
     *
     * @param json the original JSON string
     * @return a sanitized JSON string
     */
    public static String sanitizeJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Object node = mapper.readTree(json);
            log.trace("Original JSON for sanitization: {}", json);
            sanitizeJsonNode(node, mapper);
            String sanitizedJson = mapper.writeValueAsString(node);
            log.trace("Sanitized JSON: {}", sanitizedJson);
            return sanitizedJson;
        } catch (Exception e) {
            log.trace("Failed to parse JSON for sanitization. Returning original JSON. Error: {}", e.getMessage());
            // If parsing fails, fallback to original JSON
            return json;
        }
    }

    /**
     * Recursively sanitizes all string fields in a JSON node (ObjectNode or ArrayNode).
     *
     * <p>This method is used internally to traverse and sanitize all string values in the JSON tree.</p>
     *
     * @param node   the root JSON node to sanitize (ObjectNode or ArrayNode)
     * @param mapper the Jackson ObjectMapper used to create sanitized text nodes
     */
    public static void sanitizeJsonNode(Object node, ObjectMapper mapper) {
        if (node instanceof ObjectNode objectNode) {
            objectNode.fields().forEachRemaining(entry -> {
                JsonNode child = entry.getValue();
                if (child.isTextual()) {
                    objectNode.replace(entry.getKey(), mapper.getNodeFactory().textNode(sanitize(child.asText())));
                } else {
                    sanitizeJsonNode(child, mapper);
                }
            });
        } else if (node instanceof ArrayNode arrayNode) {
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode child = arrayNode.get(i);
                if (child.isTextual()) {
                    arrayNode.set(i, mapper.getNodeFactory().textNode(sanitize(child.asText())));
                } else {
                    sanitizeJsonNode(child, mapper);
                }
            }
        }
    }
}

