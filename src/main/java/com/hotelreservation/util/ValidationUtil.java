package com.hotelreservation.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Utility class providing validation, normalization, and sanitization methods.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+0-9()\\s.-]{7,25}$"
    );

    private ValidationUtil() {
        // Prevent instantiation
    }

    /**
     * Checks if an email string conforms to a valid RFC-compatible format.
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Normalizes an email address by trimming whitespace and converting to lowercase.
     */
    public static String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    /**
     * Checks if a phone number contains valid telephone characters.
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Validates that a password is at least 6 characters in length.
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Escapes critical HTML characters to guard against Cross-Site Scripting (XSS).
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
    }

    /**
     * Safely parses an integer or returns a fallback value.
     */
    public static int parsePositiveInt(String input, int defaultValue) {
        if (input == null || input.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            int val = Integer.parseInt(input.trim());
            return val > 0 ? val : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Safely parses a BigDecimal or returns null.
     */
    public static BigDecimal parsePositiveBigDecimal(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        try {
            BigDecimal val = new BigDecimal(input.trim());
            return val.compareTo(BigDecimal.ZERO) >= 0 ? val : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

