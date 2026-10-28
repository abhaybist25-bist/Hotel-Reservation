package com.hotelreservation.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Data Transfer Object containing live aggregated metrics for the Administrator Dashboard.
 */
public class AdminDashboardStats implements Serializable {
    private static final long serialVersionUID = 1L;

    private long totalRooms;
    private long activeRooms;
    private long totalBookings;
    private long upcomingBookings;
    private long cancelledBookings;
    private long registeredCustomers;
    private BigDecimal totalRevenue;

    public AdminDashboardStats() {
        this.totalRevenue = BigDecimal.ZERO;
    }

    public long getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(long totalRooms) {
        this.totalRooms = totalRooms;
    }

    public long getActiveRooms() {
        return activeRooms;
    }

    public void setActiveRooms(long activeRooms) {
        this.activeRooms = activeRooms;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public long getUpcomingBookings() {
        return upcomingBookings;
    }

    public void setUpcomingBookings(long upcomingBookings) {
        this.upcomingBookings = upcomingBookings;
    }

    public long getCancelledBookings() {
        return cancelledBookings;
    }

    public void setCancelledBookings(long cancelledBookings) {
        this.cancelledBookings = cancelledBookings;
    }

    public long getRegisteredCustomers() {
        return registeredCustomers;
    }

    public void setRegisteredCustomers(long registeredCustomers) {
        this.registeredCustomers = registeredCustomers;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}

