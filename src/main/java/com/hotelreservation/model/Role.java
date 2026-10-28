package com.hotelreservation.model;

/**
 * Enumeration representing user authorization roles in the Hotel Reservation System.
 */
public enum Role {
    CUSTOMER("Customer"),
    ADMIN("Administrator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Role fromString(String text) {
        if (text == null) {
            return CUSTOMER;
        }
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(text.trim())) {
                return role;
            }
        }
        return CUSTOMER;
    }
}

