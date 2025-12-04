package com.improved_enrollment_system.improved_enrollment_system.service;

import com.improved_enrollment_system.improved_enrollment_system.dto.*;
import com.improved_enrollment_system.improved_enrollment_system.entity.*;
import com.improved_enrollment_system.improved_enrollment_system.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CatalogRepository catalogRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final WaitlistRepository waitlistRepository;
    private final CoursePrerequisiteRepository prerequisiteRepository;

    public CourseService(CourseRepository courseRepository,
                         CatalogRepository catalogRepository,
                         StudentRepository studentRepository,
                         EnrollmentRepository enrollmentRepository,
                         WaitlistRepository waitlistRepository,
                         CoursePrerequisiteRepository prerequisiteRepository) {
        this.courseRepository = courseRepository;
        this.catalogRepository = catalogRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.waitlistRepository = waitlistRepository;
        this.prerequisiteRepository = prerequisiteRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    public List<Course> getCoursesForCatalog(Long catalogId) {
        return courseRepository.findByCatalogId(catalogId);
    }

    public Course createCourse(Long catalogId, Course course) {
        Catalog catalog = catalogRepository.findById(catalogId)
                .orElseThrow(() -> new RuntimeException("Catalog not found"));
        catalog.addCourse(course);
        return courseRepository.save(course);
    }

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public PrerequisiteResult checkPrerequisites(Long studentId, Long courseId) {
        try {
            Course course = courseRepository.findById(courseId).orElse(null);
            if (course == null) {
                return new PrerequisiteResult(false, false, "Course not found");
            }

            Student student = studentRepository.findById(studentId).orElse(null);
            if (student == null) {
                return new PrerequisiteResult(false, false, "Student not found");
            }

            List<CoursePrerequisite> prerequisites = prerequisiteRepository.findByCourseId(courseId);

            if (prerequisites.isEmpty()) {
                PrerequisiteResult result = new PrerequisiteResult(true, true, "No prerequisites required for this course");
                result.setCourseCode(course.getCode());
                result.setCourseTitle(course.getTitle());
                return result;
            }

            List<Enrollment> completedEnrollments = enrollmentRepository
                    .findByStudentIdAndStatus(studentId, "COMPLETED");

            Set<Long> completedCourseIds = completedEnrollments.stream()
                    .map(e -> e.getCourse().getId())
                    .collect(Collectors.toSet());

            List<String> missingPrereqs = new ArrayList<>();
            for (CoursePrerequisite prereq : prerequisites) {
                if (!completedCourseIds.contains(prereq.getPrerequisiteCourse().getId())) {
                    missingPrereqs.add(prereq.getPrerequisiteCourse().getCode() +
                            " - " + prereq.getPrerequisiteCourse().getTitle());
                }
            }

            PrerequisiteResult result;
            if (missingPrereqs.isEmpty()) {
                result = new PrerequisiteResult(true, true, "Student meets all prerequisites");
            } else {
                result = new PrerequisiteResult(true, false, "Missing prerequisites: " + String.join(", ", missingPrereqs));
            }

            result.setCourseCode(course.getCode());
            result.setCourseTitle(course.getTitle());
            return result;

        } catch (Exception e) {
            return new PrerequisiteResult(false, false, "Error: " + e.getMessage());
        }
    }

    public WaitlistResult joinWaitlist(Long studentId, Long courseId) {
        try {
            Course course = courseRepository.findById(courseId).orElse(null);
            if (course == null) {
                return new WaitlistResult(false, "Course not found");
            }

            Student student = studentRepository.findById(studentId).orElse(null);
            if (student == null) {
                return new WaitlistResult(false, "Student not found");
            }

            Optional<Waitlist> existing = waitlistRepository
                    .findByStudentIdAndCourseIdAndStatus(studentId, courseId, "WAITING");

            if (existing.isPresent()) {
                WaitlistResult result = new WaitlistResult(false, "Already on waitlist");
                result.setCourseCode(course.getCode());
                result.setPosition(existing.get().getPosition());
                return result;
            }

            Optional<Enrollment> enrolled = enrollmentRepository
                    .findByStudentIdAndCourseId(studentId, courseId);

            if (enrolled.isPresent() &&
                    ("ENROLLED".equals(enrolled.get().getStatus()) ||
                            "IN_PROGRESS".equals(enrolled.get().getStatus()))) {
                return new WaitlistResult(false, "Already enrolled in this course");
            }

            List<Waitlist> existingWaitlist = waitlistRepository
                    .findByCourseIdAndStatus(courseId, "WAITING");
            int position = existingWaitlist.size() + 1;

            Waitlist waitlist = new Waitlist();
            waitlist.setStudent(student);
            waitlist.setCourse(course);
            waitlist.setPosition(position);
            waitlist.setStatus("WAITING");
            waitlist.setJoinedDate(LocalDateTime.now());

            waitlistRepository.save(waitlist);

            WaitlistResult result = new WaitlistResult(true, "Successfully added to waitlist");
            result.setCourseCode(course.getCode());
            result.setPosition(position);

            return result;

        } catch (Exception e) {
            return new WaitlistResult(false, "Error: " + e.getMessage());
        }
    }

    public List<ScheduleConflict> displayConflicts(Long studentId, Long newCourseId) {
        List<ScheduleConflict> conflicts = new ArrayList<>();

        try {
            Course newCourse = courseRepository.findById(newCourseId).orElse(null);
            if (newCourse == null) {
                return conflicts;
            }

            List<Enrollment> enrollments = enrollmentRepository
                    .findByStudentIdAndStatus(studentId, "IN_PROGRESS");

            for (Enrollment enrollment : enrollments) {
                Course enrolledCourse = enrollment.getCourse();

                boolean hasConflict = checkScheduleOverlap(
                        getSchedule(newCourse),
                        getSchedule(enrolledCourse)
                );

                if (hasConflict) {
                    ScheduleConflict conflict = new ScheduleConflict(
                            newCourse.getCode(),
                            newCourse.getTitle(),
                            getSchedule(newCourse),
                            enrolledCourse.getCode(),
                            enrolledCourse.getTitle(),
                            getSchedule(enrolledCourse),
                            "Time overlap detected"
                    );
                    conflicts.add(conflict);
                }
            }

        } catch (Exception e) {}

        return conflicts;
    }

    public WithdrawResult withdrawFromCourse(Long studentId, Long courseId) {
        try {
            Course course = courseRepository.findById(courseId).orElse(null);
            if (course == null) {
                return new WithdrawResult(false, "Course not found");
            }

            Student student = studentRepository.findById(studentId).orElse(null);
            if (student == null) {
                return new WithdrawResult(false, "Student not found");
            }

            Optional<Enrollment> enrollmentOpt = enrollmentRepository
                    .findByStudentIdAndCourseId(studentId, courseId);

            if (!enrollmentOpt.isPresent()) {
                return new WithdrawResult(false, "Not enrolled in this course");
            }

            Enrollment enrollment = enrollmentOpt.get();

            if ("WITHDRAWN".equals(enrollment.getStatus())) {
                return new WithdrawResult(false, "Already withdrawn from this course");
            }

            enrollment.setStatus("WITHDRAWN");
            enrollmentRepository.save(enrollment);

            processWaitlistForCourse(courseId);

            WithdrawResult result = new WithdrawResult(true, "Successfully withdrawn from course");
            result.setCourseCode(course.getCode());

            return result;

        } catch (Exception e) {
            return new WithdrawResult(false, "Error: " + e.getMessage());
        }
    }

    public boolean isCourseFull(Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return false;
        }

        long enrolledCount = enrollmentRepository.countByCourseIdAndStatus(courseId, "IN_PROGRESS");

        return enrolledCount >= course.getCapacity();
    }

    public int getAvailableSeats(Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return 0;
        }

        long enrolledCount = enrollmentRepository.countByCourseIdAndStatus(courseId, "IN_PROGRESS");
        int available = course.getCapacity() - (int) enrolledCount;

        return Math.max(0, available);
    }

    private void processWaitlistForCourse(Long courseId) {
        try {
            if (isCourseFull(courseId)) {
                return;
            }

            List<Waitlist> waitlist = waitlistRepository
                    .findByCourseIdOrderByPositionAsc(courseId);

            if (!waitlist.isEmpty()) {
                Waitlist firstInLine = waitlist.get(0);

                Enrollment enrollment = new Enrollment();
                enrollment.setStudent(firstInLine.getStudent());
                enrollment.setCourse(firstInLine.getCourse());
                enrollment.setStatus("IN_PROGRESS");
                enrollment.setEnrollmentDate(LocalDateTime.now());
                enrollmentRepository.save(enrollment);

                firstInLine.setStatus("ENROLLED");
                waitlistRepository.save(firstInLine);

                for (int i = 1; i < waitlist.size(); i++) {
                    Waitlist w = waitlist.get(i);
                    w.setPosition(i);
                    waitlistRepository.save(w);
                }
            }
        } catch (Exception e) {}
    }

    private String getSchedule(Course course) {
        try {
            java.lang.reflect.Method method = course.getClass().getMethod("getSchedule");
            Object result = method.invoke(course);
            return result != null ? result.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }

    private boolean checkScheduleOverlap(String schedule1, String schedule2) {
        if (schedule1 == null || schedule1.isEmpty() ||
                schedule2 == null || schedule2.isEmpty()) {
            return false;
        }

        String days1 = schedule1.split(" ")[0];
        String days2 = schedule2.split(" ")[0];

        boolean daysOverlap = false;
        for (char day : days1.toCharArray()) {
            if (days2.indexOf(day) >= 0) {
                daysOverlap = true;
                break;
            }
        }

        if(!daysOverlap) {
            return false;
        }

        return true;
    }
}
