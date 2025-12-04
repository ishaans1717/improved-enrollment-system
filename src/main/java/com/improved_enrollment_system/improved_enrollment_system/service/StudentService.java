package com.improved_enrollment_system.improved_enrollment_system.service;

import com.improved_enrollment_system.improved_enrollment_system.dto.DeleteAccountResult;
import com.improved_enrollment_system.improved_enrollment_system.dto.GPAResult;
import com.improved_enrollment_system.improved_enrollment_system.entity.Enrollment;
import com.improved_enrollment_system.improved_enrollment_system.entity.Student;
import com.improved_enrollment_system.improved_enrollment_system.entity.Waitlist;
import com.improved_enrollment_system.improved_enrollment_system.repository.EnrollmentRepository;
import com.improved_enrollment_system.improved_enrollment_system.repository.StudentRepository;
import com.improved_enrollment_system.improved_enrollment_system.repository.WaitlistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final WaitlistRepository waitlistRepository;

    public StudentService(StudentRepository studentRepository,
                          EnrollmentRepository enrollmentRepository,
                          WaitlistRepository waitlistRepository) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.waitlistRepository = waitlistRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    public Boolean deleteStudent(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public GPAResult getCurrentGPA(Long studentId) {
        try {
            Student student = studentRepository.findById(studentId).orElse(null);
            if (student == null) {
                return new GPAResult(false, 0.0, "Student not found");
            }
            double gpa = student.getGpa();
            String standing = calculateAcademicStanding(gpa);
            GPAResult result = new GPAResult(true, gpa, "GPA retrieved successfully");
            result.setStanding(standing);
            return result;
        } catch (Exception e) {
            return new GPAResult(false, 0.0, "Error: " + e.getMessage());
        }
    }

    public DeleteAccountResult deleteAccount(Long studentId) {
        try {
            Student student = studentRepository.findById(studentId).orElse(null);
            if (student == null) {
                return new DeleteAccountResult(false, "Student not found");
            }
            List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
            for (Enrollment enrollment : enrollments) {
                enrollmentRepository.delete(enrollment);
            }
            List<Waitlist> waitlistEntries = waitlistRepository.findByStudentId(studentId);
            for (Waitlist waitlist : waitlistEntries) {
                waitlistRepository.delete(waitlist);
            }
            studentRepository.deleteById(studentId);
            return new DeleteAccountResult(true, "Account deleted successfully");
        } catch (Exception e) {
            return new DeleteAccountResult(false, "Error: " + e.getMessage());
        }
    }

    public GPAResult updateGPA(Long studentId, double newGpa) {
        try {
            if (newGpa < 0.0 || newGpa > 4.0) {
                return new GPAResult(false, newGpa, "GPA must be between 0.0 and 4.0");
            }
            Student student = studentRepository.findById(studentId).orElse(null);
            if (student == null) {
                return new GPAResult(false, 0.0, "Student not found");
            }
            student.setGpa(newGpa);
            studentRepository.save(student);
            String standing = calculateAcademicStanding(newGpa);
            GPAResult result = new GPAResult(true, newGpa, "GPA updated successfully");
            result.setStanding(standing);
            return result;
        } catch (Exception e) {
            return new GPAResult(false, 0.0, "Error: " + e.getMessage());
        }
    }

    public String getAcademicStanding(Long studentId) {
        try {
            Student student = studentRepository.findById(studentId).orElse(null);
            if (student == null) {
                return "Unknown";
            }
            return calculateAcademicStanding(student.getGpa());
        } catch (Exception e) {
            return "Error";
        }
    }

    public List<Enrollment> getStudentEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<Enrollment> getActiveEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentIdAndStatus(studentId, "IN_PROGRESS");
    }

    public List<Enrollment> getCompletedCourses(Long studentId) {
        return enrollmentRepository.findByStudentIdAndStatus(studentId, "COMPLETED");
    }

    public List<Waitlist> getStudentWaitlists(Long studentId) {
        return waitlistRepository.findByStudentId(studentId);
    }

    public int getTotalCreditsEnrolled(Long studentId) {
        List<Enrollment> activeEnrollments = enrollmentRepository
                .findByStudentIdAndStatus(studentId, "IN_PROGRESS");
        int totalCredits = 0;
        for (Enrollment enrollment : activeEnrollments) {
            totalCredits += enrollment.getCourse().getCredits();
        }
        return totalCredits;
    }

    public boolean canAddMoreCourses(Long studentId) {
        int currentCredits = getTotalCreditsEnrolled(studentId);
        int maxCredits = 18;
        return currentCredits < maxCredits;
    }

    private String calculateAcademicStanding(double gpa) {
        if (gpa >= 3.5) {
            return "Dean's List";
        } else if (gpa >= 3.0) {
            return "Good Standing";
        } else if (gpa >= 2.0) {
            return "Satisfactory";
        } else {
            return "Academic Warning";
        }
    }
}
