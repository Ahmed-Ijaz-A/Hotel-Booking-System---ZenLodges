package com.xcoders;

import com.xcoders.model.User;

/**
 * Manages the current session (logged-in user).
 * Used across the application to access the current user's info.
 */
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Set the current logged-in user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("[SessionManager] User logged in: " + (user != null ? user.getName() : "null"));
    }

    /**
     * Get the current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Get current user ID (0 if not logged in)
     */
    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : 0;
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Clear the session (logout)
     */
    public void clearSession() {
        System.out.println("[SessionManager] User logged out: " + (currentUser != null ? currentUser.getName() : "none"));
        currentUser = null;
    }
}
