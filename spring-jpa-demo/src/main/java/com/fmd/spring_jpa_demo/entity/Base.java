package com.fmd.spring_jpa_demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLocking;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.Date;

/**
 * Base entity class providing ID, creation, and modification timestamps.
 */
@Getter
@Setter
@DynamicUpdate
@MappedSuperclass
@OptimisticLocking
@Slf4j
@ToString
public class Base implements Serializable {

    /**
     * The unique identifier for the entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

    /**
     * The creation timestamp.
     */
    @CreatedDate
    Date createdAt;

    /**
     * The last modification timestamp.
     */
    @LastModifiedDate
    Date modifiedAt;

    /**
     * Sets creation and modification timestamps before persisting.
     */
    @PrePersist
    public void prePersist() {
        log.info("Inside Pre Persist.");
        this.createdAt = new Date();
        this.modifiedAt = new Date();
    }

    /**
     * Updates the modification timestamp before updating.
     */
    @PreUpdate
    public void preUpdate() {
        log.info("Inside Pre Update.");
        this.modifiedAt = new Date();
    }
}

