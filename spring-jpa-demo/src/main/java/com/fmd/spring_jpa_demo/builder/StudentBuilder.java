package com.fmd.spring_jpa_demo.builder;

import com.fmd.spring_jpa_demo.dto.StudentDTO;
import com.fmd.spring_jpa_demo.entity.Student;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for building Student and StudentDTO objects.
 */
@UtilityClass
public class StudentBuilder {

    /**
     * Converts a StudentDTO to a Student entity.
     *
     * @param studentDTO the student DTO
     * @return the Student entity
     */
    public static Student toEntity(StudentDTO studentDTO) {

        var student = Student.builder()
                .firstName(studentDTO.firstName())
                .lastName(studentDTO.lastName())
                .addressList(new ArrayList<>())
                .build();

        for (var addressDTO : studentDTO.addressDTOList()) {
            student.addAddress(AddressBuilder.toEntity(addressDTO));
        }

        return student;
    }

    /**
     * Converts a list of Student entities to a list of StudentDTOs.
     *
     * @param studentList the list of Student entities
     * @return the list of StudentDTOs
     */
    public static List<StudentDTO> toDTO(List<Student> studentList) {

        return studentList.stream()
                .map(StudentBuilder::toDTO)
                .toList();
    }

    /**
     * Converts a Student entity to a StudentDTO.
     *
     * @param student the Student entity
     * @return the StudentDTO
     */
    public static StudentDTO toDTO(Student student) {

        var addressDTOList = AddressBuilder.toDTO(student.getAddressList());

        return StudentDTO.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .addressDTOList(addressDTOList)
                .createdAt(student.getCreatedAt())
                .modifiedAt(student.getModifiedAt())
                .build();
    }

}
