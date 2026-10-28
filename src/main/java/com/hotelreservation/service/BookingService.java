package com.hotelreservation.service;

import com.hotelreservation.config.AppConfig;
import com.hotelreservation.dao.BookingDAO;
import com.hotelreservation.dao.RoomDAO;
import com.hotelreservation.dao.UserDAO;
import com.hotelreservation.model.Booking;
import com.hotelreservation.model.BookingStatus;
import com.hotelreservation.model.Room;
import com.hotelreservation.model.User;
import com.hotelreservation.model.dto.BookingCostBreakdown;
import com.hotelreservation.util.DateUtil;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import org.bson.types.ObjectId;

/**
 * Core Service managing reservation lifecycle, financial calculations, conflict prevention,
 * and concurrent booking protection.
 *
 * <p><b>Concurrency Architecture:</b>
 * For this single-instance educational deployment, this service maintains a thread-safe
 * per-room reentrant lock cache ({@code ConcurrentHashMap<String, ReentrantLock>}).
 * When a reservation is finalized, the room's lock is acquired, and availability is re-verified
 * directly within the locked critical section before the booking is inserted into MongoDB.
 *
 * <p><b>Distributed Limitations & Multi-Instance Architecture:</b>
 * In-memory Java locks coordinate threads only within a single JVM / Tomcat instance.
 * In a multi-node horizontal production cluster behind a load balancer, single-process locks
 * cannot prevent cross-instance race conditions. In such distributed production environments,
 * architects must implement either:
 * <ul>
 *   <li>Distributed locking (e.g., Redis Redlock algorithm or ZooKeeper recipes).</li>
 *   <li>MongoDB Multi-Document ACID Transactions on a Replica Set using snapshot isolation.</li>
 *   <li>Atomic conditional document updates with optimistic concurrency control.</li>
 * </ul>
 */
public class BookingService {

    private static final Logger LOGGER = Logger.getLogger(BookingService.class.getName());

    // Per-room locking table for single-instance race condition mitigation
    private static final ConcurrentHashMap<String, ReentrantLock> ROOM_LOCKS = new ConcurrentHashMap<>();
    private static final SecureRandom RANDOM = new SecureRandom();

