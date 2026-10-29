package com.hotelreservation.dao;

import com.hotelreservation.config.DatabaseConnection;
import com.hotelreservation.config.DatabaseInitializer;
import com.hotelreservation.model.Role;
import com.hotelreservation.model.User;
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

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.descending;

/**
 * Data Access Object managing persistent MongoDB operations for User accounts.
 */
public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    private MongoCollection<Document> getCollection() {
        MongoDatabase db = DatabaseConnection.getDatabase();
        if (db == null) {
            throw new IllegalStateException("Database connection has not been initialized");
        }
        return db.getCollection(DatabaseInitializer.USERS_COLLECTION);
    }

    /**
     * Inserts a new user document into the database.
     * Sets the generated ObjectId on the user object.
     *
     * @param user User to persist
     * @return true if successful
     * @throws IllegalArgumentException on duplicate email
     */
    public boolean create(User user) {
        if (user == null) {
            return false;
        }

        Document doc = toDocument(user);
        try {
            getCollection().insertOne(doc);
            user.setId(doc.getObjectId("_id"));
            return true;
        } catch (MongoWriteException e) {
            if (e.getError().getCode() == 11000) {
                // Duplicate key error on normalizedEmail
                LOGGER.warning("Duplicate key conflict on user registration: " + user.getNormalizedEmail());
                throw new IllegalArgumentException("An account with this email address already exists.");
            }
            LOGGER.log(Level.SEVERE, "MongoDB write error creating user", e);
            throw e;
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "MongoDB error creating user", e);
            return false;
        }
    }

    /**
     * Finds a user by their MongoDB ObjectId.
     */
    public User findById(ObjectId id) {
        if (id == null) {
            return null;
        }
        try {
            Document doc = getCollection().find(eq("_id", id)).first();
            return doc != null ? toEntity(doc) : null;
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by id: " + id, e);
            return null;
        }
    }

    public boolean updateHotelId(ObjectId userId, ObjectId hotelId) {
        if (userId == null) {
            return false;
        }
        try {
            getCollection().updateOne(eq("_id", userId), new Document("$set", new Document("hotelId", hotelId).append("updatedAt", new Date())));
            return true;
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error updating user hotelId", e);
            return false;
        }
    }

    /**
     * Finds a user by their normalized email address.
     */
    public User findByNormalizedEmail(String normalizedEmail) {
        if (normalizedEmail == null || normalizedEmail.trim().isEmpty()) {
            return null;
        }
        try {
            Document doc = getCollection().find(eq("normalizedEmail", normalizedEmail.trim().toLowerCase())).first();
            return doc != null ? toEntity(doc) : null;
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by normalizedEmail: " + normalizedEmail, e);
            return null;
        }
    }

    /**
     * Returns total registered customers count (excluding administrators).
     */
    public long countCustomers() {
        try {
            return getCollection().countDocuments(eq("role", Role.CUSTOMER.name()));
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error counting registered customers", e);
            return 0;
        }
    }

    /**
     * Retrieves all registered customers with pagination.
     */
    public List<User> findAllCustomers(int skip, int limit) {
        List<User> users = new ArrayList<>();
        try {
            for (Document doc : getCollection().find(eq("role", Role.CUSTOMER.name()))
                    .sort(descending("createdAt"))
                    .skip(Math.max(0, skip))
                    .limit(Math.max(1, limit))) {
                users.add(toEntity(doc));
            }
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error fetching registered customers", e);
        }
        return users;
    }

    /**
     * Searches customers by keyword (name or email).
     */
    public List<User> searchCustomers(String keyword, int skip, int limit) {
        List<User> users = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllCustomers(skip, limit);
        }
        try {
            String regex = "(?i)" + PatternQuote(keyword.trim());
            Document filter = new Document("role", Role.CUSTOMER.name())
                    .append("$or", List.of(
                            new Document("fullName", new Document("$regex", regex)),
                            new Document("email", new Document("$regex", regex)),
                            new Document("phone", new Document("$regex", regex))
                    ));

            for (Document doc : getCollection().find(filter)
                    .sort(descending("createdAt"))
                    .skip(Math.max(0, skip))
                    .limit(Math.max(1, limit))) {
                users.add(toEntity(doc));
            }
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error searching customers with keyword: " + keyword, e);
        }
        return users;
    }

    private String PatternQuote(String s) {
        return java.util.regex.Pattern.quote(s);
    }

    public static Document toDocument(User user) {
        Document doc = new Document();
        if (user.getId() != null) {
            doc.append("_id", user.getId());
        }
        doc.append("fullName", user.getFullName());
        doc.append("email", user.getEmail());
        doc.append("normalizedEmail", user.getNormalizedEmail());
        doc.append("phone", user.getPhone());
        doc.append("passwordHash", user.getPasswordHash());
        doc.append("role", user.getRole() != null ? user.getRole().name() : Role.CUSTOMER.name());
        if (user.getHotelId() != null) {
            doc.append("hotelId", user.getHotelId());
        }
        doc.append("createdAt", user.getCreatedAt() != null ? user.getCreatedAt() : new Date());
        doc.append("updatedAt", user.getUpdatedAt() != null ? user.getUpdatedAt() : new Date());
        return doc;
    }

    public static User toEntity(Document doc) {
        if (doc == null) {
            return null;
        }
        User user = new User();
        user.setId(doc.getObjectId("_id"));
        user.setFullName(doc.getString("fullName"));
        user.setEmail(doc.getString("email"));
        user.setNormalizedEmail(doc.getString("normalizedEmail"));
        user.setPhone(doc.getString("phone"));
        user.setPasswordHash(doc.getString("passwordHash"));
        user.setRole(Role.fromString(doc.getString("role")));
        user.setHotelId(doc.getObjectId("hotelId"));
        user.setCreatedAt(doc.getDate("createdAt"));
        user.setUpdatedAt(doc.getDate("updatedAt"));
        return user;
    }
}

