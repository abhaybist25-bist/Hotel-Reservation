package com.hotelreservation.servlet;

import com.hotelreservation.model.Hotel;
import com.hotelreservation.model.Room;
import com.hotelreservation.model.RoomType;
import com.hotelreservation.model.User;
import com.hotelreservation.service.HotelService;
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
import org.bson.types.ObjectId;

/**
 * Controller handling room inventory management specifically for the logged-in Hotel Partner.
 */
@WebServlet(name = "HotelRoomServlet", urlPatterns = {"/hotel/rooms", "/hotel/rooms/add", "/hotel/rooms/edit", "/hotel/rooms/toggle", "/hotel/rooms/delete"})
public class HotelRoomServlet extends HttpServlet {

    private final RoomService roomService = new RoomService();
    private final HotelService hotelService = new HotelService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/hotel/rooms" -> handleListRooms(request, response);
            case "/hotel/rooms/add" -> handleShowAddForm(request, response);
            case "/hotel/rooms/edit" -> handleShowEditForm(request, response);
            default -> response.sendRedirect(request.getContextPath() + "/hotel/rooms");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/hotel/rooms/add" -> handleSaveNewRoom(request, response);
            case "/hotel/rooms/edit" -> handleUpdateRoom(request, response);
            case "/hotel/rooms/toggle" -> handleToggleActive(request, response);
            case "/hotel/rooms/delete" -> handleDeleteRoom(request, response);
            default -> response.sendRedirect(request.getContextPath() + "/hotel/rooms");
        }
    }

    private Hotel getHotel(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (user == null) {
            return null;
        }
        Hotel hotel = hotelService.getHotelByManagerId(user.getId());
        if (hotel == null && user.getHotelId() != null) {
            hotel = hotelService.getHotelById(user.getHotelId());
        }
        return hotel;
    }

    private void handleListRooms(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Hotel hotel = getHotel(request);
        if (hotel == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<Room> rooms = roomService.getRoomsByHotelId(hotel.getId(), false);
        request.setAttribute("hotel", hotel);
        request.setAttribute("rooms", rooms);
        request.setAttribute("hotelSection", "rooms");
        request.getRequestDispatcher("/WEB-INF/views/hotel/rooms.jsp").forward(request, response);
    }

    private void handleShowAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Hotel hotel = getHotel(request);
        request.setAttribute("hotel", hotel);
        request.setAttribute("roomTypes", RoomType.values());
        request.setAttribute("isEdit", false);
        request.setAttribute("hotelSection", "add-room");
        request.getRequestDispatcher("/WEB-INF/views/hotel/room-form.jsp").forward(request, response);
    }

    private void handleShowEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Hotel hotel = getHotel(request);
        String roomId = request.getParameter("id");
        Room room = roomService.getRoomById(roomId);

        if (room == null || hotel == null || (room.getHotelId() != null && !room.getHotelId().equals(hotel.getId()))) {
            request.getSession().setAttribute("flashError", "Room not found or unauthorized.");
            response.sendRedirect(request.getContextPath() + "/hotel/rooms");
            return;
        }

        request.setAttribute("hotel", hotel);
        request.setAttribute("room", room);
        request.setAttribute("roomTypes", RoomType.values());
        request.setAttribute("isEdit", true);
        request.setAttribute("hotelSection", "rooms");
        request.getRequestDispatcher("/WEB-INF/views/hotel/room-form.jsp").forward(request, response);
    }

    private void handleSaveNewRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        Hotel hotel = getHotel(request);
        if (hotel == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String roomNumber = request.getParameter("roomNumber");
        String roomTypeStr = request.getParameter("roomType");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("pricePerNight");
        String capacityStr = request.getParameter("capacity");
        String amenitiesCsv = request.getParameter("amenities");
        String imageUrl = request.getParameter("imageUrl");

        try {
            RoomType roomType = RoomType.fromString(roomTypeStr);
            BigDecimal price = ValidationUtil.parsePositiveBigDecimal(priceStr);
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Nightly rate must be a valid positive number.");
            }
            int capacity = ValidationUtil.parsePositiveInt(capacityStr, 2);

            List<String> amenities = (amenitiesCsv != null && !amenitiesCsv.trim().isEmpty())
                    ? Arrays.stream(amenitiesCsv.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList())
                    : new ArrayList<>();

            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                imageUrl = "assets/images/room-standard-1.svg";
            }

            Room room = new Room(roomNumber, roomType, title, description, price, capacity, amenities, imageUrl.trim());
            room.setHotelId(hotel.getId());
            room.setHotelName(hotel.getName());

            roomService.createRoom(room);

            request.getSession().setAttribute("flashSuccess", "Room " + roomNumber + " added successfully to " + hotel.getName() + "!");
            response.sendRedirect(request.getContextPath() + "/hotel/rooms");

        } catch (IllegalArgumentException | IllegalStateException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("hotel", hotel);
            request.setAttribute("roomNumber", roomNumber);
            request.setAttribute("selectedType", roomTypeStr);
            request.setAttribute("title", title);
            request.setAttribute("description", description);
            request.setAttribute("pricePerNight", priceStr);
            request.setAttribute("capacity", capacityStr);
            request.setAttribute("amenities", amenitiesCsv);
            request.setAttribute("imageUrl", imageUrl);
            request.setAttribute("roomTypes", RoomType.values());
            request.setAttribute("isEdit", false);
            request.setAttribute("hotelSection", "add-room");
            request.getRequestDispatcher("/WEB-INF/views/hotel/room-form.jsp").forward(request, response);
        }
    }

    private void handleUpdateRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        Hotel hotel = getHotel(request);
        String roomId = request.getParameter("id");
        Room existingRoom = roomService.getRoomById(roomId);

        if (existingRoom == null || hotel == null || (existingRoom.getHotelId() != null && !existingRoom.getHotelId().equals(hotel.getId()))) {
            request.getSession().setAttribute("flashError", "Room not found or unauthorized.");
            response.sendRedirect(request.getContextPath() + "/hotel/rooms");
            return;
        }

        String roomTypeStr = request.getParameter("roomType");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("pricePerNight");
        String capacityStr = request.getParameter("capacity");
        String amenitiesCsv = request.getParameter("amenities");
        String imageUrl = request.getParameter("imageUrl");
        boolean active = "true".equalsIgnoreCase(request.getParameter("active"));

        try {
            RoomType roomType = RoomType.fromString(roomTypeStr);
            BigDecimal price = ValidationUtil.parsePositiveBigDecimal(priceStr);
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Nightly rate must be a valid positive number.");
            }
            int capacity = ValidationUtil.parsePositiveInt(capacityStr, existingRoom.getCapacity());

            List<String> amenities = (amenitiesCsv != null && !amenitiesCsv.trim().isEmpty())
                    ? Arrays.stream(amenitiesCsv.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList())
                    : new ArrayList<>();

            existingRoom.setRoomType(roomType);
            existingRoom.setTitle(title);
            existingRoom.setDescription(description);
            existingRoom.setPricePerNight(price);
            existingRoom.setCapacity(capacity);
            existingRoom.setAmenities(amenities);
            existingRoom.setImageUrl(imageUrl);
            existingRoom.setActive(active);
            existingRoom.setHotelId(hotel.getId());
            existingRoom.setHotelName(hotel.getName());

            boolean updated = roomService.updateRoom(existingRoom);
            if (!updated) {
                throw new IllegalStateException("Failed to update room in database.");
            }

            request.getSession().setAttribute("flashSuccess", "Room " + existingRoom.getRoomNumber() + " updated successfully!");
            response.sendRedirect(request.getContextPath() + "/hotel/rooms");

        } catch (IllegalArgumentException | IllegalStateException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("hotel", hotel);
            request.setAttribute("room", existingRoom);
            request.setAttribute("roomTypes", RoomType.values());
            request.setAttribute("isEdit", true);
            request.setAttribute("hotelSection", "rooms");
            request.getRequestDispatcher("/WEB-INF/views/hotel/room-form.jsp").forward(request, response);
        }
    }

    private void handleToggleActive(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Hotel hotel = getHotel(request);
        String roomId = request.getParameter("id");
        Room room = roomService.getRoomById(roomId);

        if (room != null && hotel != null && (room.getHotelId() == null || room.getHotelId().equals(hotel.getId()))) {
            boolean newStatus = !room.isActive();
            roomService.toggleRoomActive(roomId, newStatus);
            request.getSession().setAttribute("flashSuccess",
                    "Room " + room.getRoomNumber() + " is now " + (newStatus ? "ACTIVE" : "INACTIVE") + ".");
        }

        response.sendRedirect(request.getContextPath() + "/hotel/rooms");
    }

    private void handleDeleteRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Hotel hotel = getHotel(request);
        String roomId = request.getParameter("id");
        Room room = roomService.getRoomById(roomId);

        if (room != null && hotel != null && (room.getHotelId() == null || room.getHotelId().equals(hotel.getId()))) {
            try {
                roomService.deleteRoomSafely(roomId);
                request.getSession().setAttribute("flashSuccess", "Room " + room.getRoomNumber() + " permanently removed.");
            } catch (IllegalStateException e) {
                request.getSession().setAttribute("flashError", e.getMessage());
            }
        }

        response.sendRedirect(request.getContextPath() + "/hotel/rooms");
    }
}
