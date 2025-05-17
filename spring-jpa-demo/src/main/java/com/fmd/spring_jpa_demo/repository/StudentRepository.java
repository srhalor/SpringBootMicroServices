package com.fmd.spring_jpa_demo.repository;

import com.fmd.spring_jpa_demo.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Student entity CRUD operations.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
}
