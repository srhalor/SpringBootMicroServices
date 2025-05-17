package com.fmd.spring_jpa_demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Date;
import java.util.List;

@Builder
public record StudentDTO(
        int id,
        String firstName,
        String lastName,
        @JsonProperty("address")
        List<AddressDTO> addressDTOList,
        Date createdAt,
        Date modifiedAt) {
}
