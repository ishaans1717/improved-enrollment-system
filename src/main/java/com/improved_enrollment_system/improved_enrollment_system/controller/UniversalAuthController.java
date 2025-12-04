package com.improved_enrollment_system.improved_enrollment_system.controller;

import com.improved_enrollment_system.improved_enrollment_system.dto.LoginResult;
import com.improved_enrollment_system.improved_enrollment_system.dto.LogoutResult;
import com.improved_enrollment_system.improved_enrollment_system.entity.User;
import com.improved_enrollment_system.improved_enrollment_system.service.UniversalAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class UniversalAuthController {

    private final UniversalAuthService authService;

    public UniversalAuthController(UniversalAuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<LoginResult> register(@RequestBody RegisterRequest request) {
        LoginResult result = authService.createAccount(
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getUserType()
        );
        return ResponseEntity.ok(result);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResult> login(@RequestBody LoginRequest request) {
        LoginResult result = authService.login(
                request.getEmail(),
                request.getPassword()
        );
        return ResponseEntity.ok(result);
    }


    @PostMapping("/logout")
    public ResponseEntity<LogoutResult> logout(@RequestBody LogoutRequest request) {
        LogoutResult result = authService.logout(request.getToken());
        return ResponseEntity.ok(result);
    }


    @GetMapping("/me")
    public ResponseEntity<UserInfo> getCurrentUser(@RequestParam String token) {
        User user = authService.getCurrentUser(token);

        if (user == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        String userType = authService.getUserType(token);

        UserInfo info = new UserInfo();
        info.setId(user.getId());
        info.setUserType(userType);
        info.setLoggedIn(true);

        return ResponseEntity.ok(info);
    }


    @GetMapping("/check")
    public ResponseEntity<Boolean> checkLogin(@RequestParam String token) {
        boolean isLoggedIn = authService.isLoggedIn(token);
        return ResponseEntity.ok(isLoggedIn);
    }


    public static class RegisterRequest {
        private String username;
        private String password;
        private String email;
        private String userType; // "STUDENT", "ADVISOR", "ADMINISTRATOR"

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
    }

    public static class LoginRequest{
        private String email;
        private String password;

        public String getEmail(){ return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LogoutRequest{
        private String token;

        public String getToken(){ return token; }
        public void setToken(String token) { this.token = token; }
    }

    public static class UserInfo{
        private Long id;
        private String userType;
        private boolean loggedIn;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }

        public boolean isLoggedIn() { return loggedIn; }
        public void setLoggedIn(boolean loggedIn) { this.loggedIn = loggedIn; }
    }
}