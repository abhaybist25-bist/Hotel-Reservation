package com.hotelreservation.dao;

import com.hotelreservation.config.DatabaseConnection;
import com.hotelreservation.config.DatabaseInitializer;
import com.hotelreservation.model.Hotel;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.ascending;

/**
 * Data Access Object managing persistent MongoDB operations for Hotel properties.
 */
public class HotelDAO {

    private static final Logger LOGGER = Logger.getLogger(HotelDAO.class.getName());

    private MongoCollection<Document> getCollection() {
        MongoDatabase db = DatabaseConnection.getDatabase();
        if (db == null) {
            throw new IllegalStateException("Database connection has not been initialized");
        }
        return db.getCollection(DatabaseInitializer.HOTELS_COLLECTION);
    }

    /**
     * Persists a new Hotel document into MongoDB.
     */
    public boolean create(Hotel hotel) {
        if (hotel == null) {
            return false;
        }
        Document doc = toDocument(hotel);
        try {
            getCollection().insertOne(doc);
            hotel.setId(doc.getObjectId("_id"));
            return true;
        } catch (MongoWriteException e) {
            LOGGER.log(Level.SEVERE, "MongoDB write error creating hotel", e);
            throw e;
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "MongoDB error creating hotel", e);
            return false;
        }
    }

    /**
     * Finds a hotel by its ObjectId.
     */
    public Hotel findById(ObjectId id) {
        if (id == null) {
            return null;
        }
        try {
            Document doc = getCollection().find(eq("_id", id)).first();
            return doc != null ? toEntity(doc) : null;
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error finding hotel by id: " + id, e);
            return null;
        }
    }

    /**
     * Finds a hotel managed by a specific user account.
     */
    public Hotel findByManagerUserId(ObjectId managerUserId) {
        if (managerUserId == null) {
            return null;
        }
        try {
            Document doc = getCollection().find(eq("managerUserId", managerUserId)).first();
            return doc != null ? toEntity(doc) : null;
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error finding hotel by managerUserId: " + managerUserId, e);
            return null;
        }
    }

    /**
     * Retrieves all active hotels.
     */
    public List<Hotel> findAllActive() {
        List<Hotel> hotels = new ArrayList<>();
        try {
            for (Document doc : getCollection().find(eq("active", true)).sort(ascending("name"))) {
                hotels.add(toEntity(doc));
            }
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error fetching active hotels", e);
        }
        return hotels;
    }

    /**
     * Retrieves all hotels.
     */
    public List<Hotel> findAll() {
        List<Hotel> hotels = new ArrayList<>();
        try {
            for (Document doc : getCollection().find().sort(ascending("name"))) {
                hotels.add(toEntity(doc));
            }
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all hotels", e);
        }
        return hotels;
    }

    /**
     * Updates an existing hotel's details in MongoDB.
     */
    public boolean update(Hotel hotel) {
        if (hotel == null || hotel.getId() == null) {
            return false;
        }
        try {
            Document updateDoc = new Document()
                    .append("name", hotel.getName())
                    .append("description", hotel.getDescription())
                    .append("address", hotel.getAddress())
                    .append("city", hotel.getCity())
                    .append("state", hotel.getState())
                    .append("country", hotel.getCountry())
                    .append("phone", hotel.getPhone())
                    .append("email", hotel.getEmail())
                    .append("starRating", hotel.getStarRating())
                    .append("amenities", hotel.getAmenities() != null ? hotel.getAmenities() : new ArrayList<String>())
                    .append("imageUrl", hotel.getImageUrl())
                    .append("checkInTime", hotel.getCheckInTime())
                    .append("checkOutTime", hotel.getCheckOutTime())
                    .append("active", hotel.isActive())
                    .append("updatedAt", new Date());

            if (hotel.getManagerUserId() != null) {
                updateDoc.append("managerUserId", hotel.getManagerUserId());
            }

            getCollection().updateOne(eq("_id", hotel.getId()), new Document("$set", updateDoc));
            return true;
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error updating hotel: " + hotel.getId(), e);
            return false;
        }
    }

    /**
     * Counts total hotels.
     */
    public long countHotels() {
        try {
            return getCollection().countDocuments();
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error counting hotels", e);
            return 0;
        }
    }

    public static Document toDocument(Hotel hotel) {
        Document doc = new Document();
        if (hotel.getId() != null) {
            doc.append("_id", hotel.getId());
        }
        if (hotel.getManagerUserId() != null) {
            doc.append("managerUserId", hotel.getManagerUserId());
        }
        doc.append("name", hotel.getName());
        doc.append("description", hotel.getDescription());
        doc.append("address", hotel.getAddress());
        doc.append("city", hotel.getCity());
        doc.append("state", hotel.getState());
        doc.append("country", hotel.getCountry());
        doc.append("phone", hotel.getPhone());
        doc.append("email", hotel.getEmail());
        doc.append("starRating", hotel.getStarRating());
        doc.append("amenities", hotel.getAmenities() != null ? hotel.getAmenities() : new ArrayList<String>());
        doc.append("imageUrl", hotel.getImageUrl());
        doc.append("checkInTime", hotel.getCheckInTime());
        doc.append("checkOutTime", hotel.getCheckOutTime());
        doc.append("active", hotel.isActive());
        doc.append("createdAt", hotel.getCreatedAt() != null ? hotel.getCreatedAt() : new Date());
        doc.append("updatedAt", hotel.getUpdatedAt() != null ? hotel.getUpdatedAt() : new Date());
        return doc;
    }

    public static Hotel toEntity(Document doc) {
        if (doc == null) {
            return null;
        }
        Hotel hotel = new Hotel();
        hotel.setId(doc.getObjectId("_id"));
        hotel.setManagerUserId(doc.getObjectId("managerUserId"));
        hotel.setName(doc.getString("name"));
        hotel.setDescription(doc.getString("description"));
        hotel.setAddress(doc.getString("address"));
        hotel.setCity(doc.getString("city"));
        hotel.setState(doc.getString("state"));
        hotel.setCountry(doc.getString("country"));
        hotel.setPhone(doc.getString("phone"));
        hotel.setEmail(doc.getString("email"));
        hotel.setStarRating(doc.getInteger("starRating", 4));

        List<String> amenities = doc.getList("amenities", String.class);
        hotel.setAmenities(amenities != null ? amenities : new ArrayList<>());

        hotel.setImageUrl(doc.getString("imageUrl"));
        hotel.setCheckInTime(doc.getString("checkInTime"));
        hotel.setCheckOutTime(doc.getString("checkOutTime"));
        hotel.setActive(doc.getBoolean("active", true));
        hotel.setCreatedAt(doc.getDate("createdAt"));
        hotel.setUpdatedAt(doc.getDate("updatedAt"));
        return hotel;
    }
}
