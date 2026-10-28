package com.hotelreservation.servlet;

import com.hotelreservation.model.Booking;
import com.hotelreservation.model.Room;
import com.hotelreservation.model.User;
import com.hotelreservation.model.dto.BookingCostBreakdown;
import com.hotelreservation.service.BookingService;
import com.hotelreservation.service.RoomService;
import com.hotelreservation.util.ValidationUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controller orchestrating the end-to-end hotel reservation process:
 * Reviewing summary, server-side pricing validation, concurrent booking insertion,
 * and displaying confirmation.
 */
@WebServlet(name = "BookingServlet", urlPatterns = {"/book", "/booking/summary", "/booking/confirm", "/booking/confirmation"})
public class BookingServlet extends HttpServlet {

    private final BookingService bookingService = new BookingService();
    private final RoomService roomService = new RoomService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/book", "/booking/summary" -> handleBookingSummary(request, response);
            case "/booking/confirmation" -> handleBookingConfirmation(request, response);
            default -> response.sendRedirect(request.getContextPath() + "/rooms");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/booking/confirm".equals(path)) {
            handleConfirmBooking(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/rooms");
        }
    }

    private void handleBookingSummary(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String roomId = request.getParameter("roomId");
        String checkIn = request.getParameter("checkIn");
        String checkOut = request.getParameter("checkOut");
        int guests = ValidationUtil.parsePositiveInt(request.getParameter("guests"), 1);

        try {
            Room room = roomService.getRoomById(roomId);
            if (room == null || !room.isActive()) {
                request.setAttribute("error", "The requested room is not available.");
                request.getRequestDispatcher("/WEB-INF/views/rooms/room-list.jsp").forward(request, response);
                return;
            }

            BookingCostBreakdown breakdown = bookingService.calculateCostBreakdown(roomId, checkIn, checkOut, guests);

            request.setAttribute("room", room);
            request.setAttribute("checkIn", checkIn);
            request.setAttribute("checkOut", checkOut);
            request.setAttribute("guests", guests);
            request.setAttribute("breakdown", breakdown);

            request.getRequestDispatcher("/WEB-INF/views/bookings/booking-summary.jsp").forward(request, response);

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/rooms").forward(request, response);
        }
    }

    private void handleConfirmBooking(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String roomId = request.getParameter("roomId");
        String checkIn = request.getParameter("checkIn");
        String checkOut = request.getParameter("checkOut");
        int guests = ValidationUtil.parsePositiveInt(request.getParameter("guests"), 1);

        try {
            // Confirm reservation with per-room concurrency lock
            Booking booking = bookingService.createReservation(
                    currentUser.getId(), roomId, checkIn, checkOut, guests
            );

            // Post/Redirect/Get pattern
            session.setAttribute("flashSuccess", "Reservation confirmed successfully! Your booking confirmation is #" +
                    booking.getBookingNumber());

            response.sendRedirect(request.getContextPath() + "/booking/confirmation?num=" + booking.getBookingNumber());

        } catch (IllegalArgumentException | IllegalStateException e) {
            // Redisplay summary with error message
            Room room = roomService.getRoomById(roomId);
            request.setAttribute("room", room);
            request.setAttribute("checkIn", checkIn);
            request.setAttribute("checkOut", checkOut);
            request.setAttribute("guests", guests);
            request.setAttribute("error", e.getMessage());

            if (room != null) {
                try {
                    BookingCostBreakdown breakdown = bookingService.calculateCostBreakdown(roomId, checkIn, checkOut, guests);
                    request.setAttribute("breakdown", breakdown);
                } catch (Exception ignored) {
                }
            }

            request.getRequestDispatcher("/WEB-INF/views/bookings/booking-summary.jsp").forward(request, response);
        }
    }

    private void handleBookingConfirmation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        String bookingNumber = request.getParameter("num");
        if (bookingNumber == null || bookingNumber.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/customer/bookings");
            return;
        }

        Booking booking = bookingService.getBookingByNumber(bookingNumber);
        if (booking == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Reservation not found.");
            return;
        }

        // Ownership enforcement: Ensure current customer owns the reservation (or is admin)
        if (currentUser != null && !currentUser.isAdmin() && !booking.getUserId().equals(currentUser.getId())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized: You do not have permission to view this reservation.");
            return;
        }

        request.setAttribute("booking", booking);
        request.getRequestDispatcher("/WEB-INF/views/bookings/booking-confirmation.jsp").forward(request, response);
    }
}

