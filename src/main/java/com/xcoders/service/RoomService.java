package com.xcoders.service;

import java.util.List;

import com.xcoders.dao.RoomDAO;
import com.xcoders.model.Room;

/**
 * Service layer for room-related operations.
 * Sits between controllers/UI and the DAO — contains business rules,
 * no SQL or UI logic.
 */
public class RoomService {

    private final RoomDAO roomDAO = new RoomDAO();

    // ── Add Room ───────────────────────────────────────────

    /**
     * Adds a new room after validating inputs and checking
     * for duplicate room numbers.
     *
     * @return {@code true} if the room was added, {@code false} otherwise.
     */
    public boolean addRoom(String roomNumber, String type, double price, String status) {
        if (roomNumber == null || roomNumber.isBlank()
                || type == null || type.isBlank()
                || status == null || status.isBlank()) {
            System.err.println("Add room failed: all fields are required.");
            return false;
        }

        if (price < 0) {
            System.err.println("Add room failed: price cannot be negative.");
            return false;
        }

        if (roomDAO.roomNumberExists(roomNumber.trim())) {
            System.err.println("Add room failed: room number already exists.");
            return false;
        }

        Room newRoom = new Room(roomNumber.trim(), type.trim(), price, status.trim());
        boolean created = roomDAO.addRoom(newRoom);

        if (!created) {
            System.err.println("Add room failed: could not save room.");
        }

        return created;
    }

    // ── Get All Rooms ──────────────────────────────────────

    /**
     * Retrieves all rooms from the database.
     *
     * @return a list of {@link Room} objects (never {@code null}).
     */
    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }
}
