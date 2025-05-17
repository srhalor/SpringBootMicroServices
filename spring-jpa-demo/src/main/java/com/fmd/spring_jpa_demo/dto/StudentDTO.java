package com.fmd.spring_jpa_demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Date;
import java.util.List;

/**
 * Data transfer object for Student entity.
 */
@Builder
public record StudentDTO(
        @JsonProperty("id")
        int id,

        @JsonProperty("firstName")
        @NotBlank(message = "First name must not be blank")
        @Size(max = 100, message = "First name must be at most 100 characters")
        String firstName,

        @JsonProperty("lastName")
        @NotBlank(message = "Last name must not be blank")
        @Size(max = 100, message = "Last name must be at most 100 characters")
        String lastName,

        @JsonProperty("address")
        @NotNull(message = "Address list must not be null")
        @Size(min = 1, message = "At least one address is required")
        List<@NotNull AddressDTO> addressDTOList,

        @JsonProperty("createdAt")
        Date createdAt,

        @JsonProperty("modifiedAt")
        Date modifiedAt
) {
}
