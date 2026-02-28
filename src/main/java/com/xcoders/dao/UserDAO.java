package com.xcoders.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.xcoders.DBConnection;
import com.xcoders.model.User;

/**
 * Data-Access Object for the {@code users} table.
 * All database interaction for users is centralised here.
 */
public class UserDAO {

    // ── Validate (login) ───────────────────────────────────

    /**
     * Looks up a user by email and password.
     *
     * @return the matching {@link User}, or {@code null} if credentials are invalid
     *         or the account is blocked.
     */
    public User validateUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ? AND status = 'ACTIVE'";

        try {
            Connection conn = DBConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, email);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return extractUser(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validating user: " + e.getMessage());
        }

        return null;
    }

    // ── Register ───────────────────────────────────────────

    /**
     * Inserts a new user row.
     *
     * @return {@code true} if the row was inserted, {@code false} otherwise.
     */
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password, role, status) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, user.getName());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getPassword());
                ps.setString(4, user.getRole());
                ps.setString(5, user.getStatus());

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
        }

        return false;
    }

    // ── Email-exists check ─────────────────────────────────

    /**
     * Checks whether an email address is already registered.
     *
     * @return {@code true} if the email exists in the database.
     */
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";

        try {
            Connection conn = DBConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, email);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
        }

        return false;
    }

    // ── Helper ─────────────────────────────────────────────

    /**
     * Maps the current row of a {@link ResultSet} to a {@link User} object.
     * Keeps ResultSet handling private to the DAO layer.
     */
    private User extractUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("role"),
                rs.getString("status"),
                rs.getTimestamp("created_at")
        );
    }
}
