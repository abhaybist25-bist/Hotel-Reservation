package com.hotelreservation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;

/**
 * Model class representing a Hotel in the multi-vendor reservation system.
 */
public class Hotel implements Serializable {
    private static final long serialVersionUID = 1L;

    private ObjectId id;
    private ObjectId managerUserId;
    private String name;
    private String description;
    private String address;
    private String city;
    private String state;
    private String country;
    private String phone;
    private String email;
    private int starRating;
    private List<String> amenities;
    private String imageUrl;
    private String checkInTime;
    private String checkOutTime;
    private boolean active;
    private Date createdAt;
    private Date updatedAt;

    public Hotel() {
        this.amenities = new ArrayList<>();
        this.starRating = 4;
        this.checkInTime = "14:00";
        this.checkOutTime = "11:00";
        this.active = true;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Hotel(String name, String city, String address, String phone, String email, ObjectId managerUserId) {
        this();
        this.name = name;
        this.city = city;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.managerUserId = managerUserId;
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

    public ObjectId getManagerUserId() {
        return managerUserId;
    }

    public void setManagerUserId(ObjectId managerUserId) {
        this.managerUserId = managerUserId;
    }

    public String getManagerUserIdString() {
        return managerUserId != null ? managerUserId.toHexString() : null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStarRating() {
        return starRating;
    }

    public void setStarRating(int starRating) {
        this.starRating = Math.max(1, Math.min(5, starRating));
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities != null ? new ArrayList<>(amenities) : new ArrayList<>();
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

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
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
        return "Hotel{" +
                "id=" + getIdString() +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", starRating=" + starRating +
                ", active=" + active +
                '}';
    }
}
