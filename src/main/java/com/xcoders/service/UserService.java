package com.xcoders.service;

import com.xcoders.dao.UserDAO;
import com.xcoders.model.User;

/**
 * Service layer for user-related operations.
 * Sits between controllers/UI and the DAO — contains business rules,
 * no SQL or UI logic.
 */
public class UserService {

    private final UserDAO userDAO = new UserDAO();

    // ── Login ──────────────────────────────────────────────

    /**
     * Authenticates a user by email and password.
     *
     * @return the authenticated {@link User}, or {@code null} if login fails.
     */
    public User login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            System.err.println("Login failed: email and password must not be empty.");
            return null;
        }

        User user = userDAO.validateUser(email.trim(), password);

        if (user == null) {
            System.err.println("Login failed: invalid credentials or account blocked.");
        }

        return user;
    }

    // ── Register ───────────────────────────────────────────

    /**
     * Registers a new GUEST user after validating inputs
     * and checking for duplicate emails.
     *
     * @return {@code true} if registration succeeded, {@code false} otherwise.
     */
    public boolean register(String name, String email, String password) {
        if (name == null || name.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()) {
            System.err.println("Registration failed: all fields are required.");
            return false;
        }

        if (userDAO.emailExists(email.trim())) {
            System.err.println("Registration failed: email already in use.");
            return false;
        }

        User newUser = new User(name.trim(), email.trim(), password, "GUEST", "ACTIVE");
        boolean created = userDAO.registerUser(newUser);

        if (!created) {
            System.err.println("Registration failed: could not save user.");
        }

        return created;
    }
}
