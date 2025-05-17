package com.fmd.spring_jpa_demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

/**
 * Entity representing a student with a list of addresses.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicUpdate
@Builder
@Slf4j
@Table(name = "student")
public class Student extends Base {

    /**
     * The first name of the student.
     */
    private String firstName;
    /**
     * The last name of the student.
     */
    private String lastName;

    /**
     * List of addresses associated with the student.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "student", orphanRemoval = true)
    private List<Address> addressList;

    /**
     * Adds an address to the student's address list.
     *
     * @param address the address to add
     */
    public void addAddress(Address address) {
        log.debug("Adding address [{}] to student [{}]", address, this);
        addressList.add(address);
        address.setStudent(this);
    }

}

