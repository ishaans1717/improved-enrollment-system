package com.improved_enrollment_system.improved_enrollment_system.repository;

import com.improved_enrollment_system.improved_enrollment_system.entity.Waitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {

    List<Waitlist> findByCourseIdOrderByPositionAsc(Long courseId);
    List<Waitlist> findByStudentId(Long studentId);
    Optional<Waitlist> findByStudentIdAndCourseIdAndStatus(Long studentId, Long courseId, String status);

    long countByCourseIdAndStatus(Long courseId, String status);
}
