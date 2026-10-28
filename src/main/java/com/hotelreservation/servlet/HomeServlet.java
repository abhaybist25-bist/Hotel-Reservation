package com.hotelreservation.servlet;

import com.hotelreservation.model.Room;
import com.hotelreservation.service.RoomService;
import com.hotelreservation.util.DateUtil;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller handling the hotel home landing page.
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"", "/home"})
public class HomeServlet extends HttpServlet {

    private final RoomService roomService = new RoomService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Fetch active rooms from MongoDB for the featured showcase
        List<Room> allRooms = roomService.getAllRooms(true);
        List<Room> featuredRooms = allRooms.size() > 4 ? allRooms.subList(0, 4) : allRooms;

        request.setAttribute("featuredRooms", featuredRooms);
        request.setAttribute("defaultCheckIn", DateUtil.getTomorrowIso());
        request.setAttribute("defaultCheckOut", DateUtil.getDatePlusDaysIso(3));

        request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
    }
}

