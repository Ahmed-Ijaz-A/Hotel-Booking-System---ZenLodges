package com.xcoders.model;

/**
 * Enum representing user roles in the ZenLodges system.
 * 
 * USER role = Guest user who can search, view, and book rooms
 * HOTEL_ADMIN role = Admin who manages a specific hotel
 * PLATFORM_ADMIN role = Admin who manages the entire platform
 */
public enum Role {
    PLATFORM_ADMIN("Platform Admin"),
    HOTEL_ADMIN("Hotel Admin"),
    USER("Guest");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
        * Convert string value to a Role enum.
     */
    public static Role fromString(String value) {
        if (value == null) {
            return USER;
        }
        String normalized = value.trim().toUpperCase();
        if ("GUEST".equals(normalized)) {
            return USER;
        }
        if ("ADMIN".equals(normalized)) {
            return PLATFORM_ADMIN;
        }
        try {
            return Role.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return USER;
        }
    }

    /**
     * Get all role display names (for UI dropdowns)
     */
    public static String[] getAllDisplayNames() {
        return new String[]{
            PLATFORM_ADMIN.displayName,
            HOTEL_ADMIN.displayName,
            USER.displayName
        };
    }
}
