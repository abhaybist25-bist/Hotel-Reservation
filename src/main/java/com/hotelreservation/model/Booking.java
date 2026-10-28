package com.hotelreservation.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.bson.types.ObjectId;

/**
 * Model class representing a Hotel Room Reservation stored in MongoDB.
 */
public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;

    private ObjectId id;
    private String bookingNumber;
    private ObjectId userId;
    private ObjectId roomId;
    private String checkIn;   // ISO-8601 date string YYYY-MM-DD
    private String checkOut;  // ISO-8601 date string YYYY-MM-DD
    private int numberOfGuests;
    private int numberOfNights;
    private BigDecimal pricePerNightAtBooking;
    private BigDecimal subtotal;
    private BigDecimal taxes;
    private BigDecimal totalAmount;
    private BookingStatus status;
    private Date createdAt;
    private Date updatedAt;
    private Date cancelledAt;

    // Transient attributes for populated views
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String roomNumber;
    private String roomTitle;
    private RoomType roomType;
    private String roomImageUrl;

    public Booking() {
        this.status = BookingStatus.CONFIRMED;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.pricePerNightAtBooking = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
        this.taxes = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
    }

    public Booking(String bookingNumber, ObjectId userId, ObjectId roomId,
                   String checkIn, String checkOut, int numberOfGuests, int numberOfNights,
                   BigDecimal pricePerNightAtBooking, BigDecimal subtotal, BigDecimal taxes,
                   BigDecimal totalAmount) {
        this();
        this.bookingNumber = bookingNumber;
        this.userId = userId;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numberOfGuests = numberOfGuests;
        this.numberOfNights = numberOfNights;
        this.pricePerNightAtBooking = pricePerNightAtBooking;
        this.subtotal = subtotal;
        this.taxes = taxes;
        this.totalAmount = totalAmount;
        this.status = BookingStatus.CONFIRMED;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getIdString() {
        return id != null ? id.toHexString() : null;
    }

    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public String getUserIdString() {
        return userId != null ? userId.toHexString() : null;
    }

    public ObjectId getRoomId() {
        return roomId;
    }

    public void setRoomId(ObjectId roomId) {
        this.roomId = roomId;
    }

    public String getRoomIdString() {
        return roomId != null ? roomId.toHexString() : null;
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

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public int getNumberOfNights() {
        return numberOfNights;
    }

    public void setNumberOfNights(int numberOfNights) {
        this.numberOfNights = numberOfNights;
    }

    public BigDecimal getPricePerNightAtBooking() {
        return pricePerNightAtBooking;
    }

    public void setPricePerNightAtBooking(BigDecimal pricePerNightAtBooking) {
        this.pricePerNightAtBooking = pricePerNightAtBooking;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTaxes() {
        return taxes;
    }

    public void setTaxes(BigDecimal taxes) {
        this.taxes = taxes;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Date cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public boolean isEligibleForCancellation() {
        return this.status == BookingStatus.CONFIRMED;
    }

    // View populated properties
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public String getRoomImageUrl() {
        return roomImageUrl;
    }

    public void setRoomImageUrl(String roomImageUrl) {
        this.roomImageUrl = roomImageUrl;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + getIdString() +
                ", bookingNumber='" + bookingNumber + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                ", checkIn='" + checkIn + '\'' +
                ", checkOut='" + checkOut + '\'' +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                '}';
    }
}

