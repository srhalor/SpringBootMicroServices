package com.fmd.spring_jpa_demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Date;

/**
 * Data transfer object for Address entity.
 */
@Builder
public record AddressDTO(
        @JsonProperty("id")
        int id,

        @JsonProperty("area")
        @NotBlank(message = "Area must not be blank")
        @Size(max = 255, message = "Area must be at most 255 characters")
        String area,

        @JsonProperty("city")
        @NotBlank(message = "City must not be blank")
        @Size(max = 100, message = "City must be at most 100 characters")
        String city,

        @JsonProperty("zipcode")
        @NotBlank(message = "Zipcode must not be blank")
        @Size(max = 20, message = "Zipcode must be at most 20 characters")
        String zipcode,

        @JsonProperty("createdAt")
        Date createdAt,

        @JsonProperty("modifiedAt")
        Date modifiedAt
) {
}
