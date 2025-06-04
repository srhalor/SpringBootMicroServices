package com.fmd.spring_jpa_demo.dto.mapper;

import com.fmd.spring_jpa_demo.dto.AddressDTO;
import com.fmd.spring_jpa_demo.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AddressMapper {

    @Mapping(target = "student", ignore = true)
    Address toEntity(AddressDTO addressDTO);
    AddressDTO toDTO(Address address);
    List<AddressDTO> toDTO(List<Address> addressList);
    List<Address> toEntity(List<AddressDTO> addressDTOList);
}
