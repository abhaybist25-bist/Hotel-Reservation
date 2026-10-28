package com.hotelreservation.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utility class for formatting currency amounts for user interface presentation.
 */
public final class CurrencyUtil {

    private static final Locale US_LOCALE = Locale.US;

    private CurrencyUtil() {
        // Prevent instantiation
    }

    /**
     * Formats a BigDecimal as a US currency string ($#,##0.00).
     */
    public static String format(BigDecimal amount) {
        if (amount == null) {
            return "$0.00";
        }
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(US_LOCALE);
        return currencyFormatter.format(amount);
    }
}

