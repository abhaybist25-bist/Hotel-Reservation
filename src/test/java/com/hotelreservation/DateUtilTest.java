package com.hotelreservation;

import com.hotelreservation.util.DateUtil;
import java.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Automated Unit Tests for Date manipulation, ISO validation,
 * stay night calculation, and hotel reservation overlap logic.
 */
public class DateUtilTest {

    @Test
    public void testValidIsoDateFormat() {
        assertTrue("Valid ISO date should be accepted", DateUtil.isValidIsoDate("2026-11-15"));
        assertTrue("Leap year date should be accepted", DateUtil.isValidIsoDate("2028-02-29"));
        assertFalse("Invalid format should be rejected", DateUtil.isValidIsoDate("15/11/2026"));
        assertFalse("Invalid format should be rejected", DateUtil.isValidIsoDate("Nov 15, 2026"));
        assertFalse("Null date should be rejected", DateUtil.isValidIsoDate(null));
        assertFalse("Empty string should be rejected", DateUtil.isValidIsoDate(""));
    }

    @Test
    public void testIsCheckOutAfterCheckIn() {
        assertTrue("Check-out 3 days later is valid", DateUtil.isCheckOutAfterCheckIn("2026-11-01", "2026-11-04"));
        assertTrue("Check-out 1 day later is valid", DateUtil.isCheckOutAfterCheckIn("2026-11-01", "2026-11-02"));
        assertFalse("Same-day check-in and check-out is rejected for overnight stay",
                DateUtil.isCheckOutAfterCheckIn("2026-11-01", "2026-11-01"));
        assertFalse("Check-out prior to check-in must be rejected",
                DateUtil.isCheckOutAfterCheckIn("2026-11-05", "2026-11-02"));
    }

    @Test
    public void testCalculateNights() {
        assertEquals(3, DateUtil.calculateNights("2026-11-01", "2026-11-04"));
        assertEquals(1, DateUtil.calculateNights("2026-11-01", "2026-11-02"));
        assertEquals(7, DateUtil.calculateNights("2026-11-01", "2026-11-08"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateNightsThrowsOnInvalidRange() {
        DateUtil.calculateNights("2026-11-05", "2026-11-02");
    }

    @Test
    public void testPastDateCheck() {
        String yesterday = LocalDate.now().minusDays(1).toString();
        String today = LocalDate.now().toString();
        String tomorrow = LocalDate.now().plusDays(1).toString();

        assertTrue("Yesterday must be flagged as past date", DateUtil.isPastDate(yesterday));
        assertFalse("Today is eligible for check-in", DateUtil.isPastDate(today));
        assertFalse("Tomorrow is eligible for check-in", DateUtil.isPastDate(tomorrow));
    }

    /**
     * Tests the core Hotel Reservation Overlap Rule:
     * existingCheckIn < requestedCheckOut AND existingCheckOut > requestedCheckIn
     */
    @Test
    public void testDatesOverlapScenarios() {
        // Scenario 1: Exact identical interval overlaps
        assertTrue("Identical intervals overlap",
                DateUtil.datesOverlap("2026-11-05", "2026-11-10", "2026-11-05", "2026-11-10"));

        // Scenario 2: Requested stay completely inside existing stay
        assertTrue("Inner stay overlaps",
                DateUtil.datesOverlap("2026-11-01", "2026-11-15", "2026-11-05", "2026-11-10"));

        // Scenario 3: Requested stay envelops existing stay
        assertTrue("Enveloping stay overlaps",
                DateUtil.datesOverlap("2026-11-05", "2026-11-10", "2026-11-01", "2026-11-15"));

        // Scenario 4: Partial overlap on tail
        assertTrue("Tail overlap",
                DateUtil.datesOverlap("2026-11-01", "2026-11-07", "2026-11-05", "2026-11-12"));

        // Scenario 5: Partial overlap on head
        assertTrue("Head overlap",
                DateUtil.datesOverlap("2026-11-08", "2026-11-15", "2026-11-05", "2026-11-10"));

        // Scenario 6: Consecutive Stays - Same Day Checkout and Checkin (Must NOT overlap!)
        // Guest A departs 2026-11-05 at 11:00 AM, Guest B arrives 2026-11-05 at 3:00 PM
        assertFalse("Consecutive stays on same boundary date do NOT overlap",
                DateUtil.datesOverlap("2026-11-01", "2026-11-05", "2026-11-05", "2026-11-10"));

        assertFalse("Consecutive stays on opposite boundary do NOT overlap",
                DateUtil.datesOverlap("2026-11-10", "2026-11-15", "2026-11-05", "2026-11-10"));

        // Scenario 7: Completely disjoint intervals
        assertFalse("Disjoint stays do not overlap",
                DateUtil.datesOverlap("2026-11-01", "2026-11-04", "2026-11-10", "2026-11-15"));
    }
}

