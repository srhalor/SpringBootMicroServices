package com.fmd.spring_jpa_demo.dto;

import lombok.Builder;

import java.util.Date;

/**
 * Data transfer object for Address entity.
 */
@Builder
public record AddressDTO(
        int id,
        String area,
        String city,
        String zipcode,
        Date createdAt,
        Date modifiedAt) {
}
