package com.hotelreservation.api;

import com.hotelreservation.model.Role;
import com.hotelreservation.model.Room;
import com.hotelreservation.model.RoomType;
import com.hotelreservation.model.User;
import com.hotelreservation.model.dto.RoomSearchCriteria;
import com.hotelreservation.service.RoomService;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;

/**
 * REST API endpoint for Room CRUD operations.
 *
 * <p>Endpoints:
 * <ul>
 *   <li>GET    /api/rooms          — List active rooms (supports search params)</li>
 *   <li>GET    /api/rooms/{id}     — Get a single room by ID</li>
 *   <li>POST   /api/rooms          — Create a new room (ADMIN or HOTEL)</li>
 *   <li>PUT    /api/rooms/{id}     — Update a room (ADMIN or owning HOTEL)</li>
 *   <li>DELETE /api/rooms/{id}     — Delete a room (ADMIN or owning HOTEL)</li>
 * </ul>
 *
 * <p>Query params for GET /api/rooms:
 * <ul>
 *   <li>checkIn, checkOut — YYYY-MM-DD availability filter</li>
 *   <li>guests            — minimum capacity</li>
 *   <li>type              — RoomType enum name (STANDARD, DELUXE, SUITE, etc.)</li>
 *   <li>minPrice, maxPrice — price range</li>
 * </ul>
 */
@WebServlet(urlPatterns = {"/api/rooms", "/api/rooms/*"})
public class ApiRoomServlet extends ApiBaseServlet {

    private static final Logger LOGGER = Logger.getLogger(ApiRoomServlet.class.getName());

    private RoomService roomService;

