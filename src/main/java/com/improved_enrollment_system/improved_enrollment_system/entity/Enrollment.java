package com.improved_enrollment_system.improved_enrollment_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "enrollment_date")
    private LocalDateTime enrollmentDate;

    @Column(name = "status")
    private String status; // "ENROLLED", "COMPLETED", "WITHDRAWN", "IN_PROGRESS"

    @Column(name = "grade")
    private Double grade; // null if not completed

    @Column(name = "progress_percentage")
    private Double progressPercentage; // 0-100%

    @PrePersist
    protected void onCreate() {
        enrollmentDate = LocalDateTime.now();
        if (status == null) {
            status = "IN_PROGRESS";
        }
        if (progressPercentage == null) {
            progressPercentage = 0.0;
        }
    }
}