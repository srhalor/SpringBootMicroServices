package com.fmd.spring_jpa_demo.dto;

import org.springframework.data.domain.Sort;

/**
 * DTO for pagination and sorting request parameters.
 */
public record PagingAndSortingRequest(
        Integer offset,
        Integer pageSize,
        String sortBy,
        Sort.Direction direction
) {
    /**
     * Constructs a PagingAndSortingRequest with default values.
     */
    public PagingAndSortingRequest() {
        this(0, 10, "id", Sort.Direction.ASC);
    }
}