    @Override
    public void init() throws ServletException {
        this.roomService = new RoomService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String id = extractId(req);
        if (id != null) {
            handleGetById(req, res, id);
        } else {
            handleList(req, res);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (!hasRole(req, Role.ADMIN, Role.HOTEL)) {
            unauthorized(res);
            return;
        }
        handleCreate(req, res);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String id = extractId(req);
        if (id == null) {
            badRequest(res, "Room ID is required in URL path: PUT /api/rooms/{id}");
            return;
        }
        if (!hasRole(req, Role.ADMIN, Role.HOTEL)) {
            unauthorized(res);
            return;
        }
        handleUpdate(req, res, id);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String id = extractId(req);
        if (id == null) {
            badRequest(res, "Room ID is required in URL path: DELETE /api/rooms/{id}");
            return;
        }
        if (!hasRole(req, Role.ADMIN, Role.HOTEL)) {
            unauthorized(res);
            return;
        }
        handleDelete(req, res, id);
    }

    // -------------------------------------------------------------------------
    // GET /api/rooms
    // -------------------------------------------------------------------------

    private void handleList(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String checkIn   = req.getParameter("checkIn");
        String checkOut  = req.getParameter("checkOut");
        String guestsStr = req.getParameter("guests");
        String typeStr   = req.getParameter("type");
        String minP      = req.getParameter("minPrice");
        String maxP      = req.getParameter("maxPrice");

        try {
            List<Room> rooms;
            if (checkIn != null && checkOut != null) {
                RoomSearchCriteria criteria = new RoomSearchCriteria();
                criteria.setCheckIn(checkIn);
                criteria.setCheckOut(checkOut);
                if (guestsStr != null) {
                    try { criteria.setGuests(Integer.parseInt(guestsStr)); } catch (NumberFormatException ignored) {}
                }
                if (typeStr != null && !typeStr.isEmpty()) {
                    try { criteria.setRoomType(RoomType.valueOf(typeStr.toUpperCase())); } catch (IllegalArgumentException ignored) {}
                }
                if (minP != null) {
                    try { criteria.setMinPrice(new BigDecimal(minP)); } catch (NumberFormatException ignored) {}
                }
                if (maxP != null) {
                    try { criteria.setMaxPrice(new BigDecimal(maxP)); } catch (NumberFormatException ignored) {}
                }
                rooms = roomService.searchAvailableRooms(criteria);
            } else {
                rooms = roomService.getAllRooms(true);
            }

            ok(res, rooms.stream().map(this::roomToMap).toList());
        } catch (IllegalArgumentException e) {
            badRequest(res, e.getMessage());
        } catch (Exception e) {
            LOGGER.warning("Error listing rooms: " + e.getMessage());
            serverError(res, "Failed to retrieve rooms.");
        }
    }

    // -------------------------------------------------------------------------
    // GET /api/rooms/{id}
    // -------------------------------------------------------------------------

    private void handleGetById(HttpServletRequest req, HttpServletResponse res, String id) throws IOException {
        Room room = roomService.getRoomById(id);
        if (room == null) {
            notFound(res, "Room");
            return;
        }
        ok(res, roomToMap(room));
    }

    // -------------------------------------------------------------------------
    // POST /api/rooms
    // -------------------------------------------------------------------------

    /**
     * Request body:
     * <pre>{@code
     * {
     *   "roomNumber": "101",
     *   "roomType": "DELUXE",
     *   "title": "Deluxe King Room",
     *   "description": "Spacious room with city view",
     *   "pricePerNight": 299.99,
     *   "capacity": 2,
     *   "amenities": ["Wi-Fi", "Air Conditioning", "Mini Bar"],
     *   "imageUrl": "assets/images/deluxe.jpg"
     * }
     * }</pre>
     */
    private void handleCreate(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Map<String, Object> body;
        try {
            body = parseBody(req);
        } catch (IllegalArgumentException e) {
            badRequest(res, e.getMessage());
            return;
        }

        try {
            Room room = mapToRoom(body, null);

            // If HOTEL role, automatically assign hotelId
            User user = getCurrentUser(req);
            if (user != null && user.isHotel() && user.getHotelId() != null) {
                room.setHotelId(user.getHotelId());
            }

            Room created = roomService.createRoom(room);
            LOGGER.info("API: Room created - " + created.getRoomNumber());
            ok(res, HttpServletResponse.SC_CREATED, roomToMap(created));
        } catch (IllegalArgumentException e) {
            badRequest(res, e.getMessage());
        } catch (IllegalStateException e) {
            serverError(res, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // PUT /api/rooms/{id}
    // -------------------------------------------------------------------------

    private void handleUpdate(HttpServletRequest req, HttpServletResponse res, String id) throws IOException {
        Room existing = roomService.getRoomById(id);
        if (existing == null) {
            notFound(res, "Room");
            return;
        }

        // HOTEL users can only update rooms they own
        User user = getCurrentUser(req);
        if (user != null && user.isHotel()) {
            if (existing.getHotelId() == null || !existing.getHotelId().equals(user.getHotelId())) {
                forbidden(res);
                return;
            }
        }

        Map<String, Object> body;
        try {
            body = parseBody(req);
        } catch (IllegalArgumentException e) {
            badRequest(res, e.getMessage());
            return;
        }

        try {
            Room updated = mapToRoom(body, existing);
            roomService.updateRoom(updated);
            LOGGER.info("API: Room updated - " + updated.getRoomNumber());
            ok(res, roomToMap(updated));
        } catch (IllegalArgumentException e) {
            badRequest(res, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // DELETE /api/rooms/{id}
    // -------------------------------------------------------------------------

    private void handleDelete(HttpServletRequest req, HttpServletResponse res, String id) throws IOException {
        Room existing = roomService.getRoomById(id);
        if (existing == null) {
            notFound(res, "Room");
            return;
        }

        // HOTEL users can only delete rooms they own
        User user = getCurrentUser(req);
        if (user != null && user.isHotel()) {
            if (existing.getHotelId() == null || !existing.getHotelId().equals(user.getHotelId())) {
                forbidden(res);
                return;
            }
        }

        try {
            roomService.deleteRoomSafely(id);
            LOGGER.info("API: Room deleted - " + id);
            okMessage(res, "Room deleted successfully.");
        } catch (IllegalArgumentException e) {
            badRequest(res, e.getMessage());
        } catch (IllegalStateException e) {
            // Room has bookings — block hard delete, suggest deactivation
            error(res, HttpServletResponse.SC_CONFLICT, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Extracts the /{id} segment from path info (e.g. /api/rooms/abc123 → abc123). */
    private String extractId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
            return null;
        }
        // pathInfo is "/abc123"
        String id = pathInfo.substring(1);
        return id.isEmpty() ? null : id;
    }

    /**
     * Maps request JSON body fields onto a Room instance.
     * When {@code existing} is non-null, only provided fields are overwritten (partial update).
     */
    @SuppressWarnings("unchecked")
    private Room mapToRoom(Map<String, Object> body, Room existing) {
        Room room = (existing != null) ? existing : new Room();

        String roomNumber = getString(body, "roomNumber");
        if (roomNumber != null) room.setRoomNumber(roomNumber);

        String typeStr = getString(body, "roomType");
        if (typeStr != null) {
            try { room.setRoomType(RoomType.valueOf(typeStr.toUpperCase())); } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid roomType: '" + typeStr + "'. Valid values: " + Arrays.toString(RoomType.values()));
            }
        }

        String title = getString(body, "title");
        if (title != null) room.setTitle(title);

        String description = getString(body, "description");
        if (description != null) room.setDescription(description);

        Object priceObj = body.get("pricePerNight");
        if (priceObj != null) {
            try { room.setPricePerNight(new BigDecimal(priceObj.toString())); } catch (NumberFormatException e) {
                throw new IllegalArgumentException("'pricePerNight' must be a valid decimal number.");
            }
        }

        Object capObj = body.get("capacity");
        if (capObj != null) {
            try { room.setCapacity(Integer.parseInt(capObj.toString())); } catch (NumberFormatException e) {
                throw new IllegalArgumentException("'capacity' must be a whole number.");
            }
        }

        Object amenitiesObj = body.get("amenities");
        if (amenitiesObj instanceof List) {
            room.setAmenities((List<String>) amenitiesObj);
        }

        String imageUrl = getString(body, "imageUrl");
        if (imageUrl != null) room.setImageUrl(imageUrl);

        Object activeObj = body.get("active");
        if (activeObj != null) {
            room.setActive(Boolean.parseBoolean(activeObj.toString()));
        }

        return room;
    }

    /** Converts a Room model to a serializable Map for JSON response. */
    private Map<String, Object> roomToMap(Room room) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",             room.getIdString());
        m.put("hotelId",        room.getHotelIdString());
        m.put("hotelName",      room.getHotelName());
        m.put("roomNumber",     room.getRoomNumber());
        m.put("roomType",       room.getRoomType() != null ? room.getRoomType().name() : null);
        m.put("title",          room.getTitle());
        m.put("description",    room.getDescription());
        m.put("pricePerNight",  room.getPricePerNight());
        m.put("capacity",       room.getCapacity());
        m.put("amenities",      room.getAmenities());
        m.put("imageUrl",       room.getImageUrl());
        m.put("active",         room.isActive());
        m.put("createdAt",      room.getCreatedAt());
        m.put("updatedAt",      room.getUpdatedAt());
        return m;
    }
}

