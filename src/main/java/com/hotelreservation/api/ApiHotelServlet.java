package com.hotelreservation.api;

import com.hotelreservation.model.Hotel;
import com.hotelreservation.model.Role;
import com.hotelreservation.model.User;
import com.hotelreservation.service.HotelService;
import com.hotelreservation.service.RoomService;
import java.io.IOException;
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
 * REST API endpoint for Hotel CRUD operations.
 *
 * <p>Endpoints:
 * <ul>
 *   <li>GET  /api/hotels              — List all active hotels</li>
 *   <li>GET  /api/hotels/{id}         — Get a single hotel by ID</li>
 *   <li>PUT  /api/hotels/{id}         — Update hotel details (ADMIN or owning manager)</li>
 *   <li>GET  /api/hotels/{id}/rooms   — List all rooms for a specific hotel</li>
 * </ul>
 */
@WebServlet(urlPatterns = {"/api/hotels", "/api/hotels/*"})
public class ApiHotelServlet extends ApiBaseServlet {

    private static final Logger LOGGER = Logger.getLogger(ApiHotelServlet.class.getName());

    private HotelService hotelService;
    private RoomService  roomService;

    @Override
    public void init() throws ServletException {
        this.hotelService = new HotelService();
        this.roomService  = new RoomService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // e.g. null, "/abc123", "/abc123/rooms"

        if (pathInfo == null || pathInfo.equals("/")) {
            handleList(req, res);
        } else {
            String[] parts = pathInfo.split("/");
            // parts[0] is "" (before leading slash), parts[1] is the id
            if (parts.length == 2) {
                handleGetById(req, res, parts[1]);
            } else if (parts.length == 3 && "rooms".equals(parts[2])) {
                handleGetRooms(req, res, parts[1]);
            } else {
                notFound(res, "Endpoint");
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            badRequest(res, "Hotel ID is required in URL path: PUT /api/hotels/{id}");
            return;
        }

        User user = getCurrentUser(req);
        if (user == null) {
            unauthorized(res);
            return;
        }
        if (!user.isAdmin() && !user.isHotel()) {
            forbidden(res);
            return;
        }

        String id = pathInfo.substring(1);
        handleUpdate(req, res, id, user);
    }

    // -------------------------------------------------------------------------
    // GET /api/hotels
    // -------------------------------------------------------------------------

    private void handleList(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            List<Hotel> hotels = hotelService.getAllActiveHotels();
            ok(res, hotels.stream().map(this::hotelToMap).toList());
        } catch (Exception e) {
            LOGGER.warning("Error listing hotels: " + e.getMessage());
            serverError(res, "Failed to retrieve hotels.");
        }
    }

    // -------------------------------------------------------------------------
    // GET /api/hotels/{id}
    // -------------------------------------------------------------------------

    private void handleGetById(HttpServletRequest req, HttpServletResponse res, String id) throws IOException {
        Hotel hotel = hotelService.getHotelById(id);
        if (hotel == null) {
            notFound(res, "Hotel");
            return;
        }
        ok(res, hotelToMap(hotel));
    }

    // -------------------------------------------------------------------------
    // GET /api/hotels/{id}/rooms
    // -------------------------------------------------------------------------

