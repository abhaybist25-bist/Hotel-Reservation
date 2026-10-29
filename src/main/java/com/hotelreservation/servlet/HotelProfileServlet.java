package com.hotelreservation.servlet;

import com.hotelreservation.model.Hotel;
import com.hotelreservation.model.User;
import com.hotelreservation.service.HotelService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.bson.types.ObjectId;

/**
 * Controller handling Hotel Partner profile and details management:
 * Viewing and updating hotel property information (name, address, city, contact, star rating, amenities, description, etc.).
 */
@WebServlet(name = "HotelProfileServlet", urlPatterns = {"/hotel/profile", "/hotel/profile/edit"})
public class HotelProfileServlet extends HttpServlet {

    private final HotelService hotelService = new HotelService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Hotel hotel = getOrCreateHotelForUser(currentUser);
        request.setAttribute("hotel", hotel);
        request.setAttribute("hotelSection", "profile");
        request.getRequestDispatcher("/WEB-INF/views/hotel/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Hotel hotel = getOrCreateHotelForUser(currentUser);

        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String address = request.getParameter("address");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String country = request.getParameter("country");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String starRatingStr = request.getParameter("starRating");
        String imageUrl = request.getParameter("imageUrl");
        String checkInTime = request.getParameter("checkInTime");
        String checkOutTime = request.getParameter("checkOutTime");

        // Parse amenities checkboxes and/or custom amenities
        String[] amenityParams = request.getParameterValues("amenities");
        List<String> amenitiesList = new ArrayList<>();
        if (amenityParams != null) {
            amenitiesList.addAll(Arrays.asList(amenityParams));
        }
        String customAmenities = request.getParameter("customAmenities");
        if (customAmenities != null && !customAmenities.trim().isEmpty()) {
            for (String item : customAmenities.split(",")) {
                String trimmed = item.trim();
                if (!trimmed.isEmpty() && !amenitiesList.contains(trimmed)) {
                    amenitiesList.add(trimmed);
                }
            }
        }

        int starRating = 4;
        try {
            if (starRatingStr != null && !starRatingStr.trim().isEmpty()) {
                starRating = Integer.parseInt(starRatingStr.trim());
            }
        } catch (NumberFormatException ignored) {
            starRating = 4;
        }

        try {
            Hotel updatedHotel = hotelService.updateHotelDetails(
                    hotel.getId(),
                    currentUser.getId(),
                    currentUser.isAdmin(),
                    name,
                    description,
                    address,
                    city,
                    state,
                    country,
                    phone,
                    email,
                    starRating,
                    amenitiesList,
                    imageUrl,
                    checkInTime,
                    checkOutTime
            );

            session.setAttribute("flashSuccess", "Hotel details for '" + updatedHotel.getName() + "' have been updated successfully!");
            response.sendRedirect(request.getContextPath() + "/hotel/profile");

        } catch (IllegalArgumentException | SecurityException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("hotel", hotel);
            request.setAttribute("hotelSection", "profile");
            request.getRequestDispatcher("/WEB-INF/views/hotel/profile.jsp").forward(request, response);
        }
    }

    private Hotel getOrCreateHotelForUser(User user) {
        Hotel hotel = hotelService.getHotelByManagerId(user.getId());
        if (hotel == null) {
            if (user.getHotelId() != null) {
                hotel = hotelService.getHotelById(user.getHotelId());
            }
        }
        if (hotel == null) {
            // Create default hotel property for this manager
            hotel = hotelService.createHotelForManager(
                    user.getId(),
                    user.getFullName() + "'s Hotel",
                    "Azure Bay",
                    "100 Coast Highway",
                    user.getEmail(),
                    user.getPhone()
            );
        }
        return hotel;
    }
}
