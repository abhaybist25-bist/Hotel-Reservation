package com.hotelreservation;

import com.hotelreservation.model.dto.BookingCostBreakdown;
import java.math.BigDecimal;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Automated Unit Tests for accurate server-side monetary calculations using BigDecimal.
 */
public class BookingCostBreakdownTest {

    @Test
    public void testStandardCostBreakdown() {
        BigDecimal nightlyRate = new BigDecimal("250.00");
        int nights = 3;
        BigDecimal taxRate = new BigDecimal("12.0");

        BookingCostBreakdown breakdown = BookingCostBreakdown.calculate(nightlyRate, nights, taxRate);

        // Subtotal = 250 * 3 = 750.00
        assertEquals(new BigDecimal("750.00"), breakdown.getSubtotal());

        // Tax = 750 * 0.12 = 90.00
        assertEquals(new BigDecimal("90.00"), breakdown.getTaxAmount());

        // Total = 750.00 + 90.00 = 840.00
        assertEquals(new BigDecimal("840.00"), breakdown.getTotalAmount());
        assertEquals(3, breakdown.getNumberOfNights());
    }

    @Test
    public void testFractionalRounding() {
        BigDecimal nightlyRate = new BigDecimal("199.99");
        int nights = 1;
        BigDecimal taxRate = new BigDecimal("12.0");

        BookingCostBreakdown breakdown = BookingCostBreakdown.calculate(nightlyRate, nights, taxRate);

        // Subtotal = 199.99
        assertEquals(new BigDecimal("199.99"), breakdown.getSubtotal());

        // Tax = 199.99 * 0.12 = 23.9988 -> rounds to 24.00
        assertEquals(new BigDecimal("24.00"), breakdown.getTaxAmount());

        // Total = 199.99 + 24.00 = 223.99
        assertEquals(new BigDecimal("223.99"), breakdown.getTotalAmount());
    }

    @Test
    public void testZeroAndDefaultHandling() {
        BookingCostBreakdown breakdown = BookingCostBreakdown.calculate(BigDecimal.ZERO, 0, null);
        assertEquals(new BigDecimal("0.00"), breakdown.getSubtotal());
        assertEquals(new BigDecimal("0.00"), breakdown.getTaxAmount());
        assertEquals(new BigDecimal("0.00"), breakdown.getTotalAmount());
        assertEquals(1, breakdown.getNumberOfNights()); // Defaults to at least 1 night
    }
}

