package com.hotelreservation.model;

/**
 * Enumeration representing available hotel room categories.
 */
public enum RoomType {
    STANDARD("Standard Room", 2),
    DELUXE("Deluxe Ocean View", 3),
    SUITE("Executive Luxury Suite", 4),
    FAMILY("Family Garden Suite", 6);

    private final String displayName;
    private final int defaultCapacity;

    RoomType(String displayName, int defaultCapacity) {
        this.displayName = displayName;
        this.defaultCapacity = defaultCapacity;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDefaultCapacity() {
        return defaultCapacity;
    }

    public static RoomType fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return STANDARD;
        }
        for (RoomType type : RoomType.values()) {
            if (type.name().equalsIgnoreCase(text.trim()) || type.displayName.equalsIgnoreCase(text.trim())) {
                return type;
            }
        }
        return STANDARD;
    }
}

