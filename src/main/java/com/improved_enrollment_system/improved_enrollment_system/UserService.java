package src.user.Service;
import src.user.Dao.UserDAO;
import src.user.Dao.SessionDAO;

public class UserService {
    private UserDAO userDAO;
    private SessionDAO sessionDAO;
    
    public UserService() {
        this.userDAO = new UserDAO();
        this.sessionDAO = new SessionDAO();
    }
    
    /**
     * Clears user session from database
     */
    public boolean clearUserSession(String sessionId) {
        return sessionDAO.deleteSession(sessionId);
    }
    
    /**
     * Verifies user password
     */
    public boolean verifyPassword(String userId, String password) {
        String storedPassword = userDAO.getPassword(userId);
        // In production, use proper password hashing (BCrypt, etc.)
        return password.equals(storedPassword);
    }
    
    /**
     * Deletes user and related data
     */
    public boolean deleteUser(String userId) {
        // Delete user enrollments first (cascade)
        userDAO.deleteUserEnrollments(userId);
        
        // Delete user sessions
        sessionDAO.deleteUserSessions(userId);
        
        // Delete user account
        return userDAO.deleteUser(userId);
    }
    
    /**
     * Retrieves user's GPA
     */
    public double getGPA(String userId) {
        return userDAO.getUserGPA(userId);
    }
}