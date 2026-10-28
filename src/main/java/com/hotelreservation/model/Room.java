package com.hotelreservation.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;

/**
 * Model class representing a Hotel Room in the MongoDB database.
 */
public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    private ObjectId id;
    private String roomNumber;
    private RoomType roomType;
    private String title;
    private String description;
    private BigDecimal pricePerNight;
    private int capacity;
    private List<String> amenities;
    private String imageUrl;
    private boolean active;
    private Date createdAt;
    private Date updatedAt;

    public Room() {
        this.amenities = new ArrayList<>();
        this.active = true;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.pricePerNight = BigDecimal.ZERO;
    }

    public Room(String roomNumber, RoomType roomType, String title, String description,
                BigDecimal pricePerNight, int capacity, List<String> amenities, String imageUrl) {
        this();
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.title = title;
        this.description = description;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.amenities = amenities != null ? new ArrayList<>(amenities) : new ArrayList<>();
        this.imageUrl = imageUrl;
        this.active = true;
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

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities != null ? amenities : new ArrayList<>();
    }

    public String getAmenitiesCsv() {
        if (amenities == null || amenities.isEmpty()) {
            return "";
        }
        return String.join(", ", amenities);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    @Override
    public String toString() {
        return "Room{" +
                "id=" + getIdString() +
                ", roomNumber='" + roomNumber + '\'' +
                ", roomType=" + roomType +
                ", title='" + title + '\'' +
                ", pricePerNight=" + pricePerNight +
                ", capacity=" + capacity +
                ", active=" + active +
                '}';
    }
}

