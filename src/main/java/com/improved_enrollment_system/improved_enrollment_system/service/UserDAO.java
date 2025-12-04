package com.improved_enrollment_system.improved_enrollment_system.service;

import java.sql.*;

public class UserDAO {
    private Connection getConnection() throws SQLException {
        // Database connection details
        String url = "jdbc:mysql://localhost:3306/registration_db";
        String username = "root";
        String password = "password";
        return DriverManager.getConnection(url, username, password);
    }
    
    /**
     * Gets stored password for user
     */
    public String getPassword(String userId) {
        String query = "SELECT password FROM users WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Deletes user from database
     */
    public boolean deleteUser(String userId) {
        String query = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deletes user enrollments (for cascade delete)
     */
    public void deleteUserEnrollments(String userId) {
        String query = "DELETE FROM enrollments WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, userId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Gets user's current GPA
     */
    public double getUserGPA(String userId) {
        String query = "SELECT gpa FROM student_info WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("gpa");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}