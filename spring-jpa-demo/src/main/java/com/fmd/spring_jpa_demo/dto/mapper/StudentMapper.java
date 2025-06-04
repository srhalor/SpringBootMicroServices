package com.fmd.spring_jpa_demo.dto.mapper;

import com.fmd.spring_jpa_demo.dto.StudentDTO;
import com.fmd.spring_jpa_demo.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = AddressMapper.class)
public interface StudentMapper {

    @Mapping(source = "addressDTOList", target = "addressList")
    Student toEntity(StudentDTO studentDTO);

    @Mapping(source = "addressList", target = "addressDTOList")
    StudentDTO toDTO(Student student);

    List<StudentDTO> toDTO(List<Student> studentList);
    List<Student> toEntity(List<StudentDTO> studentDTOList);
}
