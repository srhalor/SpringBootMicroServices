package com.fmd.spring_jpa_demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLocking;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@DynamicUpdate
@MappedSuperclass
@OptimisticLocking
@Slf4j
@ToString
public class Base implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

/*    @CreatedBy
    User creator;

    @LastModifiedBy
    User modifier;*/

    //@Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    Date createdAt;

    //@Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    Date modifiedAt;

    @PrePersist
    public void prePersist() {
        log.info("Inside Pre Persist.");
        this.createdAt = new Date();
        this.modifiedAt = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        log.info("Inside Pre Update.");
        this.modifiedAt = new Date();
    }
}