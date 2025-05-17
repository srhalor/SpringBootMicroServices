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

@RestController
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/student")
    public StudentDTO saveStudent(@RequestBody StudentDTO studentDTO) {
        log.info("Saving Student : {}", studentDTO);
        return studentService.saveStudent(studentDTO);
    }

    @GetMapping("/student")
    public Page<StudentDTO> getAllStudent(
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction) {

        log.info("Get All Students with offset [{}], page size [{}] and " +
                "sort by [{}] with direction [{}]", offset, pageSize, sortBy, direction);

        var sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        return studentService.getAllStudent(PageRequest.of(offset, pageSize, sort));
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable int id) {

        log.info("Find student by ID : {}", id);
        Optional<StudentDTO> studentDTO = studentService.getStudentById(id);

        return studentDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/student/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@RequestBody StudentDTO studentDTO,
                                                    @PathVariable int id) {

        log.info("Update student by ID : {}", id);
        try {
            StudentDTO updatedStudentDTO = studentService.updateStudent(id, studentDTO);
            return ResponseEntity.ok(updatedStudentDTO);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

    }

    @DeleteMapping("/student/{id}")
    public ResponseEntity<Void> deleteStudentById(@PathVariable int id) {

        log.info("Delete student by ID : {}", id);
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
