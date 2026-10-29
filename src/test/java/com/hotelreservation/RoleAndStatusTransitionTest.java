package com.hotelreservation;

import com.hotelreservation.model.BookingStatus;
import com.hotelreservation.model.Role;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Automated Unit Tests for user authorization roles and reservation lifecycle status transitions.
 */
public class RoleAndStatusTransitionTest {

    @Test
    public void testRoleResolution() {
        assertEquals(Role.CUSTOMER, Role.fromString("CUSTOMER"));
        assertEquals(Role.ADMIN, Role.fromString("ADMIN"));
        assertEquals(Role.HOTEL, Role.fromString("HOTEL"));
        assertEquals(Role.CUSTOMER, Role.fromString("unknown"));
        assertEquals(Role.CUSTOMER, Role.fromString(null));
    }

    @Test
    public void testStatusRoomOccupancyFlag() {
        // Only active stays occupy the room on the calendar
        assertTrue("CONFIRMED reservations occupy room", BookingStatus.CONFIRMED.occupiesRoom());
        assertTrue("CHECKED_IN guests occupy room", BookingStatus.CHECKED_IN.occupiesRoom());

        // Released states do NOT occupy the room
        assertFalse("CHECKED_OUT stays do not occupy room", BookingStatus.CHECKED_OUT.occupiesRoom());
        assertFalse("CANCELLED reservations do not occupy room", BookingStatus.CANCELLED.occupiesRoom());
    }

    @Test
    public void testPermittedStatusTransitions() {
        // CONFIRMED transitions
        assertTrue("CONFIRMED can transition to CHECKED_IN", BookingStatus.CONFIRMED.canTransitionTo(BookingStatus.CHECKED_IN));
        assertTrue("CONFIRMED can transition to CANCELLED", BookingStatus.CONFIRMED.canTransitionTo(BookingStatus.CANCELLED));
        assertFalse("CONFIRMED cannot skip straight to CHECKED_OUT", BookingStatus.CONFIRMED.canTransitionTo(BookingStatus.CHECKED_OUT));

        // CHECKED_IN transitions
        assertTrue("CHECKED_IN can transition to CHECKED_OUT", BookingStatus.CHECKED_IN.canTransitionTo(BookingStatus.CHECKED_OUT));
        assertFalse("CHECKED_IN cannot transition to CANCELLED", BookingStatus.CHECKED_IN.canTransitionTo(BookingStatus.CANCELLED));
        assertFalse("CHECKED_IN cannot revert to CONFIRMED", BookingStatus.CHECKED_IN.canTransitionTo(BookingStatus.CONFIRMED));

        // Terminal states
        assertFalse("CHECKED_OUT is terminal", BookingStatus.CHECKED_OUT.canTransitionTo(BookingStatus.CONFIRMED));
        assertFalse("CHECKED_OUT is terminal", BookingStatus.CHECKED_OUT.canTransitionTo(BookingStatus.CANCELLED));
        assertFalse("CANCELLED is terminal", BookingStatus.CANCELLED.canTransitionTo(BookingStatus.CONFIRMED));
        assertFalse("CANCELLED is terminal", BookingStatus.CANCELLED.canTransitionTo(BookingStatus.CHECKED_IN));
    }
}
