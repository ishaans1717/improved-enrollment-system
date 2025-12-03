package com.improved_enrollment_system.improved_enrollment_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CoursePrerequisite entity - defines which courses are prerequisites for other courses
 * Required for: check course prerequisite
 */
@Entity
@Table(name = "course_prerequisite")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoursePrerequisite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // The course that has prerequisites

    @ManyToOne
    @JoinColumn(name = "prerequisite_course_id", nullable = false)
    private Course prerequisiteCourse; // The required prerequisite course
}