package com.fmd.spring_jpa_demo.service.impl;

import com.fmd.spring_jpa_demo.dto.StudentDTO;
import com.fmd.spring_jpa_demo.dto.mapper.StudentMapper;
import com.fmd.spring_jpa_demo.exception.StudentNotFoundException;
import com.fmd.spring_jpa_demo.repository.StudentRepository;
import com.fmd.spring_jpa_demo.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * Service class for managing Student entities and related operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    /**
     * Saves a new student.
     *
     * @param studentDTO the student data transfer object
     * @return the saved student as DTO
     */
    public StudentDTO saveStudent(StudentDTO studentDTO) {
        log.info("Saving student: {}", studentDTO);
        var student = studentMapper.toEntity(studentDTO);
        log.debug("Student entity to be saved: {}", student);
        var savedStudent = studentRepository.save(student);
        log.info("Student saved with ID: {}", savedStudent.getId());
        return studentMapper.toDTO(savedStudent);
    }

    /**
     * Retrieves all students with pagination.
     *
     * @param pageRequest the page request
     * @return a page of student DTOs
     */
    public Page<StudentDTO> getAllStudent(PageRequest pageRequest) {
        log.info("Fetching all students with page request: {}", pageRequest);
        return studentRepository.findAll(pageRequest)
                .map(studentMapper::toDTO);
    }

    /**
     * Retrieves a student by ID.
     *
     * @param id the student ID
     * @return an optional student DTO
     */
    public StudentDTO getStudentById(int id) {
        log.info("Fetching student by ID: {}", id);
        var student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        log.info("Student found for ID: {}", id);
        return studentMapper.toDTO(student);
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
        var student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        log.debug("Student from DB: {}", student);

        student.setFirstName(studentDTO.firstName());
        student.setLastName(studentDTO.lastName());

        student.getAddressList().forEach(address ->
                studentDTO.addressDTOList().stream()
                        .filter(addressDTO -> address.getId() == addressDTO.id())
                        .findFirst()
                        .ifPresent(addressDTO -> {
                            log.debug("Updating address with ID: {} for student ID: {}", address.getId(), id);
                            address.setArea(addressDTO.area());
                            address.setCity(addressDTO.city());
                            address.setZipcode(addressDTO.zipcode());
                        })
        );

        log.info("Student after update: {}", student);
        var updatedStudent = studentRepository.save(student);
        log.info("Student updated and saved with ID: {}", updatedStudent.getId());
        return studentMapper.toDTO(updatedStudent);
    }

    /**
     * Deletes a student by ID.
     *
     * @param id the student ID
     */
    public void deleteStudent(int id) {
        log.info("Deleting student with ID: {}", id);
        var student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        studentRepository.delete(student);
        log.info("Student deleted with ID: {}", id);
    }
}
