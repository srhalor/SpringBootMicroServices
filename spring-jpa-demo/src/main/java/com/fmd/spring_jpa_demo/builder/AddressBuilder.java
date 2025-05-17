package com.fmd.spring_jpa_demo.builder;

import com.fmd.spring_jpa_demo.dto.AddressDTO;
import com.fmd.spring_jpa_demo.entity.Address;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class AddressBuilder {

    public static Address toEntity(AddressDTO addressDTO) {
        return Address.builder()
                .area(addressDTO.area())
                .city(addressDTO.city())
                .zipcode(addressDTO.zipcode())
                .build();
    }

    public static List<AddressDTO> toDTO(List<Address> addressList) {

        return addressList.stream()
                .map(AddressBuilder::toDTO)
                .toList();
    }

    private static AddressDTO toDTO(Address address) {
        return AddressDTO.builder()
                .id(address.getId())
                .area(address.getArea())
                .city(address.getCity())
                .zipcode(address.getZipcode())
                .createdAt(address.getCreatedAt())
                .modifiedAt(address.getModifiedAt())
                .build();
    }
}
