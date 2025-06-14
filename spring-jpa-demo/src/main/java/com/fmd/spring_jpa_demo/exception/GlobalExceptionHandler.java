package com.fmd.spring_jpa_demo.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

/**
 * Global exception handler for REST controllers.
 * <p>
 * Intercepts exceptions and returns a custom ApiError response object.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles EntityNotFoundException and returns a 404 error response.
     *
     * @param ex      the exception
     * @param request the web request
     * @return ResponseEntity with ApiError and 404 status
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
        log.warn("Entity not found: {} | Path: {}", ex.getMessage(), request.getDescription(false));
        ApiError error = new ApiError(NOT_FOUND, ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(error, NOT_FOUND);
    }

    /**
     * Handles validation errors and returns a 400 error response with field error details.
     *
     * @param ex      the exception
     * @param request the web request
     * @return ResponseEntity with ApiError and 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        String message = "Validation failed: " + errors;
        log.warn("Validation error: {} | Path: {}", message, request.getDescription(false));
        ApiError error = new ApiError(BAD_REQUEST, message, request.getDescription(false));
        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    /**
     * Handles StudentNotFoundException and returns a 404 error response with a custom message.
     *
     * @param ex      the exception
     * @param request the web request
     * @return ResponseEntity with ApiError and 404 status
     */
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ApiError> handleStudentNotFound(StudentNotFoundException ex, WebRequest request) {
        log.warn("Student not found: {} | Path: {}", ex.getMessage(), request.getDescription(false));
        ApiError error = new ApiError(NOT_FOUND, ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(error, NOT_FOUND);
    }

    /**
     * Handles AuthorizationDeniedException and returns a 403 error response with a custom message.
     *
     * @param ex      the exception
     * @param request the web request
     * @return ResponseEntity with ApiError and 403 status
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiError> handleAuthorizationDeniedException(AuthorizationDeniedException ex, WebRequest request) {
        log.warn("Access denied: {} | Path: {}", ex.getMessage(), request.getDescription(false));
        ApiError error = new ApiError(FORBIDDEN, "Access Denied: " + ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(error, FORBIDDEN);
    }

    /**
     * Handles all other exceptions and returns a 500 error response.
     *
     * @param ex      the exception
     * @param request the web request
     * @return ResponseEntity with ApiError and 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unhandled exception: {} | Path: {}", ex.getMessage(), request.getDescription(false), ex);
        ApiError error = new ApiError(INTERNAL_SERVER_ERROR, ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(error, INTERNAL_SERVER_ERROR);
    }
}
