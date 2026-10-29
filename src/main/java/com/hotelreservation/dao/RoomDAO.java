package com.hotelreservation.dao;

import com.hotelreservation.config.DatabaseConnection;
import com.hotelreservation.config.DatabaseInitializer;
import com.hotelreservation.model.Room;
import com.hotelreservation.model.RoomType;
import com.hotelreservation.model.dto.RoomSearchCriteria;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Updates.*;

/**
 * Data Access Object managing persistent MongoDB operations for Hotel Rooms.
 */
public class RoomDAO {

    private static final Logger LOGGER = Logger.getLogger(RoomDAO.class.getName());

    private MongoCollection<Document> getCollection() {
        MongoDatabase db = DatabaseConnection.getDatabase();
        if (db == null) {
            throw new IllegalStateException("Database connection has not been initialized");
        }
        return db.getCollection(DatabaseInitializer.ROOMS_COLLECTION);
    }

    /**
     * Persists a new Room document into MongoDB.
     */
    public boolean create(Room room) {
        if (room == null) {
            return false;
        }
        Document doc = toDocument(room);
        try {
            getCollection().insertOne(doc);
            room.setId(doc.getObjectId("_id"));
            return true;
        } catch (MongoWriteException e) {
            if (e.getError().getCode() == 11000) {
                LOGGER.warning("Duplicate room number: " + room.getRoomNumber());
                throw new IllegalArgumentException("Room number " + room.getRoomNumber() + " already exists.");
            }
            LOGGER.log(Level.SEVERE, "MongoDB write error creating room", e);
            throw e;
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "MongoDB error creating room", e);
            return false;
        }
    }

    /**
     * Finds a room by its ObjectId.
     */
    public Room findById(ObjectId id) {
        if (id == null) {
            return null;
        }
        try {
            Document doc = getCollection().find(eq("_id", id)).first();
            return doc != null ? toEntity(doc) : null;
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error finding room by id: " + id, e);
            return null;
        }
    }

    /**
     * Finds a room by its unique room number.
     */
    public Room findByRoomNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            return null;
        }
        try {
            Document doc = getCollection().find(eq("roomNumber", roomNumber.trim())).first();
            return doc != null ? toEntity(doc) : null;
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error finding room by roomNumber: " + roomNumber, e);
            return null;
        }
    }

    /**
     * Retrieves all rooms, optionally filtering by active status only.
     */
    public List<Room> findAll(boolean onlyActive) {
        List<Room> rooms = new ArrayList<>();
        try {
            Bson filter = onlyActive ? eq("active", true) : new Document();
            for (Document doc : getCollection().find(filter).sort(ascending("roomNumber"))) {
                rooms.add(toEntity(doc));
            }
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all rooms", e);
        }
        return rooms;
    }

    /**
     * Searches and filters available active rooms based on search criteria and excluded room IDs.
     *
     * @param criteria           Search parameters (dates, guests, room type, min/max price, sorting)
     * @param unavailableRoomIds Room IDs that have overlapping occupying bookings
     * @return List of matching available rooms
     */
    public List<Room> searchRooms(RoomSearchCriteria criteria, List<ObjectId> unavailableRoomIds) {
        List<Room> rooms = new ArrayList<>();
        try {
            List<Bson> filters = new ArrayList<>();

            // 1. Only active rooms
            filters.add(eq("active", true));

            // 2. Exclude rooms occupied for the requested dates
            if (unavailableRoomIds != null && !unavailableRoomIds.isEmpty()) {
                filters.add(nin("_id", unavailableRoomIds));
            }

            // 3. Minimum guest capacity
            if (criteria.getGuests() != null && criteria.getGuests() > 0) {
                filters.add(gte("capacity", criteria.getGuests()));
            }

            // 4. Room type filter
            if (criteria.getRoomType() != null) {
                filters.add(eq("roomType", criteria.getRoomType().name()));
            }

            // 5. Price bounds
            if (criteria.getMinPrice() != null) {
                filters.add(gte("pricePerNight", new Decimal128(criteria.getMinPrice())));
            }
            if (criteria.getMaxPrice() != null) {
                filters.add(lte("pricePerNight", new Decimal128(criteria.getMaxPrice())));
            }

            Bson combinedFilter = filters.size() == 1 ? filters.get(0) : and(filters);

            // Sorting
            Bson sortOrder = ascending("roomNumber");
            if ("price_asc".equalsIgnoreCase(criteria.getSortBy())) {
                sortOrder = ascending("pricePerNight");
            } else if ("price_desc".equalsIgnoreCase(criteria.getSortBy())) {
                sortOrder = descending("pricePerNight");
            } else if ("capacity_desc".equalsIgnoreCase(criteria.getSortBy())) {
                sortOrder = descending("capacity");
            }

            for (Document doc : getCollection().find(combinedFilter).sort(sortOrder)) {
                rooms.add(toEntity(doc));
            }
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error searching rooms", e);
        }
        return rooms;
    }

    /**
     * Updates an existing Room's editable fields.
     */
    public boolean update(Room room) {
        if (room == null || room.getId() == null) {
            return false;
        }
        try {
            Bson updates = combine(
                    set("title", room.getTitle()),
                    set("description", room.getDescription()),
                    set("roomType", room.getRoomType() != null ? room.getRoomType().name() : RoomType.STANDARD.name()),
                    set("pricePerNight", new Decimal128(room.getPricePerNight())),
                    set("capacity", room.getCapacity()),
                    set("amenities", room.getAmenities()),
                    set("imageUrl", room.getImageUrl()),
                    set("active", room.isActive()),
                    set("hotelId", room.getHotelId()),
                    set("hotelName", room.getHotelName()),
                    set("updatedAt", new Date())
            );

            return getCollection().updateOne(eq("_id", room.getId()), updates).getModifiedCount() > 0;
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error updating room id: " + room.getId(), e);
            return false;
        }
    }

    /**
     * Toggles room active/inactive status.
     */
    public boolean setActiveStatus(ObjectId id, boolean active) {
        if (id == null) {
            return false;
        }
        try {
            Bson updates = combine(
                    set("active", active),
                    set("updatedAt", new Date())
            );
            return getCollection().updateOne(eq("_id", id), updates).getModifiedCount() > 0;
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error setting active status for room: " + id, e);
            return false;
        }
    }

    /**
     * Physically deletes a room. Should only be called when room has no historical reservations.
     */
    public boolean delete(ObjectId id) {
        if (id == null) {
            return false;
        }
        try {
            return getCollection().deleteOne(eq("_id", id)).getDeletedCount() > 0;
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error deleting room: " + id, e);
            return false;
        }
    }

    /**
     * Retrieves all rooms associated with a specific hotel.
     */
    public List<Room> findByHotelId(ObjectId hotelId, boolean onlyActive) {
        List<Room> rooms = new ArrayList<>();
        if (hotelId == null) {
            return rooms;
        }
        try {
            Bson filter = onlyActive ? and(eq("hotelId", hotelId), eq("active", true)) : eq("hotelId", hotelId);
            for (Document doc : getCollection().find(filter).sort(ascending("roomNumber"))) {
                rooms.add(toEntity(doc));
            }
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error fetching rooms for hotel: " + hotelId, e);
        }
        return rooms;
    }

    /**
     * Counts total rooms for a hotel.
     */
    public long countByHotelId(ObjectId hotelId) {
        if (hotelId == null) {
            return 0;
        }
        try {
            return getCollection().countDocuments(eq("hotelId", hotelId));
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error counting rooms for hotel: " + hotelId, e);
            return 0;
        }
    }

    public long countTotal() {
        try {
            return getCollection().countDocuments();
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error counting total rooms", e);
            return 0;
        }
    }

    public long countActive() {
        try {
            return getCollection().countDocuments(eq("active", true));
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error counting active rooms", e);
            return 0;
        }
    }

    public static Document toDocument(Room room) {
        Document doc = new Document();
        if (room.getId() != null) {
            doc.append("_id", room.getId());
        }
        if (room.getHotelId() != null) {
            doc.append("hotelId", room.getHotelId());
        }
        if (room.getHotelName() != null) {
            doc.append("hotelName", room.getHotelName());
        }
        doc.append("roomNumber", room.getRoomNumber());
        doc.append("roomType", room.getRoomType() != null ? room.getRoomType().name() : RoomType.STANDARD.name());
        doc.append("title", room.getTitle());
        doc.append("description", room.getDescription());
        doc.append("pricePerNight", new Decimal128(room.getPricePerNight() != null ? room.getPricePerNight() : BigDecimal.ZERO));
        doc.append("capacity", room.getCapacity());
        doc.append("amenities", room.getAmenities() != null ? room.getAmenities() : new ArrayList<String>());
        doc.append("imageUrl", room.getImageUrl());
        doc.append("active", room.isActive());
        doc.append("createdAt", room.getCreatedAt() != null ? room.getCreatedAt() : new Date());
        doc.append("updatedAt", room.getUpdatedAt() != null ? room.getUpdatedAt() : new Date());
        return doc;
    }

    public static Room toEntity(Document doc) {
        if (doc == null) {
            return null;
        }
        Room room = new Room();
        room.setId(doc.getObjectId("_id"));
        room.setHotelId(doc.getObjectId("hotelId"));
        room.setHotelName(doc.getString("hotelName"));
        room.setRoomNumber(doc.getString("roomNumber"));
        room.setRoomType(RoomType.fromString(doc.getString("roomType")));
        room.setTitle(doc.getString("title"));
        room.setDescription(doc.getString("description"));

        // Safely extract pricePerNight as BigDecimal from Decimal128 or Number
        Object priceObj = doc.get("pricePerNight");
        if (priceObj instanceof Decimal128) {
            room.setPricePerNight(((Decimal128) priceObj).bigDecimalValue());
        } else if (priceObj instanceof Number) {
            room.setPricePerNight(BigDecimal.valueOf(((Number) priceObj).doubleValue()));
        } else {
            room.setPricePerNight(BigDecimal.ZERO);
        }

        room.setCapacity(doc.getInteger("capacity", 2));

        List<String> amenities = doc.getList("amenities", String.class);
        room.setAmenities(amenities != null ? amenities : new ArrayList<>());

        room.setImageUrl(doc.getString("imageUrl"));
        room.setActive(doc.getBoolean("active", true));
        room.setCreatedAt(doc.getDate("createdAt"));
        room.setUpdatedAt(doc.getDate("updatedAt"));
        return room;
    }
}

