package com.xcoders;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class for managing the MySQL database connection
 * for the ZenLodges hotel booking system.
 *
 * Connection settings are loaded from {@code db.properties} on the
 * classpath (src/main/resources/db.properties).  That file is
 * git-ignored so every developer keeps their own local credentials.
 * If the file is missing, sensible defaults are used (XAMPP defaults).
 *
 * To set up your own copy, duplicate {@code db.properties.example}
 * (in the same resources folder) and rename it to {@code db.properties}.
 */
public final class DBConnection {

    // ── Load from db.properties, fall back to XAMPP defaults ──
    private static final String HOST;
    private static final String PORT;
    private static final String DATABASE;
    private static final String USER;
    private static final String PASSWORD;
    private static final String URL;

    static {
        Properties props = new Properties();
        try (InputStream in = DBConnection.class
                .getResourceAsStream("/db.properties")) {
            if (in != null) {
                props.load(in);
                System.out.println("[DBConnection] Loaded settings from db.properties");
            } else {
                System.out.println("[DBConnection] db.properties not found – using defaults (XAMPP).");
                System.out.println("[DBConnection] Copy db.properties.example → db.properties and fill in your values.");
            }
        } catch (IOException e) {
            System.err.println("[DBConnection] Could not read db.properties: " + e.getMessage());
        }

        HOST     = props.getProperty("db.host",     "localhost");
        PORT     = props.getProperty("db.port",     "3306");
        DATABASE = props.getProperty("db.name",     "hotel_booking_system");
        USER     = props.getProperty("db.user",     "root");
        PASSWORD = props.getProperty("db.password", "");
        URL      = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
                 + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    private static Connection connection;

    // Prevent instantiation
    private DBConnection() { }

    /**
     * Returns the shared database connection.
     * Creates a new one if the current connection is null or closed.
     *
     * @return a live {@link Connection} to hotel_booking_system
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connected successfully.");
            } catch (SQLException e) {
                System.err.println("Database connection failed!");
                System.err.println("URL  : " + URL);
                System.err.println("Error: " + e.getMessage());
                throw e;   // re-throw so the caller can react
            }
        }
        return connection;
    }

    /**
     * Closes the shared database connection if it is open.
     * Safe to call even when the connection is already closed or null.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Database connection closed.");
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
