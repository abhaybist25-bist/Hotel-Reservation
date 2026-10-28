package com.hotelreservation.servlet;

import com.hotelreservation.model.Booking;
import com.hotelreservation.model.BookingStatus;
import com.hotelreservation.model.User;
import com.hotelreservation.service.BookingService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controller providing customer metrics, recent bookings overview, and quick actions.
 */
@WebServlet(name = "CustomerDashboardServlet", urlPatterns = {"/customer/dashboard"})
public class CustomerDashboardServlet extends HttpServlet {

    private final BookingService bookingService = new BookingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<Booking> allBookings = bookingService.getCustomerBookings(currentUser.getId());

        List<Booking> upcomingBookings = new ArrayList<>();
        List<Booking> pastBookings = new ArrayList<>();
        List<Booking> cancelledBookings = new ArrayList<>();

        for (Booking b : allBookings) {
            if (b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.CHECKED_IN) {
                upcomingBookings.add(b);
            } else if (b.getStatus() == BookingStatus.CHECKED_OUT) {
                pastBookings.add(b);
            } else if (b.getStatus() == BookingStatus.CANCELLED) {
                cancelledBookings.add(b);
            }
        }

        request.setAttribute("totalBookings", allBookings.size());
        request.setAttribute("upcomingCount", upcomingBookings.size());
        request.setAttribute("pastCount", pastBookings.size());
        request.setAttribute("cancelledCount", cancelledBookings.size());

        // Recent upcoming bookings for dashboard table
        List<Booking> recentUpcoming = upcomingBookings.size() > 3 ? upcomingBookings.subList(0, 3) : upcomingBookings;
        request.setAttribute("recentBookings", recentUpcoming);

        request.getRequestDispatcher("/WEB-INF/views/customer/dashboard.jsp").forward(request, response);
    }
}

