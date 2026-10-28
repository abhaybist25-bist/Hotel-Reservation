package com.hotelreservation.dao;

import com.hotelreservation.config.DatabaseConnection;
import com.hotelreservation.config.DatabaseInitializer;
import com.hotelreservation.model.Booking;
import com.hotelreservation.model.BookingStatus;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Data Access Object managing persistent MongoDB operations for Hotel Bookings.
 */
public class BookingDAO {

    private static final Logger LOGGER = Logger.getLogger(BookingDAO.class.getName());

    private MongoCollection<Document> getCollection() {
        MongoDatabase db = DatabaseConnection.getDatabase();
        if (db == null) {
            throw new IllegalStateException("Database connection has not been initialized");
        }
        return db.getCollection(DatabaseInitializer.BOOKINGS_COLLECTION);
    }

    /**
     * Persists a new booking document into MongoDB.
     */
    public boolean create(Booking booking) {
        if (booking == null) {
            return false;
        }
        Document doc = toDocument(booking);
        try {
            getCollection().insertOne(doc);
            booking.setId(doc.getObjectId("_id"));
            return true;
        } catch (MongoWriteException e) {
            if (e.getError().getCode() == 11000) {
                LOGGER.warning("Duplicate booking number conflict: " + booking.getBookingNumber());
                throw new IllegalArgumentException("Booking confirmation number conflict. Please retry.");
            }
            LOGGER.log(Level.SEVERE, "MongoDB write error creating booking", e);
            throw e;
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "MongoDB error creating booking", e);
            return false;
        }
    }

    /**
     * Finds a booking by its ObjectId.
     */
    public Booking findById(ObjectId id) {
        if (id == null) {
            return null;
        }
        try {
            Document doc = getCollection().find(eq("_id", id)).first();
            return doc != null ? toEntity(doc) : null;
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error finding booking by id: " + id, e);
            return null;
        }
    }

    /**
     * Finds a booking by its unique human-readable booking confirmation number.
     */
    public Booking findByBookingNumber(String bookingNumber) {
        if (bookingNumber == null || bookingNumber.trim().isEmpty()) {
            return null;
        }
        try {
            Document doc = getCollection().find(eq("bookingNumber", bookingNumber.trim())).first();
            return doc != null ? toEntity(doc) : null;
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error finding booking by number: " + bookingNumber, e);
            return null;
        }
    }

    /**
     * Finds all bookings belonging to a specific customer, sorted latest first.
     */
    public List<Booking> findByUserId(ObjectId userId) {
        List<Booking> bookings = new ArrayList<>();
        if (userId == null) {
            return bookings;
        }
        try {
            for (Document doc : getCollection().find(eq("userId", userId)).sort(descending("createdAt"))) {
                bookings.add(toEntity(doc));
            }
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error finding bookings for user: " + userId, e);
        }
        return bookings;
    }

    /**
     * Finds all active occupying bookings for a specific room that overlap with the requested dates.
     *
     * Overlap rule:
     * existingCheckIn < requestedCheckOut AND existingCheckOut > requestedCheckIn
     *
     * Only applies to statuses that occupy the room: CONFIRMED and CHECKED_IN.
     */
    public List<Booking> findOccupyingBookingsForRoom(ObjectId roomId, String requestedCheckIn, String requestedCheckOut) {
        List<Booking> conflicts = new ArrayList<>();
        if (roomId == null || requestedCheckIn == null || requestedCheckOut == null) {
            return conflicts;
        }

        try {
            List<String> occupyingStatuses = List.of(BookingStatus.CONFIRMED.name(), BookingStatus.CHECKED_IN.name());

            Bson filter = and(
                    eq("roomId", roomId),
                    in("status", occupyingStatuses),
                    lt("checkIn", requestedCheckOut),
                    gt("checkOut", requestedCheckIn)
            );

            for (Document doc : getCollection().find(filter)) {
                conflicts.add(toEntity(doc));
            }
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error querying occupying bookings for room: " + roomId, e);
        }

        return conflicts;
    }

    /**
     * Identifies all room IDs that have overlapping occupying reservations during the requested period.
     */
    public List<ObjectId> findUnavailableRoomIds(String requestedCheckIn, String requestedCheckOut) {
        Set<ObjectId> unavailableIds = new HashSet<>();
        if (requestedCheckIn == null || requestedCheckOut == null) {
            return new ArrayList<>();
        }

        try {
            List<String> occupyingStatuses = List.of(BookingStatus.CONFIRMED.name(), BookingStatus.CHECKED_IN.name());

            Bson filter = and(
                    in("status", occupyingStatuses),
                    lt("checkIn", requestedCheckOut),
                    gt("checkOut", requestedCheckIn)
            );

            for (Document doc : getCollection().find(filter).projection(new Document("roomId", 1))) {
                ObjectId rId = doc.getObjectId("roomId");
                if (rId != null) {
                    unavailableIds.add(rId);
                }
            }
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error finding unavailable room IDs for dates " + requestedCheckIn + " - " + requestedCheckOut, e);
        }

        return new ArrayList<>(unavailableIds);
    }

    /**
     * Updates the status of a reservation.
     */
    public boolean updateStatus(ObjectId bookingId, BookingStatus newStatus) {
        if (bookingId == null || newStatus == null) {
            return false;
        }
        try {
            Date now = new Date();
            Bson updates;
            if (newStatus == BookingStatus.CANCELLED) {
                updates = combine(
                        set("status", newStatus.name()),
                        set("updatedAt", now),
                        set("cancelledAt", now)
                );
            } else {
                updates = combine(
                        set("status", newStatus.name()),
                        set("updatedAt", now)
                );
            }

            return getCollection().updateOne(eq("_id", bookingId), updates).getModifiedCount() > 0;
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error updating booking status for id: " + bookingId, e);
            return false;
        }
    }

    /**
     * Retrieves all reservations with filtering and pagination for administrator view.
     */
    public List<Booking> findAll(int skip, int limit, String statusFilter, String keyword) {
        List<Booking> bookings = new ArrayList<>();
        try {
            List<Bson> filters = new ArrayList<>();

            if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter.trim())) {
                filters.add(eq("status", statusFilter.trim().toUpperCase()));
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                String regex = "(?i)" + java.util.regex.Pattern.quote(keyword.trim());
                filters.add(regex("bookingNumber", regex));
            }

            Bson combinedFilter = filters.isEmpty() ? new Document() : (filters.size() == 1 ? filters.get(0) : and(filters));

            for (Document doc : getCollection().find(combinedFilter)
                    .sort(descending("createdAt"))
                    .skip(Math.max(0, skip))
                    .limit(Math.max(1, limit))) {
                bookings.add(toEntity(doc));
            }
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error querying all bookings", e);
        }
        return bookings;
    }

    public long countAll() {
        try {
            return getCollection().countDocuments();
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error counting total bookings", e);
            return 0;
        }
    }

    public long countByStatus(BookingStatus status) {
        if (status == null) {
            return 0;
        }
        try {
            return getCollection().countDocuments(eq("status", status.name()));
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error counting bookings by status: " + status, e);
            return 0;
        }
    }

    /**
     * Aggregates total revenue from non-cancelled bookings.
     */
    public BigDecimal calculateTotalRevenue() {
        try {
            Bson matchFilter = match(ne("status", BookingStatus.CANCELLED.name()));
            Bson groupStage = group(null, sum("totalRevenue", "$totalAmount"));

            AggregateIterable<Document> result = getCollection().aggregate(Arrays.asList(matchFilter, groupStage));
            Document first = result.first();
            if (first != null) {
                Object revObj = first.get("totalRevenue");
                if (revObj instanceof Decimal128) {
                    return ((Decimal128) revObj).bigDecimalValue();
                } else if (revObj instanceof Number) {
                    return BigDecimal.valueOf(((Number) revObj).doubleValue());
                }
            }
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error calculating total revenue", e);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Checks if a room has any historical reservations (used to prevent unsafe hard deletion).
     */
    public boolean hasBookingsForRoom(ObjectId roomId) {
        if (roomId == null) {
            return false;
        }
        try {
            return getCollection().countDocuments(eq("roomId", roomId)) > 0;
        } catch (MongoException e) {
            LOGGER.log(Level.SEVERE, "Error checking bookings for room: " + roomId, e);
            return true; // fail-safe
        }
    }

    public static Document toDocument(Booking booking) {
        Document doc = new Document();
        if (booking.getId() != null) {
            doc.append("_id", booking.getId());
        }
        doc.append("bookingNumber", booking.getBookingNumber());
        doc.append("userId", booking.getUserId());
        doc.append("roomId", booking.getRoomId());
        doc.append("checkIn", booking.getCheckIn());
        doc.append("checkOut", booking.getCheckOut());
        doc.append("numberOfGuests", booking.getNumberOfGuests());
        doc.append("numberOfNights", booking.getNumberOfNights());
        doc.append("pricePerNightAtBooking", new Decimal128(booking.getPricePerNightAtBooking()));
        doc.append("subtotal", new Decimal128(booking.getSubtotal()));
        doc.append("taxes", new Decimal128(booking.getTaxes()));
        doc.append("totalAmount", new Decimal128(booking.getTotalAmount()));
        doc.append("status", booking.getStatus() != null ? booking.getStatus().name() : BookingStatus.CONFIRMED.name());
        doc.append("createdAt", booking.getCreatedAt() != null ? booking.getCreatedAt() : new Date());
        doc.append("updatedAt", booking.getUpdatedAt() != null ? booking.getUpdatedAt() : new Date());
        if (booking.getCancelledAt() != null) {
            doc.append("cancelledAt", booking.getCancelledAt());
        }
        return doc;
    }

    public static Booking toEntity(Document doc) {
        if (doc == null) {
            return null;
        }
        Booking b = new Booking();
        b.setId(doc.getObjectId("_id"));
        b.setBookingNumber(doc.getString("bookingNumber"));
        b.setUserId(doc.getObjectId("userId"));
        b.setRoomId(doc.getObjectId("roomId"));
        b.setCheckIn(doc.getString("checkIn"));
        b.setCheckOut(doc.getString("checkOut"));
        b.setNumberOfGuests(doc.getInteger("numberOfGuests", 1));
        b.setNumberOfNights(doc.getInteger("numberOfNights", 1));

        b.setPricePerNightAtBooking(extractBigDecimal(doc.get("pricePerNightAtBooking")));
        b.setSubtotal(extractBigDecimal(doc.get("subtotal")));
        b.setTaxes(extractBigDecimal(doc.get("taxes")));
        b.setTotalAmount(extractBigDecimal(doc.get("totalAmount")));

        b.setStatus(BookingStatus.fromString(doc.getString("status")));
        b.setCreatedAt(doc.getDate("createdAt"));
        b.setUpdatedAt(doc.getDate("updatedAt"));
        b.setCancelledAt(doc.getDate("cancelledAt"));
        return b;
    }

    private static BigDecimal extractBigDecimal(Object obj) {
        if (obj instanceof Decimal128) {
            return ((Decimal128) obj).bigDecimalValue();
        } else if (obj instanceof Number) {
            return BigDecimal.valueOf(((Number) obj).doubleValue());
        }
        return BigDecimal.ZERO;
    }
}

