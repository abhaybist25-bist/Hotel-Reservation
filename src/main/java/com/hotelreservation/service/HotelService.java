package com.hotelreservation.service;

import com.hotelreservation.dao.HotelDAO;
import com.hotelreservation.model.Hotel;
import com.hotelreservation.util.ValidationUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.bson.types.ObjectId;

/**
 * Service managing hotel partner operations, hotel profile retrieval, and property detail updates.
 */
public class HotelService {

    private static final Logger LOGGER = Logger.getLogger(HotelService.class.getName());

    private final HotelDAO hotelDAO;

    public HotelService() {
        this.hotelDAO = new HotelDAO();
    }

    public HotelService(HotelDAO hotelDAO) {
        this.hotelDAO = hotelDAO;
    }

    /**
     * Finds the hotel profile managed by a specific user.
     */
    public Hotel getHotelByManagerId(ObjectId managerUserId) {
        if (managerUserId == null) {
            return null;
        }
        return hotelDAO.findByManagerUserId(managerUserId);
    }

    /**
     * Finds a hotel by its ObjectId.
     */
    public Hotel getHotelById(ObjectId hotelId) {
        if (hotelId == null) {
            return null;
        }
        return hotelDAO.findById(hotelId);
    }

    /**
     * Finds a hotel by string representation of ObjectId.
     */
    public Hotel getHotelById(String idString) {
        if (idString == null || !ObjectId.isValid(idString)) {
            return null;
        }
        return hotelDAO.findById(new ObjectId(idString));
    }

    /**
     * Retrieves all active hotels in the system.
     */
    public List<Hotel> getAllActiveHotels() {
        return hotelDAO.findAllActive();
    }

    /**
     * Creates a new hotel for a partner manager.
     */
    public Hotel createHotelForManager(ObjectId managerUserId, String name, String city, String address, String email, String phone) {
        if (managerUserId == null) {
            throw new IllegalArgumentException("Manager user ID is required.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Hotel name is required.");
        }

        Hotel hotel = new Hotel(name.trim(), city != null ? city.trim() : "", address != null ? address.trim() : "",
                phone != null ? phone.trim() : "", email != null ? email.trim() : "", managerUserId);
        hotel.setDescription("Welcome to " + name.trim() + ". Exceptional hospitality and comfortable stays await you.");
        hotel.setAmenities(List.of("Free High-Speed Wi-Fi", "Swimming Pool", "24/7 Room Service", "Air Conditioning", "Free Parking"));
        hotel.setImageUrl("assets/images/hero-hotel.svg");
        hotel.setStarRating(4);
        hotel.setCheckInTime("14:00");
        hotel.setCheckOutTime("11:00");

        boolean created = hotelDAO.create(hotel);
        if (!created) {
            throw new IllegalStateException("Failed to create hotel property in database.");
        }
        LOGGER.info("Created hotel '" + name + "' for manager: " + managerUserId);
        return hotel;
    }

    /**
     * Updates editable details of a hotel with authorization and data validation.
     */
    public Hotel updateHotelDetails(ObjectId hotelId, ObjectId managerUserId, boolean isAdmin,
                                    String name, String description, String address, String city,
                                    String state, String country, String phone, String email,
                                    int starRating, List<String> amenities, String imageUrl,
                                    String checkInTime, String checkOutTime) {

        if (hotelId == null) {
            throw new IllegalArgumentException("Hotel ID is required.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Hotel name cannot be empty.");
        }

        Hotel hotel = hotelDAO.findById(hotelId);
        if (hotel == null) {
            throw new IllegalArgumentException("Hotel not found with ID: " + hotelId);
        }

        // Strict authorization check: must be the manager of this hotel or a system admin
        if (!isAdmin && (hotel.getManagerUserId() == null || !hotel.getManagerUserId().equals(managerUserId))) {
            throw new SecurityException("Access Denied: You do not have permission to edit this hotel's details.");
        }

        if (email != null && !email.trim().isEmpty() && !ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Please provide a valid contact email address.");
        }

        hotel.setName(name.trim());
        hotel.setDescription(description != null ? description.trim() : "");
        hotel.setAddress(address != null ? address.trim() : "");
        hotel.setCity(city != null ? city.trim() : "");
        hotel.setState(state != null ? state.trim() : "");
        hotel.setCountry(country != null ? country.trim() : "");
        hotel.setPhone(phone != null ? phone.trim() : "");
        hotel.setEmail(email != null ? email.trim() : "");
        hotel.setStarRating(starRating);

        if (amenities != null) {
            hotel.setAmenities(amenities);
        }

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            hotel.setImageUrl(imageUrl.trim());
        }

        hotel.setCheckInTime(checkInTime != null && !checkInTime.trim().isEmpty() ? checkInTime.trim() : "14:00");
        hotel.setCheckOutTime(checkOutTime != null && !checkOutTime.trim().isEmpty() ? checkOutTime.trim() : "11:00");
        hotel.setUpdatedAt(new Date());

        boolean updated = hotelDAO.update(hotel);
        if (!updated) {
            throw new IllegalStateException("Failed to update hotel details in database.");
        }

        LOGGER.info("Successfully updated hotel details for hotel: " + hotel.getName() + " (" + hotelId + ")");
        return hotel;
    }
}
