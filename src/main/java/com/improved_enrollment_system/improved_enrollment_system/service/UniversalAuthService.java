package com.improved_enrollment_system.improved_enrollment_system.service;

import com.improved_enrollment_system.improved_enrollment_system.dto.LoginResult;
import com.improved_enrollment_system.improved_enrollment_system.dto.LogoutResult;
import com.improved_enrollment_system.improved_enrollment_system.entity.Student;
import com.improved_enrollment_system.improved_enrollment_system.entity.User;
import com.improved_enrollment_system.improved_enrollment_system.entity.UserSession;
import com.improved_enrollment_system.improved_enrollment_system.repository.StudentRepository;
import com.improved_enrollment_system.improved_enrollment_system.repository.UserRepository;
import com.improved_enrollment_system.improved_enrollment_system.repository.UserSessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UniversalAuthService {

    private final UserRepository userRepository;
    private final UserSessionRepository sessionRepository;
    private final StudentRepository studentRepository;

    public UniversalAuthService(UserRepository userRepository,
                                UserSessionRepository sessionRepository,
                                StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.studentRepository = studentRepository;
    }

    public LoginResult createAccount(String username, String password, String email, String userType) {
        try {
            Optional<User> existingByEmail = userRepository.findByEmail(email);
            if (existingByEmail.isPresent()) {
                return new LoginResult(false, "Email already registered");
            }

            Student student = new Student();
            student.setUsername(username);
            student.setEmail(email);
            student.setPassword(password);
            student.setGpa(0.0);
            student.setMajor("Undeclared");
            student.setStudYear("Freshman");

            studentRepository.save(student);

            System.out.println("✅ Account created: " + email);
            return new LoginResult(true, "Account created successfully! You can now login.");

        } catch (Exception e) {
            System.err.println("❌ Error creating account: " + e.getMessage());
            e.printStackTrace();
            return new LoginResult(false, "Error: " + e.getMessage());
        }
    }

    public LoginResult login(String email, String password) {
        try {
            System.out.println("🔍 Login attempt for: " + email);

            Optional<User> userOpt = userRepository.findByEmail(email);

            if (!userOpt.isPresent()) {
                System.out.println("❌ User not found: " + email);
                return new LoginResult(false, "Email not found. Please create an account.");
            }

            User user = userOpt.get();
            System.out.println("✅ User found: " + email);

            String storedPassword = user.getPassword();
            if (storedPassword == null || !storedPassword.equals(password)) {
                System.out.println("❌ Wrong password for: " + email);
                return new LoginResult(false, "Incorrect password");
            }

            System.out.println("✅ Password correct for: " + email);

            closeExistingSessions(user.getId());

            String token = UUID.randomUUID().toString();

            UserSession session = new UserSession();
            session.setUser(user);
            session.setSessionToken(token);
            session.setLoginTime(LocalDateTime.now());
            session.setIsActive(true);
            sessionRepository.save(session);

            System.out.println("✅ Session created: " + token);

            String userType = determineUserType(user);
            System.out.println("✅ User type: " + userType);

            LoginResult result = new LoginResult(true, "Login successful!");
            result.setSessionToken(token);
            result.setUserId(user.getId());
            result.setUsername(getUserDisplayName(user));

            return result;

        } catch (Exception e) {
            System.err.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();
            return new LoginResult(false, "Login failed: " + e.getMessage());
        }
    }

    public LogoutResult logout(String sessionToken) {
        try {
            Optional<UserSession> sessionOpt = sessionRepository.findBySessionToken(sessionToken);

            if (!sessionOpt.isPresent()) {
                return new LogoutResult(false, "Invalid session");
            }

            UserSession session = sessionOpt.get();
            session.setIsActive(false);
            session.setLogoutTime(LocalDateTime.now());
            sessionRepository.save(session);

            return new LogoutResult(true, "Logged out successfully!");

        } catch (Exception e) {
            return new LogoutResult(false, "Logout failed: " + e.getMessage());
        }
    }

    public boolean isLoggedIn(String sessionToken) {
        Optional<UserSession> session = sessionRepository.findBySessionToken(sessionToken);
        return session.isPresent() && session.get().getIsActive();
    }

    public User getCurrentUser(String sessionToken) {
        Optional<UserSession> session = sessionRepository.findBySessionToken(sessionToken);
        return session.map(UserSession::getUser).orElse(null);
    }

    public String getUserType(String sessionToken) {
        User user = getCurrentUser(sessionToken);
        if (user == null) return "UNKNOWN";
        return determineUserType(user);
    }

    private void closeExistingSessions(Long userId) {
        var activeSessions = sessionRepository.findByUserIdAndIsActive(userId, true);
        for (UserSession session : activeSessions) {
            session.setIsActive(false);
            session.setLogoutTime(LocalDateTime.now());
            sessionRepository.save(session);
        }
    }

    private String determineUserType(User user) {
        String className = user.getClass().getSimpleName();

        if (className.equals("Student")) {
            return "STUDENT";
        } else if (className.equals("Advisor")) {
            return "ADVISOR";
        } else if (className.equals("Administrator")) {
            return "ADMINISTRATOR";
        } else {
            return "USER";
        }
    }

    private String getUserDisplayName(User user) {
        return user.getUsername();
    }

}
