package com.hotelreservation.servlet;

import com.hotelreservation.model.Room;
import com.hotelreservation.model.RoomType;
import com.hotelreservation.model.dto.RoomSearchCriteria;
import com.hotelreservation.service.RoomService;
import com.hotelreservation.util.DateUtil;
import com.hotelreservation.util.ValidationUtil;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller handling room catalog browsing, availability filtering, and price sorting.
 */
@WebServlet(name = "RoomListServlet", urlPatterns = {"/rooms"})
public class RoomListServlet extends HttpServlet {

    private final RoomService roomService = new RoomService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RoomSearchCriteria criteria = new RoomSearchCriteria();

        String checkIn = request.getParameter("checkIn");
        String checkOut = request.getParameter("checkOut");
        String guestsStr = request.getParameter("guests");
        String roomTypeStr = request.getParameter("roomType");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");
        String sortBy = request.getParameter("sortBy");

        if (checkIn != null && !checkIn.trim().isEmpty()) {
            criteria.setCheckIn(checkIn.trim());
        } else {
            criteria.setCheckIn(DateUtil.getTomorrowIso());
        }

        if (checkOut != null && !checkOut.trim().isEmpty()) {
            criteria.setCheckOut(checkOut.trim());
        } else {
            criteria.setCheckOut(DateUtil.getDatePlusDaysIso(3));
        }

        if (guestsStr != null && !guestsStr.trim().isEmpty()) {
            criteria.setGuests(ValidationUtil.parsePositiveInt(guestsStr, 1));
        } else {
            criteria.setGuests(1);
        }

        if (roomTypeStr != null && !roomTypeStr.trim().isEmpty() && !"ALL".equalsIgnoreCase(roomTypeStr.trim())) {
            criteria.setRoomType(RoomType.fromString(roomTypeStr));
        }

        criteria.setMinPrice(ValidationUtil.parsePositiveBigDecimal(minPriceStr));
        criteria.setMaxPrice(ValidationUtil.parsePositiveBigDecimal(maxPriceStr));
        criteria.setSortBy(sortBy != null ? sortBy.trim() : "price_asc");

        try {
            List<Room> rooms = roomService.searchAvailableRooms(criteria);
            request.setAttribute("rooms", rooms);
            request.setAttribute("criteria", criteria);
            request.setAttribute("roomTypes", RoomType.values());
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("rooms", roomService.getAllRooms(true));
            request.setAttribute("criteria", criteria);
            request.setAttribute("roomTypes", RoomType.values());
        }

        request.getRequestDispatcher("/WEB-INF/views/rooms/room-list.jsp").forward(request, response);
    }
}

