package com.hotelreservation.servlet;

import com.hotelreservation.model.Room;
import com.hotelreservation.service.RoomService;
import com.hotelreservation.util.DateUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller displaying full specifications, photographs, and booking controls for an individual room.
 */
@WebServlet(name = "RoomDetailServlet", urlPatterns = {"/room-details"})
public class RoomDetailServlet extends HttpServlet {

    private final RoomService roomService = new RoomService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String roomId = request.getParameter("id");
        Room room = roomService.getRoomById(roomId);

        if (room == null || !room.isActive()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "The requested room could not be found.");
            return;
        }

        String checkIn = request.getParameter("checkIn");
        String checkOut = request.getParameter("checkOut");
        String guests = request.getParameter("guests");

        request.setAttribute("room", room);
        request.setAttribute("checkIn", (checkIn != null && !checkIn.trim().isEmpty()) ? checkIn : DateUtil.getTomorrowIso());
        request.setAttribute("checkOut", (checkOut != null && !checkOut.trim().isEmpty()) ? checkOut : DateUtil.getDatePlusDaysIso(3));
        request.setAttribute("guests", (guests != null && !guests.trim().isEmpty()) ? guests : "2");

        request.getRequestDispatcher("/WEB-INF/views/rooms/room-details.jsp").forward(request, response);
    }
}

