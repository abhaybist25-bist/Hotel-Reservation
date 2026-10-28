package com.hotelreservation.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for robust, timezone-neutral date manipulation and validation.
 * Uses ISO-8601 YYYY-MM-DD representation exclusively for hotel stay calendar dates.
 */
public final class DateUtil {

    public static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private DateUtil() {
        // Prevent instantiation
    }

    /**
     * Checks if the given string conforms to valid ISO YYYY-MM-DD format.
     */
    public static boolean isValidIsoDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        try {
            LocalDate.parse(dateStr.trim(), ISO_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Parses an ISO string to a LocalDate object.
     */
    public static LocalDate parse(String dateStr) {
        if (!isValidIsoDate(dateStr)) {
            throw new IllegalArgumentException("Invalid date format. Expected YYYY-MM-DD, received: " + dateStr);
        }
        return LocalDate.parse(dateStr.trim(), ISO_FORMATTER);
    }

    /**
     * Returns today's date formatted as ISO YYYY-MM-DD.
     */
    public static String getTodayIso() {
        return LocalDate.now().format(ISO_FORMATTER);
    }

    /**
     * Returns tomorrow's date formatted as ISO YYYY-MM-DD.
     */
    public static String getTomorrowIso() {
        return LocalDate.now().plusDays(1).format(ISO_FORMATTER);
    }

    /**
     * Returns a date N days in the future formatted as ISO YYYY-MM-DD.
     */
    public static String getDatePlusDaysIso(int days) {
        return LocalDate.now().plusDays(days).format(ISO_FORMATTER);
    }

    /**
     * Validates that the given date is not earlier than today.
     */
    public static boolean isPastDate(String dateStr) {
        if (!isValidIsoDate(dateStr)) {
            return true;
        }
        LocalDate date = parse(dateStr);
        return date.isBefore(LocalDate.now());
    }

    /**
     * Validates that checkOut is strictly after checkIn.
     */
    public static boolean isCheckOutAfterCheckIn(String checkIn, String checkOut) {
        if (!isValidIsoDate(checkIn) || !isValidIsoDate(checkOut)) {
            return false;
        }
        LocalDate in = parse(checkIn);
        LocalDate out = parse(checkOut);
        return out.isAfter(in);
    }

    /**
     * Calculates the exact number of nights between check-in and check-out.
     * Minimum returned value is 1 for valid ranges.
     */
    public static int calculateNights(String checkIn, String checkOut) {
        if (!isCheckOutAfterCheckIn(checkIn, checkOut)) {
            throw new IllegalArgumentException("Check-out date must be strictly after check-in date");
        }
        LocalDate in = parse(checkIn);
        LocalDate out = parse(checkOut);
        long nights = ChronoUnit.DAYS.between(in, out);
        return (int) Math.max(1, nights);
    }

    /**
     * Determines whether two reservation date intervals overlap.
     *
     * Hotel Overlap Rule:
     * Interval A [checkInA, checkOutA) and Interval B [checkInB, checkOutB) overlap if and only if:
     * checkInA < checkOutB AND checkOutA > checkInB
     *
     * Because check-out occurs at midday and check-in in the afternoon,
     * same-day check-out and check-in (e.g. A out on 2026-11-05 and B in on 2026-11-05)
     * is permitted and does NOT overlap.
     */
    public static boolean datesOverlap(String checkInA, String checkOutA, String checkInB, String checkOutB) {
        LocalDate inA = parse(checkInA);
        LocalDate outA = parse(checkOutA);
        LocalDate inB = parse(checkInB);
        LocalDate outB = parse(checkOutB);

        return inA.isBefore(outB) && outA.isAfter(inB);
    }
}

