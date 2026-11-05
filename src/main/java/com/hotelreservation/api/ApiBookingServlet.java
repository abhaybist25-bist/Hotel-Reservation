package com.hotelreservation.api;

import com.hotelreservation.model.Booking;
import com.hotelreservation.model.BookingStatus;
import com.hotelreservation.model.Role;
import com.hotelreservation.model.User;
import com.hotelreservation.service.AdminService;
import com.hotelreservation.service.BookingService;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * REST API endpoint for Booking CRUD operations.
 *
 * <p>Endpoints:
 * <ul>
 *   <li>GET    /api/bookings            — List bookings (scoped by role)</li>
 *   <li>GET    /api/bookings/{id}       — Get a single booking by ID</li>
 *   <li>POST   /api/bookings            — Create a new booking (CUSTOMER)</li>
 *   <li>DELETE /api/bookings/{id}       — Cancel a booking</li>
 *   <li>PUT    /api/bookings/{id}/status — Update booking status (ADMIN or HOTEL)</li>
 * </ul>
 */
@WebServlet(urlPatterns = {"/api/bookings", "/api/bookings/*"})
public class ApiBookingServlet extends ApiBaseServlet {

    private static final Logger LOGGER = Logger.getLogger(ApiBookingServlet.class.getName());

    private BookingService bookingService;
    private AdminService   adminService;

