package com.hotelreservation.service;

import com.hotelreservation.dao.BookingDAO;
import com.hotelreservation.dao.RoomDAO;
import com.hotelreservation.dao.UserDAO;
import com.hotelreservation.model.Booking;
import com.hotelreservation.model.BookingStatus;
import com.hotelreservation.model.Room;
import com.hotelreservation.model.User;
import com.hotelreservation.model.dto.AdminDashboardStats;
import java.util.List;
import java.util.logging.Logger;
import org.bson.types.ObjectId;

/**
 * Service managing administrative features, analytics, reservation status workflows,
 * and customer management.
 */
public class AdminService {

    private static final Logger LOGGER = Logger.getLogger(AdminService.class.getName());

    private final RoomDAO roomDAO;
    private final BookingDAO bookingDAO;
    private final UserDAO userDAO;

    public AdminService() {
        this.roomDAO = new RoomDAO();
        this.bookingDAO = new BookingDAO();
        this.userDAO = new UserDAO();
    }

    public AdminService(RoomDAO roomDAO, BookingDAO bookingDAO, UserDAO userDAO) {
        this.roomDAO = roomDAO;
        this.bookingDAO = bookingDAO;
        this.userDAO = userDAO;
    }

    /**
     * Calculates live metrics and KPIs directly from MongoDB collections.
     * No hardcoded values.
     */
    public AdminDashboardStats getDashboardStats() {
        AdminDashboardStats stats = new AdminDashboardStats();
        stats.setTotalRooms(roomDAO.countTotal());
        stats.setActiveRooms(roomDAO.countActive());
        stats.setTotalBookings(bookingDAO.countAll());
        stats.setUpcomingBookings(bookingDAO.countByStatus(BookingStatus.CONFIRMED));
        stats.setCancelledBookings(bookingDAO.countByStatus(BookingStatus.CANCELLED));
        stats.setRegisteredCustomers(userDAO.countCustomers());
        stats.setTotalRevenue(bookingDAO.calculateTotalRevenue());
        return stats;
    }

    /**
     * Retrieves all bookings with customer and room details joined for admin view.
     */
    public List<Booking> getBookings(int page, int pageSize, String statusFilter, String keyword) {
        int skip = (Math.max(1, page) - 1) * pageSize;
        List<Booking> bookings = bookingDAO.findAll(skip, pageSize, statusFilter, keyword);

        for (Booking b : bookings) {
            populateBookingDetails(b);
        }

        return bookings;
    }

    /**
     * Updates reservation status adhering to the lifecycle transition rules.
     */
    public boolean updateBookingStatus(String bookingIdString, BookingStatus newStatus) {
        if (bookingIdString == null || !ObjectId.isValid(bookingIdString) || newStatus == null) {
            throw new IllegalArgumentException("Invalid booking ID or target status.");
        }
        ObjectId bookingId = new ObjectId(bookingIdString);

        Booking booking = bookingDAO.findById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found.");
        }

        BookingStatus currentStatus = booking.getStatus();
        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new IllegalStateException("Invalid status transition: Cannot change status from " +
                    currentStatus.getDisplayName() + " to " + newStatus.getDisplayName() + ".");
        }

        boolean updated = bookingDAO.updateStatus(bookingId, newStatus);
        if (updated) {
            LOGGER.info("Admin updated booking " + booking.getBookingNumber() + " status to: " + newStatus);
        }
        return updated;
    }

    /**
     * Retrieves registered customer list with search and pagination.
     * Never exposes password hashes.
     */
    public List<User> getCustomers(int page, int pageSize, String keyword) {
        int skip = (Math.max(1, page) - 1) * pageSize;
        List<User> users = userDAO.searchCustomers(keyword, skip, pageSize);
        // Clear password hashes as safety guarantee
        for (User u : users) {
            u.setPasswordHash(null);
        }
        return users;
    }

    private void populateBookingDetails(Booking booking) {
        if (booking.getRoomId() != null) {
            Room r = roomDAO.findById(booking.getRoomId());
            if (r != null) {
                booking.setRoomNumber(r.getRoomNumber());
                booking.setRoomTitle(r.getTitle());
                booking.setRoomType(r.getRoomType());
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
    }
}

