package com.hotelreservation.model;

import java.io.Serializable;
import java.util.Date;
import org.bson.types.ObjectId;

/**
 * Model class representing a User (Customer or Administrator) in the Hotel Reservation System.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private ObjectId id;
    private String fullName;
    private String email;
    private String normalizedEmail;
    private String phone;
    private String passwordHash;
    private Role role;
    private Date createdAt;
    private Date updatedAt;

    public User() {
        this.role = Role.CUSTOMER;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public User(String fullName, String email, String phone, String passwordHash, Role role) {
        this();
        this.fullName = fullName;
        this.email = email;
        this.normalizedEmail = email != null ? email.trim().toLowerCase() : null;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.role = role != null ? role : Role.CUSTOMER;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.normalizedEmail = email != null ? email.trim().toLowerCase() : null;
    }

    public String getNormalizedEmail() {
        return normalizedEmail;
    }

    public void setNormalizedEmail(String normalizedEmail) {
        this.normalizedEmail = normalizedEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Internal access to hashed password. Must NEVER be exposed to JSP views.
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean isCustomer() {
        return this.role == Role.CUSTOMER;
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
        return "User{" +
                "id=" + getIdString() +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}

