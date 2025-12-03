package com.improved_enrollment_system.improved_enrollment_system.repository;

import com.improved_enrollment_system.improved_enrollment_system.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Enrollment> findByStudentIdAndStatus(Long studentId, String status);

    long countByCourseIdAndStatus(Long courseId, String status);
}