    private void handleGetRooms(HttpServletRequest req, HttpServletResponse res, String id) throws IOException {
        Hotel hotel = hotelService.getHotelById(id);
        if (hotel == null) {
            notFound(res, "Hotel");
            return;
        }
        try {
            var rooms = roomService.getRoomsByHotelId(hotel.getId(), true);
            ok(res, rooms.stream().map(r -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id",            r.getIdString());
                m.put("roomNumber",    r.getRoomNumber());
                m.put("roomType",      r.getRoomType() != null ? r.getRoomType().name() : null);
                m.put("title",         r.getTitle());
                m.put("pricePerNight", r.getPricePerNight());
                m.put("capacity",      r.getCapacity());
                m.put("amenities",     r.getAmenities());
                m.put("imageUrl",      r.getImageUrl());
                m.put("active",        r.isActive());
                return m;
            }).toList());
        } catch (Exception e) {
            LOGGER.warning("Error listing rooms for hotel " + id + ": " + e.getMessage());
            serverError(res, "Failed to retrieve rooms for hotel.");
        }
    }

    // -------------------------------------------------------------------------
    // PUT /api/hotels/{id}
    // -------------------------------------------------------------------------

    /**
     * Request body (all fields optional — only provided fields are updated):
     * <pre>{@code
     * {
     *   "name": "Grand Azure Resort",
     *   "description": "Luxury resort ...",
     *   "address": "1 Beach Road",
     *   "city": "Goa",
     *   "state": "Goa",
     *   "country": "India",
     *   "phone": "9876543210",
     *   "email": "info@grandazure.com",
     *   "starRating": 5,
     *   "amenities": ["Pool", "Spa", "Free Wi-Fi"],
     *   "imageUrl": "assets/images/grand-azure.jpg",
     *   "checkInTime": "14:00",
     *   "checkOutTime": "11:00"
     * }
     * }</pre>
     */
    @SuppressWarnings("unchecked")
    private void handleUpdate(HttpServletRequest req, HttpServletResponse res, String id, User user) throws IOException {
        Hotel hotel = hotelService.getHotelById(id);
        if (hotel == null) {
            notFound(res, "Hotel");
            return;
        }

        Map<String, Object> body;
        try {
            body = parseBody(req);
        } catch (IllegalArgumentException e) {
            badRequest(res, e.getMessage());
            return;
        }

        // Merge body fields onto existing hotel values
        String name         = body.containsKey("name")         ? getString(body, "name")         : hotel.getName();
        String description  = body.containsKey("description")  ? getString(body, "description")  : hotel.getDescription();
        String address      = body.containsKey("address")      ? getString(body, "address")       : hotel.getAddress();
        String city         = body.containsKey("city")         ? getString(body, "city")          : hotel.getCity();
        String state        = body.containsKey("state")        ? getString(body, "state")         : hotel.getState();
        String country      = body.containsKey("country")      ? getString(body, "country")       : hotel.getCountry();
        String phone        = body.containsKey("phone")        ? getString(body, "phone")         : hotel.getPhone();
        String email        = body.containsKey("email")        ? getString(body, "email")         : hotel.getEmail();
        int starRating      = body.containsKey("starRating")   ? getInt(body, "starRating", hotel.getStarRating()) : hotel.getStarRating();
        String imageUrl     = body.containsKey("imageUrl")     ? getString(body, "imageUrl")      : hotel.getImageUrl();
        String checkInTime  = body.containsKey("checkInTime")  ? getString(body, "checkInTime")   : hotel.getCheckInTime();
        String checkOutTime = body.containsKey("checkOutTime") ? getString(body, "checkOutTime")  : hotel.getCheckOutTime();
        List<String> amenities = hotel.getAmenities();
        if (body.containsKey("amenities") && body.get("amenities") instanceof List) {
            amenities = (List<String>) body.get("amenities");
        }

        try {
            ObjectId managerUserId = user.getId();
            Hotel updated = hotelService.updateHotelDetails(
                    hotel.getId(), managerUserId, user.isAdmin(),
                    name, description, address, city, state, country,
                    phone, email, starRating, amenities, imageUrl, checkInTime, checkOutTime
            );
            LOGGER.info("API: Hotel updated - " + updated.getName());
            ok(res, hotelToMap(updated));
        } catch (SecurityException e) {
            forbidden(res);
        } catch (IllegalArgumentException e) {
            badRequest(res, e.getMessage());
        } catch (IllegalStateException e) {
            serverError(res, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    /** Converts a Hotel model to a serializable Map for JSON response. */
    private Map<String, Object> hotelToMap(Hotel hotel) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",            hotel.getIdString());
        m.put("managerUserId", hotel.getManagerUserIdString());
        m.put("name",          hotel.getName());
        m.put("description",   hotel.getDescription());
        m.put("address",       hotel.getAddress());
        m.put("city",          hotel.getCity());
        m.put("state",         hotel.getState());
        m.put("country",       hotel.getCountry());
        m.put("phone",         hotel.getPhone());
        m.put("email",         hotel.getEmail());
        m.put("starRating",    hotel.getStarRating());
        m.put("amenities",     hotel.getAmenities());
        m.put("imageUrl",      hotel.getImageUrl());
        m.put("checkInTime",   hotel.getCheckInTime());
        m.put("checkOutTime",  hotel.getCheckOutTime());
        m.put("active",        hotel.isActive());
        m.put("createdAt",     hotel.getCreatedAt());
        m.put("updatedAt",     hotel.getUpdatedAt());
        return m;
    }
}

