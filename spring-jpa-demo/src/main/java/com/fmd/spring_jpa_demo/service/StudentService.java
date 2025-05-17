package com.fmd.spring_jpa_demo.service;

import com.fmd.spring_jpa_demo.dto.StudentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

/**
 * Service interface for managing Student entities and related operations.
 * <p>
 * Provides methods for saving, retrieving, updating, and deleting students.
 */
public interface StudentService {
    /**
     * Saves a new student.
     *
     * @param studentDTO the student data transfer object
     * @return the saved student as DTO
     */
    StudentDTO saveStudent(StudentDTO studentDTO);

    /**
     * Retrieves all students with pagination.
     *
     * @param pageRequest the page request
     * @return a page of student DTOs
     */
    Page<StudentDTO> getAllStudent(PageRequest pageRequest);

    /**
     * Retrieves a student by ID.
     *
     * @param id the student ID
     * @return an optional student DTO
     */
    Optional<StudentDTO> getStudentById(int id);

    /**
     * Updates an existing student.
     *
     * @param id the student ID
     * @param studentDTO the student data transfer object
     * @return the updated student as DTO
     */
    StudentDTO updateStudent(int id, StudentDTO studentDTO);

    /**
     * Deletes a student by ID.
     *
     * @param id the student ID
     */
    void deleteStudent(int id);
}

