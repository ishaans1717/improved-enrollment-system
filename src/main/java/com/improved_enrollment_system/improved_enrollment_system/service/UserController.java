package com.improved_enrollment_system.improved_enrollment_system.service;

public class UserController {
    private UserService userService;
    
    public UserController() {
        this.userService = new UserService();
    }
    
    /**
     * Logs out the current user
     * @param sessionId - current session identifier
     * @return boolean indicating success
     */
    public boolean logout(String sessionId) {
        try {
            return userService.clearUserSession(sessionId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deletes user account
     * @param userId - user identifier
     * @param password - password for verification
     * @return boolean indicating success
     */
    public boolean deleteAccount(String userId, String password) {
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
    
    /**
     * Gets current GPA for user
     * @param userId - user identifier
     * @return GPA value
     */
    public double getCurrentGPA(String userId) {
        try {
            return userService.getGPA(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}