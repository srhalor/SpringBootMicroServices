package com.fmd.spring_jpa_demo.builder;

import com.fmd.spring_jpa_demo.dto.AddressDTO;
import com.fmd.spring_jpa_demo.entity.Address;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * Utility class for building Address and AddressDTO objects.
 */
@UtilityClass
public class AddressBuilder {

    /**
     * Converts an AddressDTO to an Address entity.
     *
     * @param addressDTO the address DTO
     * @return the Address entity
     */
    public static Address toEntity(AddressDTO addressDTO) {
        return Address.builder()
                .area(addressDTO.area())
                .city(addressDTO.city())
                .zipcode(addressDTO.zipcode())
                .build();
    }

    /**
     * Converts a list of Address entities to a list of AddressDTOs.
     *
     * @param addressList the list of Address entities
     * @return the list of AddressDTOs
     */
    public static List<AddressDTO> toDTO(List<Address> addressList) {
        return addressList.stream()
                .map(AddressBuilder::toDTO)
                .toList();
    }

    /**
     * Converts an Address entity to an AddressDTO.
     *
     * @param address the Address entity
     * @return the AddressDTO
     */
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
