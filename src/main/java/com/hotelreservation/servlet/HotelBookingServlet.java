package com.hotelreservation.servlet;

import com.hotelreservation.model.Booking;
import com.hotelreservation.model.BookingStatus;
import com.hotelreservation.model.Hotel;
import com.hotelreservation.model.User;
import com.hotelreservation.service.AdminService;
import com.hotelreservation.service.BookingService;
import com.hotelreservation.service.HotelService;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controller handling guest reservation viewing and status transitions for the logged-in Hotel Partner.
 */
@WebServlet(name = "HotelBookingServlet", urlPatterns = {"/hotel/bookings", "/hotel/bookings/status"})
public class HotelBookingServlet extends HttpServlet {

    private final BookingService bookingService = new BookingService();
    private final HotelService hotelService = new HotelService();
    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Hotel hotel = hotelService.getHotelByManagerId(currentUser.getId());
        if (hotel == null && currentUser.getHotelId() != null) {
            hotel = hotelService.getHotelById(currentUser.getHotelId());
        }

        if (hotel == null) {
            response.sendRedirect(request.getContextPath() + "/hotel/dashboard");
            return;
        }

        List<Booking> bookings = bookingService.getBookingsForHotel(hotel.getId(), 0, 100);

        request.setAttribute("hotel", hotel);
        request.setAttribute("bookings", bookings);
        request.setAttribute("hotelSection", "bookings");
        request.getRequestDispatcher("/WEB-INF/views/hotel/bookings.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/hotel/bookings/status".equals(path)) {
            String bookingId = request.getParameter("id");
            String newStatusStr = request.getParameter("newStatus");

            try {
                BookingStatus newStatus = BookingStatus.valueOf(newStatusStr);
                adminService.updateBookingStatus(bookingId, newStatus);
                request.getSession().setAttribute("flashSuccess", "Reservation status updated to " + newStatus.getDisplayName() + ".");
            } catch (Exception e) {
                request.getSession().setAttribute("flashError", "Failed to update reservation status: " + e.getMessage());
            }
        }

        response.sendRedirect(request.getContextPath() + "/hotel/bookings");
    }
}
