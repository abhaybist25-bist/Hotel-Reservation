package com.hotelreservation.config;

import com.hotelreservation.model.Role;
import com.hotelreservation.model.RoomType;
import com.hotelreservation.util.PasswordUtil;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.bson.types.Decimal128;

import static com.mongodb.client.model.Filters.eq;

/**
 * Initializes MongoDB collections, indexes, and bootstrap seed records if needed.
 * Safe to execute on every application startup; never overwrites existing records.
 */
public final class DatabaseInitializer {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    public static final String USERS_COLLECTION = "users";
    public static final String ROOMS_COLLECTION = "rooms";
    public static final String BOOKINGS_COLLECTION = "bookings";

    private DatabaseInitializer() {
        // Prevent instantiation
    }

    public static void initializeDatabase() {
        try {
            MongoDatabase db = DatabaseConnection.getDatabase();
            if (db == null) {
                LOGGER.warning("Database connection is null. Skipping database initialization.");
                return;
            }

            createCollectionsAndIndexes(db);
            seedBootstrapAdmin(db);
            seedSampleRooms(db);

            LOGGER.info("MongoDB database initialization completed successfully.");
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "MongoDB initialization encountered an error: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during MongoDB initialization: " + e.getMessage(), e);
        }
    }

    private static void createCollectionsAndIndexes(MongoDatabase db) {
        // Ensure collections exist by referencing them
        MongoCollection<Document> usersColl = db.getCollection(USERS_COLLECTION);
        MongoCollection<Document> roomsColl = db.getCollection(ROOMS_COLLECTION);
        MongoCollection<Document> bookingsColl = db.getCollection(BOOKINGS_COLLECTION);

        // 1. users: unique index on normalizedEmail
        usersColl.createIndex(
                Indexes.ascending("normalizedEmail"),
                new IndexOptions().unique(true).name("idx_users_normalized_email")
        );

        // 2. rooms: unique index on roomNumber
        roomsColl.createIndex(
                Indexes.ascending("roomNumber"),
                new IndexOptions().unique(true).name("idx_rooms_room_number")
        );

        // 3. bookings: unique index on bookingNumber
        bookingsColl.createIndex(
                Indexes.ascending("bookingNumber"),
                new IndexOptions().unique(true).name("idx_bookings_booking_number")
        );

        // 4. bookings: secondary indexes for query performance
        bookingsColl.createIndex(Indexes.ascending("userId"), new IndexOptions().name("idx_bookings_user_id"));
        bookingsColl.createIndex(Indexes.ascending("roomId"), new IndexOptions().name("idx_bookings_room_id"));
        bookingsColl.createIndex(Indexes.ascending("status"), new IndexOptions().name("idx_bookings_status"));

        // Compound index for availability conflict checking: (roomId, status, checkIn, checkOut)
        bookingsColl.createIndex(
                Indexes.compoundIndex(
                        Indexes.ascending("roomId"),
                        Indexes.ascending("status"),
                        Indexes.ascending("checkIn"),
                        Indexes.ascending("checkOut")
                ),
                new IndexOptions().name("idx_bookings_availability_lookup")
        );

        LOGGER.info("Verified all MongoDB indexes on collections: users, rooms, bookings.");
    }

    private static void seedBootstrapAdmin(MongoDatabase db) {
        MongoCollection<Document> usersColl = db.getCollection(USERS_COLLECTION);

        // Check if an ADMIN user already exists in the system
        long adminCount = usersColl.countDocuments(eq("role", Role.ADMIN.name()));
        if (adminCount == 0) {
            String adminEmail = AppConfig.getAdminBootstrapEmail().trim().toLowerCase();
            String adminPassword = AppConfig.getAdminBootstrapPassword();
            String adminName = AppConfig.getAdminBootstrapName();
            String adminPhone = AppConfig.getAdminBootstrapPhone();

            Document adminDoc = new Document()
                    .append("fullName", adminName)
                    .append("email", adminEmail)
                    .append("normalizedEmail", adminEmail)
                    .append("phone", adminPhone)
                    .append("passwordHash", PasswordUtil.hashPassword(adminPassword))
                    .append("role", Role.ADMIN.name())
                    .append("createdAt", new Date())
                    .append("updatedAt", new Date());

            usersColl.insertOne(adminDoc);
            LOGGER.info("Bootstrap administrator account seeded successfully: " + adminEmail);
        } else {
            LOGGER.info("Administrator account already present. Skipping admin seeding.");
        }
    }

    private static void seedSampleRooms(MongoDatabase db) {
        MongoCollection<Document> roomsColl = db.getCollection(ROOMS_COLLECTION);

        long roomCount = roomsColl.countDocuments();
        if (roomCount == 0) {
            LOGGER.info("Rooms collection is empty. Seeding initial realistic room inventory...");

            List<Document> sampleRooms = new ArrayList<>();

            // 1. Presidential Ocean Suite
            sampleRooms.add(createRoomDoc(
                    "101",
                    RoomType.SUITE,
                    "Presidential Ocean View Suite",
                    "Unrivaled panoramic coastal views with private terrace, Italian marble bath, separate dining area, and 24-hour dedicated butler service.",
                    new BigDecimal("495.00"),
                    4,
                    Arrays.asList("King Bed", "Private Terrace", "Oceanfront View", "Jacuzzi", "High-Speed Wi-Fi", "Espresso Machine", "Mini Bar", "Butler Service"),
                    "assets/images/room-suite-1.svg"
            ));

            // 2. Executive Penthouse Suite
            sampleRooms.add(createRoomDoc(
                    "102",
                    RoomType.SUITE,
                    "Executive Penthouse Suite",
                    "A contemporary sanctuary featuring top-floor vantage points, plush king bedding, designer living room, and curated mini library.",
                    new BigDecimal("380.00"),
                    3,
                    Arrays.asList("King Bed", "City Skyline View", "Walk-in Closet", "High-Speed Wi-Fi", "Mini Bar", "Complimentary Breakfast", "Workstation"),
                    "assets/images/room-suite-2.svg"
            ));

            // 3. Deluxe Ocean Breeze
            sampleRooms.add(createRoomDoc(
                    "201",
                    RoomType.DELUXE,
                    "Deluxe Ocean Breeze King",
                    "Spacious contemporary king bedroom with balcony overlooking the ocean lagoon, rainforest shower, and custom artisanal toiletries.",
                    new BigDecimal("260.00"),
                    3,
                    Arrays.asList("King Bed", "Ocean View", "Balcony", "Rain Shower", "High-Speed Wi-Fi", "Smart 65\" TV", "Air Conditioning"),
                    "assets/images/room-deluxe-1.svg"
            ));

            // 4. Deluxe Garden Poolside
            sampleRooms.add(createRoomDoc(
                    "202",
                    RoomType.DELUXE,
                    "Deluxe Garden & Pool Access",
                    "Direct garden terrace leading straight to our heated azure pool, outfitted with premium double queen beds and serene botanical decor.",
                    new BigDecimal("225.00"),
                    4,
                    Arrays.asList("Two Queen Beds", "Garden View", "Direct Pool Access", "High-Speed Wi-Fi", "Mini Refrigerator", "Coffee Maker"),
                    "assets/images/room-deluxe-2.svg"
            ));

            // 5. Family Garden Villa
            sampleRooms.add(createRoomDoc(
                    "301",
                    RoomType.FAMILY,
                    "Grand Family Haven Villa",
                    "Sprawling family retreat featuring two connected bedrooms, private garden lawn, full kitchenette, and children's entertainment console.",
                    new BigDecimal("340.00"),
                    6,
                    Arrays.asList("1 King & 2 Double Beds", "Private Garden", "Kitchenette", "Two Bathrooms", "High-Speed Wi-Fi", "Board Games & PS5", "Laundry Facilities"),
                    "assets/images/room-family-1.svg"
            ));

            // 6. Family Coastal Lodge
            sampleRooms.add(createRoomDoc(
                    "302",
                    RoomType.FAMILY,
                    "Coastal Family Retreat",
                    "Thoughtfully designed for traveling families with cozy bunk spaces, master suite, dining table, and sunset viewing veranda.",
                    new BigDecimal("290.00"),
                    5,
                    Arrays.asList("King Bed & Bunk Beds", "Sunset Veranda", "Dining Nook", "Two Bathrooms", "High-Speed Wi-Fi", "Smart TV"),
                    "assets/images/room-family-2.svg"
            ));

            // 7. Standard King Classic
            sampleRooms.add(createRoomDoc(
                    "401",
                    RoomType.STANDARD,
                    "Classic King Comfort",
                    "Elegantly appointed room with plush signature king mattress, ergonomic desk, high-pressure rain shower, and peaceful interior courtyard view.",
                    new BigDecimal("150.00"),
                    2,
                    Arrays.asList("King Bed", "Courtyard View", "Ergonomic Desk", "High-Speed Wi-Fi", "Coffee Station", "Safe"),
                    "assets/images/room-standard-1.svg"
            ));

            // 8. Standard Twin Serenity
            sampleRooms.add(createRoomDoc(
                    "402",
                    RoomType.STANDARD,
                    "Classic Twin Urban",
                    "Modern twin accommodation ideal for solo adventurers or business colleagues, complete with acoustic soundproofing and ambient lighting.",
                    new BigDecimal("135.00"),
                    2,
                    Arrays.asList("Two Twin Beds", "Soundproof Windows", "Workstation", "High-Speed Wi-Fi", "LED Ambient Lighting"),
                    "assets/images/room-standard-2.svg"
            ));

            roomsColl.insertMany(sampleRooms);
            LOGGER.info("Seeded 8 initial sample rooms into 'rooms' collection.");
        } else {
            LOGGER.info("Rooms collection already contains " + roomCount + " rooms. Skipping room seeding.");
        }
    }

    private static Document createRoomDoc(String roomNumber, RoomType type, String title,
                                          String description, BigDecimal price, int capacity,
                                          List<String> amenities, String imageUrl) {
        return new Document()
                .append("roomNumber", roomNumber)
                .append("roomType", type.name())
                .append("title", title)
                .append("description", description)
                .append("pricePerNight", new Decimal128(price))
                .append("capacity", capacity)
                .append("amenities", amenities)
                .append("imageUrl", imageUrl)
                .append("active", true)
                .append("createdAt", new Date())
                .append("updatedAt", new Date());
    }

    public static void main(String[] args) {
        System.out.println("==============================================================================");
        System.out.println("Initializing Hotel Reservation Database in MongoDB...");
        System.out.println("==============================================================================");
        DatabaseConnection.initialize();
        initializeDatabase();
        DatabaseConnection.close();
        System.out.println("==============================================================================");
        System.out.println("[SUCCESS] Database initialization completed! Refresh MongoDB Compass now.");
        System.out.println("==============================================================================");
    }
}

