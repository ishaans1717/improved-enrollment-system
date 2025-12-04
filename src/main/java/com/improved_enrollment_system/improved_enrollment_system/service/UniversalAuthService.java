package com.improved_enrollment_system.improved_enrollment_system.service;

import com.improved_enrollment_system.improved_enrollment_system.dto.LoginResult;
import com.improved_enrollment_system.improved_enrollment_system.dto.LogoutResult;
import com.improved_enrollment_system.improved_enrollment_system.entity.Administrator;
import com.improved_enrollment_system.improved_enrollment_system.entity.Advisor;
import com.improved_enrollment_system.improved_enrollment_system.entity.Student;
import com.improved_enrollment_system.improved_enrollment_system.entity.User;
import com.improved_enrollment_system.improved_enrollment_system.entity.UserSession;
import com.improved_enrollment_system.improved_enrollment_system.repository.UserRepository;
import com.improved_enrollment_system.improved_enrollment_system.repository.UserSessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UniversalAuthService {

    private final UserRepository userRepository;
    private final UserSessionRepository sessionRepository;

    public UniversalAuthService(UserRepository userRepository,
                                UserSessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public LoginResult createAccount(String username, String password, String email, String userType) {
        try {
            if (username == null || username.isBlank()) {
                return new LoginResult(false, "Username is required");
            }
            if (email == null || email.isBlank()) {
                return new LoginResult(false, "Email is required");
            }
            if (password == null || password.isBlank()) {
                return new LoginResult(false, "Password is required");
            }

            Optional<User> existingByEmail = userRepository.findByEmail(email);
            if (existingByEmail.isPresent()) {
                return new LoginResult(false, "Email already registered");
            }
            User newUser = createUserForType(userType);
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setEmail(email);
            newUser.setRole(userType != null ? userType.toUpperCase() : "USER");

            userRepository.save(newUser);

            LoginResult result = new LoginResult(true, "Account created! You can now login.");
            result.setUserId(newUser.getId());
            result.setUsername(newUser.getUsername());
            return result;
        } catch (Exception e) {
            return new LoginResult(false, "Error: " + e.getMessage());
        }
    }

    public LoginResult login(String email, String password) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (!userOpt.isPresent()) {
                return new LoginResult(false, "User not found");
            }

            User user = userOpt.get();
            if (password == null || !password.equals(user.getPassword())) {
                return new LoginResult(false, "Invalid credentials");
            }

            deactivateActiveSessions(user);
            String token = UUID.randomUUID().toString();

            UserSession session = new UserSession();
            session.setUser(user);
            session.setSessionToken(token);
            session.setLoginTime(LocalDateTime.now());
            session.setIsActive(true);
            sessionRepository.save(session);


            LoginResult result = new LoginResult(true, "Login successful!");
            result.setSessionToken(token);
            result.setUserId(user.getId());
            result.setUsername(getUserDisplayName(user));

            return result;

        } catch (Exception e) {
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

    private User createUserForType(String userType) {
        if (userType == null) {
            return new User();
        }

        switch (userType.toUpperCase()) {
            case "STUDENT":
                return new Student();
            case "ADVISOR":
                return new Advisor();
            case "ADMINISTRATOR":
                return new Administrator();
            default:
                return new User();
        }
    }

    private void deactivateActiveSessions(User user) {
        List<UserSession> activeSessions = sessionRepository.findByUserIdAndIsActiveTrue(user.getId());
        if (activeSessions.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        for (UserSession session : activeSessions) {
            session.setIsActive(false);
            session.setLogoutTime(now);
        }
        sessionRepository.saveAll(activeSessions);
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
        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            return user.getUsername();
        }

        try {
            java.lang.reflect.Method method = user.getClass().getMethod("getName");
            Object result = method.invoke(user);
            if (result != null) return result.toString();
        } catch (Exception e) {
        }

        return determineUserType(user) + "_" + user.getId();
    }
}