package com.fmd.spring_jpa_demo.builder;

import com.fmd.spring_jpa_demo.dto.AddressDTO;
import com.fmd.spring_jpa_demo.dto.StudentDTO;
import com.fmd.spring_jpa_demo.entity.Address;
import com.fmd.spring_jpa_demo.entity.Student;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class StudentBuilder {

    public static Student toEntity(StudentDTO studentDTO) {

        Student student = Student.builder()
                .firstName(studentDTO.firstName())
                .lastName(studentDTO.lastName())
                .addressList(new ArrayList<>())
                .build();

        for (AddressDTO addressDTO : studentDTO.addressDTOList()) {
            student.addAddress(AddressBuilder.toEntity(addressDTO));
        }

        return student;
    }

    public static List<StudentDTO> toDTO(List<Student> studentList) {

        return studentList.stream()
                .map(StudentBuilder::toDTO)
                .toList();
    }

    public static StudentDTO toDTO(Student student) {

        List<AddressDTO> addressDTOList = AddressBuilder.toDTO(student.getAddressList());

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
