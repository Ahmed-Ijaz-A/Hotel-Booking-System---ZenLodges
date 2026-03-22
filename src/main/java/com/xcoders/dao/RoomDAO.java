package com.xcoders.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.xcoders.DBConnection;
import com.xcoders.model.Room;
import com.xcoders.util.DataSqlExporter;

/**
 * Data-Access Object for the {@code rooms} table.
 * All database interaction for rooms is centralised here.
 */
public class RoomDAO {

    // ── Add Room ───────────────────────────────────────────

    /**
     * Inserts a new room into the database.
     *
     * @return {@code true} if the row was inserted, {@code false} otherwise.
     */
    public boolean addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, type, price, status) VALUES (?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, room.getRoomNumber());
                ps.setString(2, room.getType());
                ps.setDouble(3, room.getPrice());
                ps.setString(4, room.getStatus());

                boolean success = ps.executeUpdate() > 0;
                if (success) {
                    DataSqlExporter.exportSnapshot();
                }
                return success;
            }
        } catch (SQLException e) {
            System.err.println("Error adding room: " + e.getMessage());
        }

        return false;
    }

    // ── Get All Rooms ──────────────────────────────────────

    /**
     * Retrieves every room from the database.
     *
     * @return a list of {@link Room} objects (never {@code null}; empty if none found or on error).
     */
    public List<Room> getAllRooms() {
        String sql = "SELECT * FROM rooms ORDER BY room_id";
        List<Room> rooms = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    rooms.add(extractRoom(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching rooms: " + e.getMessage());
        }

        return rooms;
    }

    // ── Room-number-exists check ───────────────────────────

    /**
     * Checks whether a room number is already registered.
     *
     * @return {@code true} if the room number exists in the database.
     */
    public boolean roomNumberExists(String roomNumber) {
        String sql = "SELECT 1 FROM rooms WHERE room_number = ?";

        try {
            Connection conn = DBConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, roomNumber);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking room number: " + e.getMessage());
        }

        return false;
    }

    // ── Helper ─────────────────────────────────────────────

    /**
     * Maps the current row of a {@link ResultSet} to a {@link Room} object.
     * Keeps ResultSet handling private to the DAO layer.
     */
    private Room extractRoom(ResultSet rs) throws SQLException {
        return new Room(
                rs.getInt("room_id"),
                rs.getString("room_number"),
                rs.getString("type"),
                rs.getDouble("price"),
                rs.getString("status"),
                rs.getTimestamp("created_at")
        );
    }
}
