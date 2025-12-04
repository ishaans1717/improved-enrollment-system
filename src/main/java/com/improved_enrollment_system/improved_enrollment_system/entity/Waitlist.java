package com.improved_enrollment_system.improved_enrollment_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "waitlist")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Waitlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "joined_date")
    private LocalDateTime joinedDate;

    @Column(name = "position")
    private Integer position;

    @Column(name = "status")
    private String status; //"WAITING", "ENROLLED", "EXPIRED", "REMOVED"

    @PrePersist
    protected void onCreate() {
        joinedDate = LocalDateTime.now();
        if (status == null) {
            status = "WAITING";
        }
    }
}