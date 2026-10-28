package com.hotelreservation.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton database connection provider managing a single application-wide MongoClient.
 * Avoids creating a new client on every servlet request, conserving network and memory resources.
 */
public final class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static final Object LOCK = new Object();

    private DatabaseConnection() {
        // Prevent instantiation
    }

    /**
     * Initializes the MongoClient and connects to the configured database.
     */
    public static void initialize() {
        synchronized (LOCK) {
            if (mongoClient != null) {
                LOGGER.info("MongoClient is already initialized.");
                return;
            }

            String uri = AppConfig.getMongoUri();
            String dbName = AppConfig.getDatabaseName();

            try {
                LOGGER.info("Connecting to MongoDB URI: " + maskUriForLogging(uri));

                MongoClientSettings settings = MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(uri))
                        .applyToSocketSettings(builder ->
                                builder.connectTimeout(5000, TimeUnit.MILLISECONDS)
                                       .readTimeout(10000, TimeUnit.MILLISECONDS))
                        .applyToClusterSettings(builder ->
                                builder.serverSelectionTimeout(5000, TimeUnit.MILLISECONDS))
                        .build();

                mongoClient = MongoClients.create(settings);
                database = mongoClient.getDatabase(dbName);

                // Ping the database to verify connectivity immediately
                database.runCommand(new org.bson.Document("ping", 1));
                LOGGER.info("Successfully established connection to MongoDB database: " + dbName);
            } catch (MongoException e) {
                LOGGER.log(Level.SEVERE, "Failed to connect to MongoDB at URI: " + maskUriForLogging(uri), e);
                // We keep database reference available, but log the warning clearly
            }
        }
    }

    /**
     * Retrieves the application MongoDatabase instance.
     * Auto-initializes if not previously started.
     */
    public static MongoDatabase getDatabase() {
        if (database == null) {
            initialize();
        }
        return database;
    }

    /**
     * Safely closes the single application-wide MongoClient.
     */
    public static void close() {
        synchronized (LOCK) {
            if (mongoClient != null) {
                try {
                    LOGGER.info("Closing application MongoClient...");
                    mongoClient.close();
                    LOGGER.info("MongoClient closed successfully.");
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error closing MongoClient", e);
                } finally {
                    mongoClient = null;
                    database = null;
                }
            }
        }
    }

    /**
     * Masks any password in the connection string to prevent leaking credentials into logs.
     */
    private static String maskUriForLogging(String uri) {
        if (uri == null) {
            return "null";
        }
        return uri.replaceAll("://([^:]+):([^@]+)@", "://$1:****@");
    }
}

