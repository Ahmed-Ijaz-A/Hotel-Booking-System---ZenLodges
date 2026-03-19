package com.xcoders.model;

/**
 * Enum representing user roles in the ZenLodges system.
 */
public enum Role {
    PLATFORM_ADMIN("Platform Admin"),
    HOTEL_ADMIN("Hotel Admin"),
    USER("User");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Convert string value to Role enum
     */
    public static Role fromString(String value) {
        if (value == null) {
            return USER;
        }
        try {
            return Role.valueOf(value.toUpperCase());
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
