package com.improved_enrollment_system.improved_enrollment_system.controller;

import com.improved_enrollment_system.improved_enrollment_system.entity.Course;
import com.improved_enrollment_system.improved_enrollment_system.entity.Enrollment;
import com.improved_enrollment_system.improved_enrollment_system.entity.Student;
import com.improved_enrollment_system.improved_enrollment_system.repository.EnrollmentRepository;
import com.improved_enrollment_system.improved_enrollment_system.repository.StudentRepository;
import com.improved_enrollment_system.improved_enrollment_system.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;

    public CourseController(CourseService courseService,
                            EnrollmentRepository enrollmentRepository,
                            StudentRepository studentRepository) {
        this.courseService = courseService;
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
    }


    @GetMapping
    public List<Course> getAllCourses() {
        System.out.println("📚 Fetching all courses...");
        List<Course> courses = courseService.getAllCourses();
        System.out.println("✅ Found " + courses.size() + " courses");
        return courses;
    }

    @GetMapping("/search")
    public List<Course> searchCourses(@RequestParam String query) {
        System.out.println("🔍 Searching for: '" + query + "'");

        List<Course> allCourses = courseService.getAllCourses();

        String cleanQuery = query.toLowerCase()
                .replace("it ", "")
                .replace("it", "")
                .trim();

        String originalQuery = query.toLowerCase().trim();

        List<Course> results = allCourses.stream()
                .filter(course -> {
                    String code = course.getCode().toLowerCase();
                    String title = course.getTitle().toLowerCase();

                    boolean codeMatch = code.contains(cleanQuery)
                            || code.equals(cleanQuery)
                            || ("it" + code).contains(originalQuery)
                            || ("it " + code).contains(originalQuery);

                    boolean titleMatch = title.contains(originalQuery)
                            || title.contains(cleanQuery);

                    return codeMatch || titleMatch;
                })
                .toList();

        System.out.println("✅ Found " + results.size() + " matching courses");
        return results;
    }


    @GetMapping("/{id}")
    public Course getCourse(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<Map<String, Object>> enrollInCourse(
            @PathVariable Long courseId,
            @RequestParam Long studentId
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("📝 Enrollment request: studentId=" + studentId + ", courseId=" + courseId);

            Optional<Student> studentOpt = studentRepository.findById(studentId);
            if (studentOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Student not found");
                return ResponseEntity.ok(response);
            }

            Course course = courseService.getCourseById(courseId);
            if (course == null) {
                response.put("success", false);
                response.put("message", "Course not found");
                return ResponseEntity.ok(response);
            }

            Optional<Enrollment> existing = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
            if (existing.isPresent() &&
                    ("ENROLLED".equals(existing.get().getStatus()) ||
                            "IN_PROGRESS".equals(existing.get().getStatus()))) {
                response.put("success", false);
                response.put("message", "Already enrolled in this course");
                return ResponseEntity.ok(response);
            }

            long enrolledCount = enrollmentRepository.countByCourseIdAndStatus(courseId, "ENROLLED");
            if (enrolledCount >= course.getCapacity()) {
                response.put("success", false);
                response.put("message", "Course is full. Capacity: " + course.getCapacity());
                return ResponseEntity.ok(response);
            }

            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(studentOpt.get());
            enrollment.setCourse(course);
            enrollment.setStatus("ENROLLED");
            enrollment.setEnrollmentDate(LocalDateTime.now());

            enrollmentRepository.save(enrollment);

            System.out.println("✅ Enrolled successfully: " + enrollment.getId());

            response.put("success", true);
            response.put("message", "Successfully enrolled in IT " + course.getCode() + " " + course.getTitle());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Enrollment error: " + e.getMessage());
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping("/{courseId}/drop")
    public ResponseEntity<Map<String, Object>> dropCourse(
            @PathVariable Long courseId,
            @RequestParam Long studentId
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("🗑️ Drop request: studentId=" + studentId + ", courseId=" + courseId);

            Optional<Enrollment> enrollmentOpt =
                    enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);

            if (enrollmentOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Not enrolled in this course");
                return ResponseEntity.ok(response);
            }

            Enrollment enrollment = enrollmentOpt.get();

            if ("DROPPED".equals(enrollment.getStatus()) ||
                    "WITHDRAWN".equals(enrollment.getStatus())) {
                response.put("success", false);
                response.put("message", "Already dropped this course");
                return ResponseEntity.ok(response);
            }

            enrollment.setStatus("DROPPED");
            enrollmentRepository.save(enrollment);

            System.out.println("✅ Dropped successfully");

            response.put("success", true);
            response.put("message", "Successfully dropped course");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Drop error: " + e.getMessage());
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/student/{studentId}/enrolled")
    public List<Enrollment> getStudentEnrollments(@PathVariable Long studentId) {
        System.out.println("📋 Fetching enrollments for student: " + studentId);
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        System.out.println("✅ Found " + enrollments.size() + " enrollments");
        return enrollments;
    }


    @PostMapping
    public Course createCourse(@RequestParam Long catalogId, @RequestBody Course course) {
        return courseService.createCourse(catalogId, course);
    }


    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }
}
