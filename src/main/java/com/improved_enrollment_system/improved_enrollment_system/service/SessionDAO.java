package com.improved_enrollment_system.improved_enrollment_system.service;

import java.sql.*;

public class SessionDAO {
    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/registration_db";
        String username = "root";
        String password = "password";
        return DriverManager.getConnection(url, username, password);
    }
    
    /**
     * Deletes a specific session
     */
    public boolean deleteSession(String sessionId) {
        String query = "DELETE FROM sessions WHERE session_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, sessionId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deletes all sessions for a user
     */
    public void deleteUserSessions(String userId) {
        String query = "DELETE FROM sessions WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, userId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}