package com.fmd.spring_jpa_demo.service;

import com.fmd.spring_jpa_demo.builder.StudentBuilder;
import com.fmd.spring_jpa_demo.dto.StudentDTO;
import com.fmd.spring_jpa_demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for managing Student entities and related operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;

    /**
     * Saves a new student.
     *
     * @param studentDTO the student data transfer object
     * @return the saved student as DTO
     */
    public StudentDTO saveStudent(StudentDTO studentDTO) {
        log.info("Saving student: {}", studentDTO);
        var student = StudentBuilder.toEntity(studentDTO);
        log.debug("Student entity to be saved: {}", student);
        var savedStudent = studentRepository.save(student);
        log.info("Student saved with ID: {}", savedStudent.getId());
        return StudentBuilder.toDTO(savedStudent);
    }

    /**
     * Retrieves all students with pagination.
     *
     * @param pageRequest the page request
     * @return a page of student DTOs
     */
    public Page<StudentDTO> getAllStudent(PageRequest pageRequest) {
        log.info("Fetching all students with page request: {}", pageRequest);
        var studentPage = studentRepository.findAll(pageRequest);
        log.debug("Fetched {} students", studentPage.getTotalElements());
        return studentPage.map(StudentBuilder::toDTO);
    }

    /**
     * Retrieves a student by ID.
     *
     * @param id the student ID
     * @return an optional student DTO
     */
    public Optional<StudentDTO> getStudentById(int id) {
        log.info("Fetching student by ID: {}", id);
        var result = studentRepository.findById(id).map(StudentBuilder::toDTO);
        if (result.isPresent()) {
            log.info("Student found for ID: {}", id);
        } else {
            log.warn("No student found for ID: {}", id);
        }
        return result;
    }

    /**
     * Updates an existing student.
     *
     * @param id         the student ID
     * @param studentDTO the student data transfer object
     * @return the updated student as DTO
     */
    public StudentDTO updateStudent(int id, StudentDTO studentDTO) {
        log.info("Updating student with ID: {}", id);
        var student = studentRepository.findById(id).orElseThrow();
        log.debug("Student from DB: {}", student);

        student.setFirstName(studentDTO.firstName());
        student.setLastName(studentDTO.lastName());

        for (var address : student.getAddressList()) {
            for (var addressDTO : studentDTO.addressDTOList()) {
                if (address.getId() == addressDTO.id()) {
                    log.debug("Updating address with ID: {} for student ID: {}", address.getId(), id);
                    address.setArea(addressDTO.area());
                    address.setCity(addressDTO.city());
                    address.setZipcode(addressDTO.zipcode());
                }
            }
        }

        log.info("Student after update: {}", student);
        var updatedStudent = studentRepository.save(student);
        log.info("Student updated and saved with ID: {}", updatedStudent.getId());
        return StudentBuilder.toDTO(updatedStudent);
    }

    /**
     * Deletes a student by ID.
     *
     * @param id the student ID
     */
    public void deleteStudent(int id) {
        log.info("Deleting student with ID: {}", id);
        var student = studentRepository.findById(id).orElseThrow();
        studentRepository.delete(student);
        log.info("Student deleted with ID: {}", id);
    }
}
