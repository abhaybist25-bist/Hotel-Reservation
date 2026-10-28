package com.hotelreservation.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Data Transfer Object encapsulating accurate, server-side monetary calculations for reservations.
 * Uses BigDecimal with 2 decimal places and HALF_UP rounding to ensure precision.
 */
public class BookingCostBreakdown implements Serializable {
    private static final long serialVersionUID = 1L;

    private int numberOfNights;
    private BigDecimal pricePerNight;
    private BigDecimal subtotal;
    private BigDecimal taxRatePercentage;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;

    public BookingCostBreakdown() {
        this.pricePerNight = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
        this.taxRatePercentage = BigDecimal.valueOf(12.0); // 12% default
        this.taxAmount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
    }

    public static BookingCostBreakdown calculate(BigDecimal pricePerNight, int nights, BigDecimal taxRatePercentage) {
        BookingCostBreakdown breakdown = new BookingCostBreakdown();
        breakdown.numberOfNights = Math.max(1, nights);
        breakdown.pricePerNight = pricePerNight != null ? pricePerNight : BigDecimal.ZERO;
        breakdown.taxRatePercentage = taxRatePercentage != null ? taxRatePercentage : BigDecimal.valueOf(12.0);

        // Subtotal = PricePerNight * Nights
        BigDecimal subtotal = breakdown.pricePerNight.multiply(BigDecimal.valueOf(breakdown.numberOfNights))
                .setScale(2, RoundingMode.HALF_UP);
        breakdown.subtotal = subtotal;

        // Tax = Subtotal * (TaxRate / 100)
        BigDecimal taxMultiplier = breakdown.taxRatePercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal tax = subtotal.multiply(taxMultiplier).setScale(2, RoundingMode.HALF_UP);
        breakdown.taxAmount = tax;

        // Total = Subtotal + Tax
        breakdown.totalAmount = subtotal.add(tax).setScale(2, RoundingMode.HALF_UP);

        return breakdown;
    }

    public int getNumberOfNights() {
        return numberOfNights;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getTaxRatePercentage() {
        return taxRatePercentage;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}

