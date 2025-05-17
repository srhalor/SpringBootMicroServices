package com.fmd.spring_jpa_demo.exception;

/**
 * Exception thrown when a student is not found in the database.
 */
public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(int id) {
        super("Student not found with ID: " + id);
    }
}