    @Override
    public void init() throws ServletException {
        this.bookingService = new BookingService();
        this.adminService   = new AdminService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User user = getCurrentUser(req);
        if (user == null) {
            unauthorized(res);
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            handleList(req, res, user);
        } else {
            String[] parts = pathInfo.split("/");
            // /abc123 → ["", "abc123"]
            if (parts.length == 2) {
                handleGetById(req, res, parts[1], user);
            } else {
                notFound(res, "Endpoint");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User user = getCurrentUser(req);
        if (user == null) {
            unauthorized(res);
            return;
        }
        if (!user.isCustomer()) {
            error(res, HttpServletResponse.SC_FORBIDDEN,
                    "Only CUSTOMER accounts can create bookings. Your role is: " + user.getRole().name());
            return;
        }
        handleCreate(req, res, user);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User user = getCurrentUser(req);
        if (user == null) {
            unauthorized(res);
            return;
        }
        if (!user.isAdmin() && !user.isHotel()) {
            forbidden(res);
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            badRequest(res, "URL must be: PUT /api/bookings/{id}/status");
            return;
        }

        // /abc123/status → ["", "abc123", "status"]
        String[] parts = pathInfo.split("/");
        if (parts.length == 3 && "status".equals(parts[2])) {
            handleUpdateStatus(req, res, parts[1], user);
        } else {
            badRequest(res, "Only status updates are supported: PUT /api/bookings/{id}/status");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User user = getCurrentUser(req);
        if (user == null) {
            unauthorized(res);
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            badRequest(res, "Booking ID is required: DELETE /api/bookings/{id}");
            return;
        }

        String id = pathInfo.substring(1);
        handleCancel(req, res, id, user);
    }

    // -------------------------------------------------------------------------
    // GET /api/bookings
    // -------------------------------------------------------------------------

    private void handleList(HttpServletRequest req, HttpServletResponse res, User user) throws IOException {
        try {
            List<Booking> bookings;
            if (user.isAdmin()) {
                // Admin sees all bookings
                bookings = bookingService.getAllBookings();
            } else if (user.isHotel() && user.getHotelId() != null) {
                // Hotel manager sees bookings for their hotel
                bookings = bookingService.getBookingsForHotel(user.getHotelId(), 0, 200);
            } else {
                // Customer sees their own bookings
                bookings = bookingService.getCustomerBookings(user.getId());
            }
            ok(res, bookings.stream().map(this::bookingToMap).toList());
        } catch (Exception e) {
            LOGGER.warning("Error listing bookings: " + e.getMessage());
            serverError(res, "Failed to retrieve bookings.");
        }
    }

    // -------------------------------------------------------------------------
    // GET /api/bookings/{id}
    // -------------------------------------------------------------------------

    private void handleGetById(HttpServletRequest req, HttpServletResponse res, String id, User user) throws IOException {
        try {
            Booking booking = bookingService.getBookingById(id, user.getId(), user.isAdmin() || user.isHotel());
            if (booking == null) {
                notFound(res, "Booking");
                return;
            }
            ok(res, bookingToMap(booking));
        } catch (SecurityException e) {
            forbidden(res);
        } catch (Exception e) {
            LOGGER.warning("Error getting booking " + id + ": " + e.getMessage());
            serverError(res, "Failed to retrieve booking.");
        }
    }

    // -------------------------------------------------------------------------
    // POST /api/bookings
    // -------------------------------------------------------------------------

    /**
     * Request body:
     * <pre>{@code
     * {
     *   "roomId": "...",
     *   "checkIn": "2027-02-10",
     *   "checkOut": "2027-02-15",
     *   "numberOfGuests": 2
     * }
     * }</pre>
     */
    private void handleCreate(HttpServletRequest req, HttpServletResponse res, User user) throws IOException {
        Map<String, Object> body;
        try {
            body = parseBody(req);
        } catch (IllegalArgumentException e) {
            badRequest(res, e.getMessage());
            return;
        }

        String roomId   = getString(body, "roomId");
        String checkIn  = getString(body, "checkIn");
        String checkOut = getString(body, "checkOut");
        int guests      = getInt(body, "numberOfGuests", 1);

        if (roomId == null || roomId.isEmpty()) {
            badRequest(res, "'roomId' is required.");
            return;
        }
        if (checkIn == null || checkIn.isEmpty() || checkOut == null || checkOut.isEmpty()) {
            badRequest(res, "'checkIn' and 'checkOut' dates (YYYY-MM-DD) are required.");
            return;
        }

        try {
            Booking booking = bookingService.createReservation(user.getId(), roomId, checkIn, checkOut, guests);
            LOGGER.info("API: Booking created - " + booking.getBookingNumber() + " for user " + user.getEmail());
            ok(res, HttpServletResponse.SC_CREATED, bookingToMap(booking));
        } catch (IllegalArgumentException e) {
            badRequest(res, e.getMessage());
        } catch (IllegalStateException e) {
            error(res, HttpServletResponse.SC_CONFLICT, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // DELETE /api/bookings/{id}
    // -------------------------------------------------------------------------

    private void handleCancel(HttpServletRequest req, HttpServletResponse res, String id, User user) throws IOException {
        try {
            boolean cancelled = bookingService.cancelReservation(id, user.getId(), user.isAdmin());
            if (cancelled) {
                LOGGER.info("API: Booking cancelled - " + id + " by " + user.getEmail());
                okMessage(res, "Booking cancelled successfully.");
            } else {
                serverError(res, "Failed to cancel booking. Please try again.");
            }
        } catch (SecurityException e) {
            forbidden(res);
        } catch (IllegalArgumentException e) {
            badRequest(res, e.getMessage());
        } catch (IllegalStateException e) {
            error(res, HttpServletResponse.SC_CONFLICT, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // PUT /api/bookings/{id}/status
    // -------------------------------------------------------------------------

    /**
     * Request body:
     * <pre>{@code
     * {
     *   "newStatus": "CHECKED_IN"
     * }
     * }</pre>
     * Valid statuses: CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED, NO_SHOW
     */
    private void handleUpdateStatus(HttpServletRequest req, HttpServletResponse res, String id, User user) throws IOException {
        Map<String, Object> body;
        try {
            body = parseBody(req);
        } catch (IllegalArgumentException e) {
            badRequest(res, e.getMessage());
            return;
        }

        String newStatusStr = getString(body, "newStatus");
        if (newStatusStr == null || newStatusStr.isEmpty()) {
            badRequest(res, "'newStatus' is required. Valid values: " + Arrays.toString(BookingStatus.values()));
            return;
        }

        BookingStatus newStatus;
        try {
            newStatus = BookingStatus.valueOf(newStatusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            badRequest(res, "Invalid status '" + newStatusStr + "'. Valid values: " + Arrays.toString(BookingStatus.values()));
            return;
        }

        try {
            boolean updated = adminService.updateBookingStatus(id, newStatus);
            if (updated) {
                LOGGER.info("API: Booking " + id + " status → " + newStatus + " by " + user.getEmail());
                okMessage(res, "Booking status updated to " + newStatus.name() + ".");
            } else {
                serverError(res, "Failed to update booking status.");
            }
        } catch (SecurityException e) {
            forbidden(res);
        } catch (IllegalArgumentException e) {
            badRequest(res, e.getMessage());
        } catch (IllegalStateException e) {
            error(res, HttpServletResponse.SC_CONFLICT, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    /** Converts a Booking model to a serializable Map for JSON response. */
    private Map<String, Object> bookingToMap(Booking booking) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",                     booking.getIdString());
        m.put("bookingNumber",           booking.getBookingNumber());
        m.put("userId",                  booking.getUserId() != null ? booking.getUserId().toHexString() : null);
        m.put("roomId",                  booking.getRoomId() != null ? booking.getRoomId().toHexString() : null);
        m.put("hotelId",                 booking.getHotelId() != null ? booking.getHotelId().toHexString() : null);
        m.put("hotelName",               booking.getHotelName());
        m.put("checkIn",                 booking.getCheckIn());
        m.put("checkOut",                booking.getCheckOut());
        m.put("numberOfGuests",          booking.getNumberOfGuests());
        m.put("numberOfNights",          booking.getNumberOfNights());
        m.put("pricePerNightAtBooking",  booking.getPricePerNightAtBooking());
        m.put("subtotal",                booking.getSubtotal());
        m.put("taxes",                   booking.getTaxes());
        m.put("totalAmount",             booking.getTotalAmount());
        m.put("status",                  booking.getStatus() != null ? booking.getStatus().name() : null);
        m.put("customerName",            booking.getCustomerName());
        m.put("customerEmail",           booking.getCustomerEmail());
        m.put("customerPhone",           booking.getCustomerPhone());
        m.put("roomNumber",              booking.getRoomNumber());
        m.put("roomTitle",               booking.getRoomTitle());
        m.put("roomType",                booking.getRoomType() != null ? booking.getRoomType().name() : null);
        m.put("createdAt",               booking.getCreatedAt());
        m.put("updatedAt",               booking.getUpdatedAt());
        m.put("cancelledAt",             booking.getCancelledAt());
        return m;
    }
}
