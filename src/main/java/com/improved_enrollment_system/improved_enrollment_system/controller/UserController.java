package com.improved_enrollment_system.improved_enrollment_system.controller;

import com.improved_enrollment_system.improved_enrollment_system.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public boolean logout(String sessionId) {
        try {
            return userService.clearUserSession(sessionId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteAccount(Long userId, String password) {
        try {
            // Verify password first
            if (!userService.verifyPassword(userId, password)) {
                return false;
            }

            return userService.deleteUser(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public double getCurrentGPA(Long userId) {
        try {
            return userService.getGPA(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}
