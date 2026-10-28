package com.hotelreservation.model;

/**
 * Enumeration representing the complete lifecycle status of a hotel reservation.
 *
 * Status Lifecycle and Transitions:
 * - CONFIRMED: Initial status upon reservation. Occupies the room for the stay duration.
 *   Permitted transitions: CHECKED_IN, CANCELLED.
 * - CHECKED_IN: Guest has arrived and checked into the room. Occupies the room.
 *   Permitted transitions: CHECKED_OUT.
 * - CHECKED_OUT: Guest has completed their stay. Room is released. Terminal state.
 *   Permitted transitions: None.
 * - CANCELLED: Reservation was cancelled by customer or administrator. Room is released. Terminal state.
 *   Permitted transitions: None.
 */
public enum BookingStatus {
    CONFIRMED("Confirmed", "Active reservation, awaiting guest check-in", true),
    CHECKED_IN("Checked In", "Guest currently residing in hotel", true),
    CHECKED_OUT("Checked Out", "Stay completed, bill settled", false),
    CANCELLED("Cancelled", "Reservation cancelled by guest or administrator", false);

    private final String displayName;
    private final String description;
    private final boolean occupiesRoom;

    BookingStatus(String displayName, String description, boolean occupiesRoom) {
        this.displayName = displayName;
        this.description = description;
        this.occupiesRoom = occupiesRoom;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Indicates whether this status occupies the room on the calendar,
     * triggering availability conflict detection.
     */
    public boolean occupiesRoom() {
        return occupiesRoom;
    }

    /**
     * Validates whether a state transition from this status to targetStatus is permitted.
     */
    public boolean canTransitionTo(BookingStatus targetStatus) {
        if (targetStatus == null) {
            return false;
        }
        if (this == targetStatus) {
            return true;
        }
        return switch (this) {
            case CONFIRMED -> targetStatus == CHECKED_IN || targetStatus == CANCELLED;
            case CHECKED_IN -> targetStatus == CHECKED_OUT;
            case CHECKED_OUT, CANCELLED -> false;
        };
    }

    public static BookingStatus fromString(String text) {
        if (text == null) {
            return CONFIRMED;
        }
        for (BookingStatus status : BookingStatus.values()) {
            if (status.name().equalsIgnoreCase(text.trim())) {
                return status;
            }
        }
        return CONFIRMED;
    }
}

