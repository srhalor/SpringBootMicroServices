package com.fmd.spring_jpa_demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicUpdate
@Builder
@Table(name = "student")
public class Student extends Base {

    private String firstName;
    private String lastName;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "student", orphanRemoval = true)
    private List<Address> addressList;

    public void addAddress(Address address) {
        addressList.add(address);
        address.setStudent(this);
    }

}