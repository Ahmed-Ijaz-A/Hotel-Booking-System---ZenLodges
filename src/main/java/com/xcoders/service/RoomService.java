package com.xcoders.service;

import java.util.List;

import com.xcoders.dao.RoomDAO;
import com.xcoders.model.Hotel;
import com.xcoders.model.Room;

/**
 * Service layer for room-related operations.
 * Sits between controllers/UI and the DAO and contains business rules.
 */
public class RoomService {

    private final RoomDAO roomDAO = new RoomDAO();
    private final HotelService hotelService = new HotelService();

    /**
     * Adds a new room after validating inputs and checking for duplicate room numbers.
     */
    public boolean addRoom(int hotelId, String roomNumber, String type, double price, String status) {
        if (hotelId <= 0) {
            System.err.println("Add room failed: invalid hotel ID.");
            return false;
        }

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

        String normalizedRoomNumber = roomNumber.trim();
        if (roomDAO.roomNumberExists(normalizedRoomNumber, hotelId)) {
            System.err.println("Add room failed: room number already exists.");
            return false;
        }

        Room newRoom = new Room(hotelId, normalizedRoomNumber, type.trim(), price, status.trim());
        boolean created = roomDAO.addRoom(newRoom);
        if (!created) {
            System.err.println("Add room failed: could not save room.");
        }
        return created;
    }

    public List<Room> getRoomsByHotelId(int hotelId) {
        return roomDAO.getRoomsByHotelId(hotelId);
    }

    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }

    /**
     * Returns rooms that are available and belong to approved hotels.
     */
    public List<Room> getAvailableRoomsByApprovedHotels() {
        return roomDAO.getAllRooms().stream()
                .filter(room -> "AVAILABLE".equals(room.getStatus()))
                .filter(room -> {
                    Hotel hotel = hotelService.getHotelById(room.getHotelId());
                    return hotel != null && hotel.isApproved();
                })
                .toList();
    }
}
