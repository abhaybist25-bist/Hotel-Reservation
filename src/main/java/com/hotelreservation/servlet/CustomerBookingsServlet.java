package com.hotelreservation.servlet;

import com.hotelreservation.model.Booking;
import com.hotelreservation.model.User;
import com.hotelreservation.service.BookingService;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controller managing customer booking history, inspection, and self-service cancellations.
 */
@WebServlet(name = "CustomerBookingsServlet", urlPatterns = {"/customer/bookings", "/customer/booking-details", "/customer/cancel-booking"})
public class CustomerBookingsServlet extends HttpServlet {

    private final BookingService bookingService = new BookingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        switch (path) {
            case "/customer/bookings" -> handleListBookings(request, response, currentUser);
            case "/customer/booking-details" -> handleBookingDetails(request, response, currentUser);
            default -> response.sendRedirect(request.getContextPath() + "/customer/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/customer/cancel-booking".equals(path)) {
            handleCancelBooking(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/customer/bookings");
        }
    }

    private void handleListBookings(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        List<Booking> bookings = bookingService.getCustomerBookings(currentUser.getId());
        request.setAttribute("bookings", bookings);
        request.getRequestDispatcher("/WEB-INF/views/customer/my-bookings.jsp").forward(request, response);
    }

    private void handleBookingDetails(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {
        String bookingId = request.getParameter("id");
        try {
            Booking booking = bookingService.getBookingById(bookingId, currentUser.getId(), currentUser.isAdmin());
            if (booking == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Reservation not found.");
                return;
            }
            request.setAttribute("booking", booking);
            request.getRequestDispatcher("/WEB-INF/views/customer/booking-details.jsp").forward(request, response);
        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }

    private void handleCancelBooking(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String bookingId = request.getParameter("bookingId");
        try {
            boolean success = bookingService.cancelReservation(bookingId, currentUser.getId(), false);
            if (success) {
                session.setAttribute("flashSuccess", "Your reservation has been cancelled. Any reserved dates are now released.");
            } else {
                session.setAttribute("flashError", "Failed to cancel reservation.");
            }
        } catch (IllegalArgumentException | IllegalStateException | SecurityException e) {
            session.setAttribute("flashError", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/customer/bookings");
    }
}

