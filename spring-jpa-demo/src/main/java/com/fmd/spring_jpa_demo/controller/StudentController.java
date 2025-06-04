package com.fmd.spring_jpa_demo.controller;

import com.fmd.spring_jpa_demo.dto.PagingAndSortingRequest;
import com.fmd.spring_jpa_demo.dto.StudentDTO;
import com.fmd.spring_jpa_demo.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing Student resources.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/student")
public class StudentController {

    private final StudentService studentService;

    /**
     * Creates a new student.
     *
     * @param studentDTO the student data transfer object
     * @return the saved student DTO
     */
    @PostMapping
    public StudentDTO saveStudent(@Valid @RequestBody StudentDTO studentDTO) {
        log.info("Saving Student : {}", studentDTO);
        var saved = studentService.saveStudent(studentDTO);
        log.info("Student saved: {}", saved);
        return saved;
    }

    /**
     * Retrieves all students with pagination and sorting.
     *
     * @param pageRequestDto the paging and sorting request DTO
     * @return a page of student DTOs
     */
    @GetMapping
    public Page<StudentDTO> getAllStudent(@ModelAttribute PagingAndSortingRequest pageRequestDto) {

        log.info("Get All Students with offset [{}], page size [{}] and sort by [{}] with direction [{}]",
                pageRequestDto.offset(), pageRequestDto.pageSize(), pageRequestDto.sortBy(), pageRequestDto.direction());

        var sort = Sort.by(pageRequestDto.direction(), pageRequestDto.sortBy());
        var pageRequest = PageRequest.of(pageRequestDto.offset(), pageRequestDto.pageSize(), sort);

        var page = studentService.getAllStudent(pageRequest);

        log.info("Fetched {} students", page.getTotalElements());
        return page;
    }

    /**
     * Retrieves a student by ID.
     *
     * @param id the student ID
     * @return the student DTO or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable int id) {
        log.info("Find student by ID : {}", id);
        // Let StudentNotFoundException propagate to GlobalExceptionHandler
        var studentDTO = studentService.getStudentById(id);
        log.info("Student found for ID: {}", id);
        return ResponseEntity.ok(studentDTO);
    }

    /**
     * Updates a student by ID.
     *
     * @param studentDTO the student data transfer object
     * @param id         the student ID
     * @return the updated student DTO or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@Valid @RequestBody StudentDTO studentDTO,
                                                    @PathVariable int id) {
        log.info("Update student by ID : {}", id);
        var updatedStudentDTO = studentService.updateStudent(id, studentDTO);
        log.info("Student updated: {}", updatedStudentDTO);
        return ResponseEntity.ok(updatedStudentDTO);
    }

    /**
     * Deletes a student by ID.
     * This method is only accessible to users with the "Admin" role.
     *
     * @param id the student ID
     * @return 204 if deleted, 404 if not found
     */
    @PreAuthorize("hasAuthority('Admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudentById(@PathVariable int id) {
        log.info("Delete student by ID : {}", id);
        // Let StudentNotFoundException propagate to GlobalExceptionHandler
        studentService.deleteStudent(id);
        log.info("Student deleted with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
