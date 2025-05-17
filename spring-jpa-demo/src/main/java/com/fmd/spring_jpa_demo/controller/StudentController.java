package com.fmd.spring_jpa_demo.controller;

import com.fmd.spring_jpa_demo.dto.StudentDTO;
import com.fmd.spring_jpa_demo.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST controller for managing Student resources.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;

    /**
     * Creates a new student.
     * @param studentDTO the student data transfer object
     * @return the saved student DTO
     */
    @PostMapping("/student")
    public StudentDTO saveStudent(@RequestBody StudentDTO studentDTO) {
        log.info("Saving Student : {}", studentDTO);
        StudentDTO saved = studentService.saveStudent(studentDTO);
        log.info("Student saved: {}", saved);
        return saved;
    }

    /**
     * Retrieves all students with pagination and sorting.
     * @param offset the page offset
     * @param pageSize the page size
     * @param sortBy the field to sort by
     * @param direction the sort direction
     * @return a page of student DTOs
     */
    @GetMapping("/student")
    public Page<StudentDTO> getAllStudent(
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction) {

        log.info("Get All Students with offset [{}], page size [{}] and " +
                "sort by [{}] with direction [{}]", offset, pageSize, sortBy, direction);

        var sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Page<StudentDTO> page = studentService.getAllStudent(PageRequest.of(offset, pageSize, sort));
        log.debug("Fetched {} students", page.getTotalElements());
        return page;
    }

    /**
     * Retrieves a student by ID.
     * @param id the student ID
     * @return the student DTO or 404 if not found
     */
    @GetMapping("/student/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable int id) {

        log.info("Find student by ID : {}", id);
        Optional<StudentDTO> studentDTO = studentService.getStudentById(id);
        if (studentDTO.isPresent()) {
            log.info("Student found for ID: {}", id);
        } else {
            log.warn("No student found for ID: {}", id);
        }
        return studentDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Updates a student by ID.
     * @param studentDTO the student data transfer object
     * @param id the student ID
     * @return the updated student DTO or 404 if not found
     */
    @PutMapping("/student/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@RequestBody StudentDTO studentDTO,
                                                    @PathVariable int id) {

        log.info("Update student by ID : {}", id);
        try {
            StudentDTO updatedStudentDTO = studentService.updateStudent(id, studentDTO);
            log.info("Student updated: {}", updatedStudentDTO);
            return ResponseEntity.ok(updatedStudentDTO);
        } catch (Exception e) {
            log.error("Error updating student with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }

    }

    /**
     * Deletes a student by ID.
     * @param id the student ID
     * @return 204 if deleted, 404 if not found
     */
    @DeleteMapping("/student/{id}")
    public ResponseEntity<Void> deleteStudentById(@PathVariable int id) {

        log.info("Delete student by ID : {}", id);
        try {
            studentService.deleteStudent(id);
            log.info("Student deleted with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting student with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }
}
