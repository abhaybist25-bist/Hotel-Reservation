package com.hotelreservation.model.dto;

import com.hotelreservation.model.RoomType;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Data Transfer Object encapsulating room search, filtering, and sorting criteria.
 */
public class RoomSearchCriteria implements Serializable {
    private static final long serialVersionUID = 1L;

    private String checkIn;
    private String checkOut;
    private Integer guests;
    private RoomType roomType;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String sortBy; // "price_asc", "price_desc", "capacity_desc"

    public RoomSearchCriteria() {
    }

    public boolean hasDates() {
        return checkIn != null && !checkIn.trim().isEmpty() &&
               checkOut != null && !checkOut.trim().isEmpty();
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }

    public Integer getGuests() {
        return guests;
    }

    public void setGuests(Integer guests) {
        this.guests = guests;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}

