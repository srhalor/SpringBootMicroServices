package com.fmd.spring_jpa_demo.service;

import com.fmd.spring_jpa_demo.builder.StudentBuilder;
import com.fmd.spring_jpa_demo.dto.AddressDTO;
import com.fmd.spring_jpa_demo.dto.StudentDTO;
import com.fmd.spring_jpa_demo.entity.Address;
import com.fmd.spring_jpa_demo.entity.Student;
import com.fmd.spring_jpa_demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentDTO saveStudent(StudentDTO studentDTO) {

        Student student = StudentBuilder.toEntity(studentDTO);
        log.info("Student to be saved : {}", student);
        Student savedStudent = studentRepository.save(student);
        return StudentBuilder.toDTO(savedStudent);
    }

    public Page<StudentDTO> getAllStudent(PageRequest pageRequest) {

        Page<Student> studentPage = studentRepository.findAll(pageRequest);

        return studentPage.map(StudentBuilder::toDTO);

    }

    public Optional<StudentDTO> getStudentById(int id) {
        return studentRepository.findById(id).map(StudentBuilder::toDTO);
    }

    public StudentDTO updateStudent(int id, StudentDTO studentDTO) {
        Student student = studentRepository.findById(id).orElseThrow();

        log.info("Student from DB : {}", student);

        student.setFirstName(studentDTO.firstName());
        student.setLastName(studentDTO.lastName());

        for (Address address : student.getAddressList()) {

            for (AddressDTO addressDTO : studentDTO.addressDTOList()) {

                if (address.getId() == addressDTO.id()) {
                    address.setArea(addressDTO.area());
                    address.setCity(addressDTO.city());
                    address.setZipcode(addressDTO.zipcode());
                }
            }
        }

        log.info("Updated Student : {}", student);
        Student updatedStudent = studentRepository.save(student);
        return StudentBuilder.toDTO(updatedStudent);
    }

    public void deleteStudent(int id) {
        Student student = studentRepository.findById(id).orElseThrow();
        studentRepository.delete(student);
    }
}
