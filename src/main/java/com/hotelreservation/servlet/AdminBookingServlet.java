package com.hotelreservation.servlet;

import com.hotelreservation.model.Booking;
import com.hotelreservation.model.BookingStatus;
import com.hotelreservation.service.AdminService;
import com.hotelreservation.service.BookingService;
import com.hotelreservation.util.ValidationUtil;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controller handling administrator reservation management, searching,
 * detailed audit inspection, and status lifecycle transitions.
 */
@WebServlet(name = "AdminBookingServlet", urlPatterns = {"/admin/bookings", "/admin/bookings/status", "/admin/bookings/details"})
public class AdminBookingServlet extends HttpServlet {

    private final AdminService adminService = new AdminService();
    private final BookingService bookingService = new BookingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/admin/bookings" -> handleListBookings(request, response);
            case "/admin/bookings/details" -> handleBookingDetails(request, response);
            default -> response.sendRedirect(request.getContextPath() + "/admin/bookings");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/admin/bookings/status".equals(path)) {
            handleUpdateStatus(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/bookings");
        }
    }

    private void handleListBookings(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int page = ValidationUtil.parsePositiveInt(request.getParameter("page"), 1);
        int pageSize = 15;
        String statusFilter = request.getParameter("status");
        String keyword = request.getParameter("q");

        List<Booking> bookings = adminService.getBookings(page, pageSize, statusFilter, keyword);

        request.setAttribute("bookings", bookings);
        request.setAttribute("currentPage", page);
        request.setAttribute("statusFilter", statusFilter);
        request.setAttribute("keyword", keyword);
        request.setAttribute("statuses", BookingStatus.values());

        request.getRequestDispatcher("/WEB-INF/views/admin/bookings.jsp").forward(request, response);
    }

    private void handleBookingDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String bookingId = request.getParameter("id");
        Booking booking = bookingService.getBookingById(bookingId, null, true);

        if (booking == null) {
            request.getSession().setAttribute("flashError", "Reservation not found.");
            response.sendRedirect(request.getContextPath() + "/admin/bookings");
            return;
        }

        request.setAttribute("booking", booking);
        request.setAttribute("statuses", BookingStatus.values());
        request.getRequestDispatcher("/WEB-INF/views/admin/booking-details.jsp").forward(request, response);
    }

    private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        String bookingId = request.getParameter("bookingId");
        String targetStatusStr = request.getParameter("targetStatus");

        try {
            BookingStatus targetStatus = BookingStatus.fromString(targetStatusStr);
            boolean updated = adminService.updateBookingStatus(bookingId, targetStatus);
            if (updated) {
                session.setAttribute("flashSuccess", "Reservation status successfully changed to: " + targetStatus.getDisplayName());
            } else {
                session.setAttribute("flashError", "Failed to update reservation status.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            session.setAttribute("flashError", e.getMessage());
        }

        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            response.sendRedirect(referer);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/bookings");
        }
    }
}

