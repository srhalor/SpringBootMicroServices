package com.fmd.spring_jpa_demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

/**
 * Entity representing an address associated with a student.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicUpdate
@Builder
@Table(name = "address")
public class Address extends Base {

    /**
     * The area of the address.
     */
    private String area;
    /**
     * The city of the address.
     */
    private String city;
    /**
     * The zipcode of the address.
     */
    private String zipcode;

    /**
     * The student associated with this address.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @ToString.Exclude
    private Student student;
}

