package com.hotelreservation.service;

import com.hotelreservation.dao.BookingDAO;
import com.hotelreservation.dao.RoomDAO;
import com.hotelreservation.model.Room;
import com.hotelreservation.model.dto.RoomSearchCriteria;
import com.hotelreservation.util.DateUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bson.types.ObjectId;

/**
 * Service managing room queries, availability lookups, and administrative room configuration.
 */
public class RoomService {

    private static final Logger LOGGER = Logger.getLogger(RoomService.class.getName());

    private final RoomDAO roomDAO;
    private final BookingDAO bookingDAO;

    public RoomService() {
        this.roomDAO = new RoomDAO();
        this.bookingDAO = new BookingDAO();
    }

    public RoomService(RoomDAO roomDAO, BookingDAO bookingDAO) {
        this.roomDAO = roomDAO;
        this.bookingDAO = bookingDAO;
    }

    /**
     * Searches available rooms based on dates, guests, room type, and price filters.
     * Accurately determines unavailability using date interval overlap rules.
     */
    public List<Room> searchAvailableRooms(RoomSearchCriteria criteria) {
        if (criteria == null) {
            criteria = new RoomSearchCriteria();
        }

        List<ObjectId> unavailableRoomIds = new ArrayList<>();

        if (criteria.hasDates()) {
            String checkIn = criteria.getCheckIn().trim();
            String checkOut = criteria.getCheckOut().trim();

            if (!DateUtil.isValidIsoDate(checkIn) || !DateUtil.isValidIsoDate(checkOut)) {
                throw new IllegalArgumentException("Invalid date format. Expected YYYY-MM-DD.");
            }
            if (DateUtil.isPastDate(checkIn)) {
                throw new IllegalArgumentException("Check-in date cannot be in the past.");
            }
            if (!DateUtil.isCheckOutAfterCheckIn(checkIn, checkOut)) {
                throw new IllegalArgumentException("Check-out date must be strictly after check-in date.");
            }

            // Find rooms that already have overlapping reservations
            unavailableRoomIds = bookingDAO.findUnavailableRoomIds(checkIn, checkOut);
        }

        return roomDAO.searchRooms(criteria, unavailableRoomIds);
    }

    /**
     * Retrieves all rooms (for admin management).
     */
    public List<Room> getAllRooms(boolean onlyActive) {
        return roomDAO.findAll(onlyActive);
    }

    public Room getRoomById(String idString) {
        if (idString == null || !ObjectId.isValid(idString)) {
            return null;
        }
        return roomDAO.findById(new ObjectId(idString));
    }

    public Room getRoomById(ObjectId id) {
        if (id == null) {
            return null;
        }
        return roomDAO.findById(id);
    }

    public Room getRoomByNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            return null;
        }
        return roomDAO.findByRoomNumber(roomNumber.trim());
    }

    /**
     * Creates a new room with input validation and duplicate number check.
     */
    public Room createRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Room data is required.");
        }
        if (room.getRoomNumber() == null || room.getRoomNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Room number is required.");
        }
        if (room.getTitle() == null || room.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Room title is required.");
        }
        if (room.getPricePerNight() == null || room.getPricePerNight().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Room price per night must be greater than zero.");
        }
        if (room.getCapacity() <= 0) {
            throw new IllegalArgumentException("Guest capacity must be at least 1.");
        }

        Room existing = roomDAO.findByRoomNumber(room.getRoomNumber().trim());
        if (existing != null) {
            throw new IllegalArgumentException("Room number " + room.getRoomNumber() + " already exists.");
        }

        boolean created = roomDAO.create(room);
        if (!created) {
            throw new IllegalStateException("Failed to create room in database.");
        }
        return room;
    }

    /**
     * Updates an existing room's details.
     */
    public boolean updateRoom(Room room) {
        if (room == null || room.getId() == null) {
            throw new IllegalArgumentException("Valid room with ID is required for update.");
        }
        if (room.getTitle() == null || room.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Room title is required.");
        }
        if (room.getPricePerNight() == null || room.getPricePerNight().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Room price per night must be greater than zero.");
        }
        if (room.getCapacity() <= 0) {
            throw new IllegalArgumentException("Guest capacity must be at least 1.");
        }

        return roomDAO.update(room);
    }

    /**
     * Toggles room active / inactive status.
     */
    public boolean toggleRoomActive(String roomIdString, boolean active) {
        if (roomIdString == null || !ObjectId.isValid(roomIdString)) {
            return false;
        }
        return roomDAO.setActiveStatus(new ObjectId(roomIdString), active);
    }

    /**
     * Deletes a room safely. If the room has historical reservations, deletion is blocked
     * to preserve relational integrity and audit trails.
     */
    public boolean deleteRoomSafely(String roomIdString) {
        if (roomIdString == null || !ObjectId.isValid(roomIdString)) {
            throw new IllegalArgumentException("Invalid room ID.");
        }
        ObjectId roomId = new ObjectId(roomIdString);

        if (bookingDAO.hasBookingsForRoom(roomId)) {
            throw new IllegalStateException(
                    "Cannot permanently delete this room because historical or active bookings exist for it. " +
                    "Please deactivate the room instead to prevent new bookings while preserving booking history."
            );
        }

        return roomDAO.delete(roomId);
    }
}