    private final BookingDAO bookingDAO;
    private final RoomDAO roomDAO;
    private final UserDAO userDAO;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.roomDAO = new RoomDAO();
        this.userDAO = new UserDAO();
    }

    public BookingService(BookingDAO bookingDAO, RoomDAO roomDAO, UserDAO userDAO) {
        this.bookingDAO = bookingDAO;
        this.roomDAO = roomDAO;
        this.userDAO = userDAO;
    }

    /**
     * Prepares and validates a booking cost summary prior to final confirmation.
     * All pricing and nights calculations are performed on the server.
     */
    public BookingCostBreakdown calculateCostBreakdown(String roomIdString, String checkIn, String checkOut, int guests) {
        Room room = validateRoomAndDates(roomIdString, checkIn, checkOut, guests);
        int nights = DateUtil.calculateNights(checkIn, checkOut);
        return BookingCostBreakdown.calculate(room.getPricePerNight(), nights, AppConfig.getTaxPercentage());
    }

    /**
     * Creates and confirms a reservation with concurrency protection.
     * Re-checks room availability while holding the per-room lock.
     */
    public Booking createReservation(ObjectId userId, String roomIdString, String checkIn, String checkOut, int guests) {
        if (userId == null) {
            throw new IllegalArgumentException("User session required to make a booking.");
        }

        Room room = validateRoomAndDates(roomIdString, checkIn, checkOut, guests);
        ObjectId roomId = room.getId();

        // Obtain or create a fair reentrant lock specific to this room ID
        ReentrantLock roomLock = ROOM_LOCKS.computeIfAbsent(roomId.toHexString(), k -> new ReentrantLock(true));

        roomLock.lock();
        try {
            LOGGER.info("Acquired room lock for room: " + room.getRoomNumber() + " by thread: " + Thread.currentThread().getName());

            // CRITICAL: Recheck room availability inside the locked critical section
            List<Booking> conflicts = bookingDAO.findOccupyingBookingsForRoom(roomId, checkIn, checkOut);
            if (!conflicts.isEmpty()) {
                throw new IllegalStateException("Unfortunately, Room " + room.getRoomNumber() +
                        " was just reserved by another guest for the selected dates. Please choose another room or different dates.");
            }

            int nights = DateUtil.calculateNights(checkIn, checkOut);
            BookingCostBreakdown breakdown = BookingCostBreakdown.calculate(
                    room.getPricePerNight(), nights, AppConfig.getTaxPercentage()
            );

            String bookingNumber = generateBookingNumber();

            // Snapshot room price at time of booking to preserve historical fidelity
            Booking booking = new Booking(
                    bookingNumber,
                    userId,
                    roomId,
                    checkIn,
                    checkOut,
                    guests,
                    nights,
                    room.getPricePerNight(),
                    breakdown.getSubtotal(),
                    breakdown.getTaxAmount(),
                    breakdown.getTotalAmount()
            );

            boolean created = bookingDAO.create(booking);
            if (!created) {
                throw new IllegalStateException("Database error while confirming reservation. Please try again.");
            }

            LOGGER.info("Successfully created reservation " + bookingNumber + " for room " + room.getRoomNumber());
            return populateBookingDetails(booking);

        } finally {
            roomLock.unlock();
            LOGGER.info("Released room lock for room: " + room.getRoomNumber());
        }
    }

    /**
     * Cancels an eligible reservation with strict ownership and status transition enforcement.
     * Releases room availability immediately.
     */
    public boolean cancelReservation(String bookingIdString, ObjectId requestingUserId, boolean isAdmin) {
        if (bookingIdString == null || !ObjectId.isValid(bookingIdString)) {
            throw new IllegalArgumentException("Invalid booking ID.");
        }
        ObjectId bookingId = new ObjectId(bookingIdString);

        Booking booking = bookingDAO.findById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Reservation not found.");
        }

        // Ownership enforcement: Only the owner or an administrator can cancel
        if (!isAdmin && (requestingUserId == null || !booking.getUserId().equals(requestingUserId))) {
            throw new SecurityException("Unauthorized: You do not have permission to cancel this booking.");
        }

        // Status transition enforcement
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("This reservation cannot be cancelled because its current status is: " +
                    booking.getStatus().getDisplayName());
        }

        boolean updated = bookingDAO.updateStatus(bookingId, BookingStatus.CANCELLED);
        if (updated) {
            LOGGER.info("Reservation " + booking.getBookingNumber() + " successfully cancelled by " +
                    (isAdmin ? "Administrator" : "Customer " + requestingUserId));
        }
        return updated;
    }

    /**
     * Retrieves all bookings for a specific customer, fully populated with room info.
     */
    public List<Booking> getCustomerBookings(ObjectId userId) {
        List<Booking> bookings = bookingDAO.findByUserId(userId);
        for (Booking b : bookings) {
            populateBookingDetails(b);
        }
        return bookings;
    }

    /**
     * Retrieves a single booking by ID with ownership enforcement.
     */
    public Booking getBookingById(String bookingIdString, ObjectId requestingUserId, boolean isAdmin) {
        if (bookingIdString == null || !ObjectId.isValid(bookingIdString)) {
            return null;
        }
        ObjectId bookingId = new ObjectId(bookingIdString);
        Booking booking = bookingDAO.findById(bookingId);
        if (booking == null) {
            return null;
        }

        if (!isAdmin && (requestingUserId == null || !booking.getUserId().equals(requestingUserId))) {
            throw new SecurityException("Unauthorized access to reservation details.");
        }

        return populateBookingDetails(booking);
    }

    /**
     * Retrieves a single booking by booking confirmation number.
     */
    public Booking getBookingByNumber(String bookingNumber) {
        if (bookingNumber == null || bookingNumber.trim().isEmpty()) {
            return null;
        }
        Booking booking = bookingDAO.findByBookingNumber(bookingNumber.trim());
        return populateBookingDetails(booking);
    }

    /**
     * Validates room existence, active state, dates, and guest capacity.
     */
    private Room validateRoomAndDates(String roomIdString, String checkIn, String checkOut, int guests) {
        if (roomIdString == null || !ObjectId.isValid(roomIdString)) {
            throw new IllegalArgumentException("A valid room selection is required.");
        }
        ObjectId roomId = new ObjectId(roomIdString);
        Room room = roomDAO.findById(roomId);
        if (room == null || !room.isActive()) {
            throw new IllegalArgumentException("The selected room is either inactive or no longer available.");
        }

        if (!DateUtil.isValidIsoDate(checkIn) || !DateUtil.isValidIsoDate(checkOut)) {
            throw new IllegalArgumentException("Dates must be provided in valid ISO YYYY-MM-DD format.");
        }
        if (DateUtil.isPastDate(checkIn)) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }
        if (!DateUtil.isCheckOutAfterCheckIn(checkIn, checkOut)) {
            throw new IllegalArgumentException("Check-out date must be at least one day after check-in.");
        }
        if (guests <= 0) {
            throw new IllegalArgumentException("Number of guests must be at least 1.");
        }
        if (guests > room.getCapacity()) {
            throw new IllegalArgumentException("Number of guests (" + guests + ") exceeds room maximum capacity of " + room.getCapacity() + ".");
        }

        return room;
    }

    /**
     * Generates a unique, professional human-readable booking number, e.g., HRS-20261028-A7K92.
     */
    private String generateBookingNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String characters = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            sb.append(characters.charAt(RANDOM.nextInt(characters.length())));
        }
        return "HRS-" + datePart + "-" + sb.toString();
    }

    /**
     * Populates transient display properties (roomTitle, roomNumber, customerName, etc.)
     * by fetching related records from MongoDB.
     */
    private Booking populateBookingDetails(Booking booking) {
        if (booking == null) {
            return null;
        }

        if (booking.getRoomId() != null) {
            Room r = roomDAO.findById(booking.getRoomId());
            if (r != null) {
                booking.setRoomNumber(r.getRoomNumber());
                booking.setRoomTitle(r.getTitle());
                booking.setRoomType(r.getRoomType());
                booking.setRoomImageUrl(r.getImageUrl());
            }
        }

        if (booking.getUserId() != null) {
            User u = userDAO.findById(booking.getUserId());
            if (u != null) {
                booking.setCustomerName(u.getFullName());
                booking.setCustomerEmail(u.getEmail());
                booking.setCustomerPhone(u.getPhone());
            }
        }

        return booking;
    }
}

