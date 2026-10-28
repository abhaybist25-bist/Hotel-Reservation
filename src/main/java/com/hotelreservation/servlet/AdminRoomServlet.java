package com.hotelreservation.servlet;

import com.hotelreservation.model.Room;
import com.hotelreservation.model.RoomType;
import com.hotelreservation.service.RoomService;
import com.hotelreservation.util.ValidationUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controller handling Room inventory management for administrators:
 * Viewing, creating, updating, activating/deactivating, and safely deleting rooms.
 */
@WebServlet(name = "AdminRoomServlet", urlPatterns = {"/admin/rooms", "/admin/rooms/add", "/admin/rooms/edit", "/admin/rooms/toggle", "/admin/rooms/delete"})
public class AdminRoomServlet extends HttpServlet {

    private final RoomService roomService = new RoomService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/admin/rooms" -> handleListRooms(request, response);
            case "/admin/rooms/add" -> handleShowAddForm(request, response);
            case "/admin/rooms/edit" -> handleShowEditForm(request, response);
            default -> response.sendRedirect(request.getContextPath() + "/admin/rooms");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/admin/rooms/add" -> handleSaveNewRoom(request, response);
            case "/admin/rooms/edit" -> handleUpdateRoom(request, response);
            case "/admin/rooms/toggle" -> handleToggleActive(request, response);
            case "/admin/rooms/delete" -> handleDeleteRoom(request, response);
            default -> response.sendRedirect(request.getContextPath() + "/admin/rooms");
        }
    }

    private void handleListRooms(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Room> rooms = roomService.getAllRooms(false);
        request.setAttribute("rooms", rooms);
        request.getRequestDispatcher("/WEB-INF/views/admin/rooms.jsp").forward(request, response);
    }

    private void handleShowAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("roomTypes", RoomType.values());
        request.setAttribute("isEdit", false);
        request.getRequestDispatcher("/WEB-INF/views/admin/room-form.jsp").forward(request, response);
    }

    private void handleShowEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String roomId = request.getParameter("id");
        Room room = roomService.getRoomById(roomId);

        if (room == null) {
            request.getSession().setAttribute("flashError", "Room not found.");
            response.sendRedirect(request.getContextPath() + "/admin/rooms");
            return;
        }

        request.setAttribute("room", room);
        request.setAttribute("roomTypes", RoomType.values());
        request.setAttribute("isEdit", true);
        request.getRequestDispatcher("/WEB-INF/views/admin/room-form.jsp").forward(request, response);
    }

    private void handleSaveNewRoom(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        String roomNumber = request.getParameter("roomNumber");
        String roomTypeStr = request.getParameter("roomType");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("pricePerNight");
        String capacityStr = request.getParameter("capacity");
        String amenitiesRaw = request.getParameter("amenities");
        String imageUrl = request.getParameter("imageUrl");

        try {
            BigDecimal price = ValidationUtil.parsePositiveBigDecimal(priceStr);
            int capacity = ValidationUtil.parsePositiveInt(capacityStr, 2);
            RoomType type = RoomType.fromString(roomTypeStr);
            List<String> amenities = parseAmenities(amenitiesRaw);

            Room room = new Room(
                    roomNumber,
                    type,
                    title,
                    description,
                    price,
                    capacity,
                    amenities,
                    (imageUrl != null && !imageUrl.trim().isEmpty()) ? imageUrl.trim() : "assets/images/room-standard-1.svg"
            );

            roomService.createRoom(room);
            session.setAttribute("flashSuccess", "Room " + roomNumber + " created successfully.");
            response.sendRedirect(request.getContextPath() + "/admin/rooms");

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("roomTypes", RoomType.values());
            request.setAttribute("isEdit", false);
            request.getRequestDispatcher("/WEB-INF/views/admin/room-form.jsp").forward(request, response);
        }
    }

    private void handleUpdateRoom(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        String roomId = request.getParameter("id");
        String roomTypeStr = request.getParameter("roomType");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("pricePerNight");
        String capacityStr = request.getParameter("capacity");
        String amenitiesRaw = request.getParameter("amenities");
        String imageUrl = request.getParameter("imageUrl");
        boolean active = "on".equalsIgnoreCase(request.getParameter("active")) || "true".equalsIgnoreCase(request.getParameter("active"));

        Room room = roomService.getRoomById(roomId);
        if (room == null) {
            session.setAttribute("flashError", "Room not found.");
            response.sendRedirect(request.getContextPath() + "/admin/rooms");
            return;
        }

        try {
            BigDecimal price = ValidationUtil.parsePositiveBigDecimal(priceStr);
            int capacity = ValidationUtil.parsePositiveInt(capacityStr, room.getCapacity());

            room.setRoomType(RoomType.fromString(roomTypeStr));
            room.setTitle(title);
            room.setDescription(description);
            room.setPricePerNight(price);
            room.setCapacity(capacity);
            room.setAmenities(parseAmenities(amenitiesRaw));
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                room.setImageUrl(imageUrl.trim());
            }
            room.setActive(active);

            roomService.updateRoom(room);
            session.setAttribute("flashSuccess", "Room " + room.getRoomNumber() + " updated successfully.");
            response.sendRedirect(request.getContextPath() + "/admin/rooms");

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("room", room);
            request.setAttribute("roomTypes", RoomType.values());
            request.setAttribute("isEdit", true);
            request.getRequestDispatcher("/WEB-INF/views/admin/room-form.jsp").forward(request, response);
        }
    }

    private void handleToggleActive(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        String roomId = request.getParameter("roomId");
        boolean active = Boolean.parseBoolean(request.getParameter("active"));

        boolean updated = roomService.toggleRoomActive(roomId, active);
        if (updated) {
            session.setAttribute("flashSuccess", "Room status updated to: " + (active ? "Active" : "Inactive"));
        } else {
            session.setAttribute("flashError", "Failed to update room status.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/rooms");
    }

    private void handleDeleteRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        String roomId = request.getParameter("roomId");

        try {
            boolean deleted = roomService.deleteRoomSafely(roomId);
            if (deleted) {
                session.setAttribute("flashSuccess", "Room deleted permanently.");
            } else {
                session.setAttribute("flashError", "Could not delete room.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            session.setAttribute("flashError", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/admin/rooms");
    }

    private List<String> parseAmenities(String amenitiesRaw) {
        if (amenitiesRaw == null || amenitiesRaw.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(amenitiesRaw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}

