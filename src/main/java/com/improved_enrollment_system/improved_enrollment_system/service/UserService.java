package com.improved_enrollment_system.improved_enrollment_system.service;

import com.improved_enrollment_system.improved_enrollment_system.entity.Enrollment;
import com.improved_enrollment_system.improved_enrollment_system.entity.Student;
import com.improved_enrollment_system.improved_enrollment_system.entity.User;
import com.improved_enrollment_system.improved_enrollment_system.entity.UserSession;
import com.improved_enrollment_system.improved_enrollment_system.repository.EnrollmentRepository;
import com.improved_enrollment_system.improved_enrollment_system.repository.UserRepository;
import com.improved_enrollment_system.improved_enrollment_system.repository.UserSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserSessionRepository sessionRepository;
    private final EnrollmentRepository enrollmentRepository;

    public UserService(UserRepository userRepository,
                       UserSessionRepository sessionRepository,
                       EnrollmentRepository enrollmentRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public boolean clearUserSession(String sessionToken) {
        Optional<UserSession> sessionOpt = sessionRepository.findBySessionToken(sessionToken);
        if (!sessionOpt.isPresent()) {
            return false;
        }
        sessionRepository.delete(sessionOpt.get());
        return true;
    }



    public boolean verifyPassword(Long userId, String password) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }
        User user = userOpt.get();
        return password != null && password.equals(user.getPassword());
    }
    
    /**
     * Deletes user and related data
     */
    @Transactional
    public boolean deleteUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }
        if (userOpt.get() instanceof Student) {
            List<Enrollment> enrollments = enrollmentRepository.findByStudentId(userId);
            enrollmentRepository.deleteAll(enrollments);
        }
        List<UserSession> sessions = sessionRepository.findByUserId(userId);
        if (!sessions.isEmpty()) {
            sessionRepository.deleteAll(sessions);
        }
        userRepository.delete(userOpt.get());
        return true;
    }


    public double getGPA(Long userId) {
        return userRepository.findById(userId)
                .filter(Student.class::isInstance)
                .map(Student.class::cast)
                .map(Student::getGpa)
                .orElse(0.0);
    }
}