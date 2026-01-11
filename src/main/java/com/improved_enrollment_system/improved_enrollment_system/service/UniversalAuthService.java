package com.improved_enrollment_system.improved_enrollment_system.service;

import com.improved_enrollment_system.improved_enrollment_system.dto.LoginResult;
import com.improved_enrollment_system.improved_enrollment_system.dto.LogoutResult;
import com.improved_enrollment_system.improved_enrollment_system.entity.Administrator;
import com.improved_enrollment_system.improved_enrollment_system.entity.Advisor;
import com.improved_enrollment_system.improved_enrollment_system.entity.Student;
import com.improved_enrollment_system.improved_enrollment_system.entity.User;
import com.improved_enrollment_system.improved_enrollment_system.entity.UserSession;
import com.improved_enrollment_system.improved_enrollment_system.repository.AdministratorRepository;
import com.improved_enrollment_system.improved_enrollment_system.repository.AdvisorRepository;
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
    private final AdvisorRepository advisorRepository;
    private final AdministratorRepository administratorRepository;

    public UniversalAuthService(UserRepository userRepository,
                                UserSessionRepository sessionRepository,
                                StudentRepository studentRepository,
                                AdvisorRepository advisorRepository,
                                AdministratorRepository administratorRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.studentRepository = studentRepository;
        this.advisorRepository = advisorRepository;
        this.administratorRepository = administratorRepository;
    }

    public LoginResult createAccount(String username, String password, String email, String userType) {
        try {
            System.out.println("📝 Creating account: " + email + " as " + userType);

            // Check if email already exists
            Optional<User> existingByEmail = userRepository.findByEmail(email);
            if (existingByEmail.isPresent()) {
                return new LoginResult(false, "Email already registered");
            }

            // Create the appropriate user type
            User newUser;

            if ("ADMINISTRATOR".equalsIgnoreCase(userType) || "Administrator".equalsIgnoreCase(userType)) {
                // Create Administrator
                Administrator admin = new Administrator();
                admin.setUsername(username);
                admin.setEmail(email);
                admin.setPassword(password);
                admin.setRole("ADMINISTRATOR");
                newUser = administratorRepository.save(admin);
                System.out.println("✅ Administrator account created: " + email);

            } else if ("ADVISOR".equalsIgnoreCase(userType) || "Advisor".equalsIgnoreCase(userType)) {
                // Create Advisor
                Advisor advisor = new Advisor();
                advisor.setUsername(username);
                advisor.setEmail(email);
                advisor.setPassword(password);
                advisor.setRole("ADVISOR");
                advisor.setDepartment("General");
                newUser = advisorRepository.save(advisor);
                System.out.println("✅ Advisor account created: " + email);

            } else {
                // Default to Student
                Student student = new Student();
                student.setUsername(username);
                student.setEmail(email);
                student.setPassword(password);
                student.setRole("STUDENT");
                student.setGpa(0.0);
                student.setMajor("Undeclared");
                student.setStudYear("Freshman");
                newUser = studentRepository.save(student);
                System.out.println("✅ Student account created: " + email);
            }

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
            System.out.println("✅ User found: " + email + " (Type: " + user.getClass().getSimpleName() + ")");

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