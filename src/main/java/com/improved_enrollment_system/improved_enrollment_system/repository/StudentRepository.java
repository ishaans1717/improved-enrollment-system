package com.improved_enrollment_system.improved_enrollment_system.repository;

import com.improved_enrollment_system.improved_enrollment_system.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}