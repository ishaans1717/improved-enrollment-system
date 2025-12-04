package com.improved_enrollment_system.improved_enrollment_system.repository;

import com.improved_enrollment_system.improved_enrollment_system.entity.CoursePrerequisite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoursePrerequisiteRepository extends JpaRepository<CoursePrerequisite, Long> {

    List<CoursePrerequisite> findByCourseId(Long courseId);
    List<CoursePrerequisite> findByPrerequisiteCourseId(Long prerequisiteCourseId);
}