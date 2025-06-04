package com.fmd.spring_jpa_demo.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static com.fmd.spring_jpa_demo.util.SanitizerUtil.sanitizeJson;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * HttpServletRequestWrapper that replaces the request body with a sanitized version.
 * <p>
 * This wrapper reads the original request body, sanitizes all string values in the JSON,
 * and provides the sanitized body to downstream filters and controllers.
 * </p>
 *
 * @author Shailesh Halor
 */
@Slf4j
public class SanitizedRequestWrapper extends HttpServletRequestWrapper {
    private final String sanitizedBody;

    /**
     * Reads and sanitizes the JSON request body.
     *
     * <p>The constructor reads the request body as a string, sanitizes all string fields in the JSON,
     * and stores the sanitized result for future use.</p>
     *
     * @param request the original HttpServletRequest
     * @throws IOException if an I/O error occurs while reading the body
     */
    public SanitizedRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        // Read the request body as a string
        String body = new BufferedReader(new InputStreamReader(request.getInputStream(), UTF_8))
                .lines().collect(Collectors.joining("\n"));
        // Sanitize all string fields in the JSON body
        this.sanitizedBody = sanitizeJson(body);
        log.trace("Sanitized request body: {}", sanitizedBody);
    }

    /**
     * Returns a ServletInputStream for the sanitized request body.
     *
     * <p>This allows the sanitized JSON to be read as an input stream by downstream code.</p>
     *
     * @return ServletInputStream containing the sanitized JSON
     */
    @Override
    public ServletInputStream getInputStream() {
        // Provide the sanitized body as a byte stream
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sanitizedBody.getBytes(UTF_8));
        return new ServletInputStream() {
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // This method is required for Servlet 3.1+ non-blocking IO.
                // Not implemented because this filter does not support async IO.
                // If async IO is needed, implement this method accordingly.
                throw new UnsupportedOperationException("Async ReadListener is not supported in this filter");
            }
        };
    }

    /**
     * Returns a BufferedReader for the sanitized request body.
     *
     * <p>This allows the sanitized JSON to be read as a character stream by downstream code.</p>
     *
     * @return BufferedReader containing the sanitized JSON
     */
    @Override
    public BufferedReader getReader() {
        // Provide the sanitized body as a character stream
        return new BufferedReader(new InputStreamReader(getInputStream(), UTF_8));
    }
}
