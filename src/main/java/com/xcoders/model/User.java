package com.xcoders.model;

import java.sql.Timestamp;

/**
 * Represents a user in the hotel booking system.
 * Maps to the {@code users} table.
 */
public class User {

    private int userId;
    private String name;
    private String email;
    private String password;
    private String role;       // "PLATFORM_ADMIN" | "HOTEL_ADMIN" | "USER"
    private String status;     // "ACTIVE" or "BLOCKED"
    private Timestamp createdAt;

    // ── No-arg constructor ──────────────────────────────────
    public User() { }

    // ── Full constructor (including generated fields) ───────
    public User(int userId, String name, String email, String password,
                String role, String status, Timestamp createdAt) {
        this.userId    = userId;
        this.name      = name;
        this.email     = email;
        this.password  = password;
        this.role      = role;
        this.status    = status;
        this.createdAt = createdAt;
    }

    // ── Constructor for creating a new user (no id / timestamp) ─
    public User(String name, String email, String password,
                String role, String status) {
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.role     = role;
        this.status   = status;
    }

    // ── Getters & Setters ──────────────────────────────────

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // ── Role Helper Methods ────────────────────────────────
    
    /**
     * Check if user is a Platform Admin
     */
    public boolean isPlatformAdmin() {
        if (role == null) {
            return false;
        }
        String normalizedRole = role.trim().toUpperCase();
        return "PLATFORM_ADMIN".equals(normalizedRole) || "ADMIN".equals(normalizedRole);
    }

    /**
     * Check if user is a Hotel Admin
     */
    public boolean isHotelAdmin() {
        if (role == null) {
            return false;
        }
        return "HOTEL_ADMIN".equals(role.trim().toUpperCase());
    }

    /**
     * Check if user is a regular User
     */
    public boolean isUser() {
        if (role == null) {
            return false;
        }
        String normalizedRole = role.trim().toUpperCase();
        return "USER".equals(normalizedRole) || "GUEST".equals(normalizedRole);
    }

    /**
     * Check if user is any type of admin
     */
    public boolean isAdmin() {
        return isPlatformAdmin() || isHotelAdmin();
    }

    // ── toString ───────────────────────────────────────────

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
