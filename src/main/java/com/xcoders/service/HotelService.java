package com.xcoders.service;

import java.util.List;

import com.xcoders.dao.HotelDAO;
import com.xcoders.model.Hotel;

/**
 * Service class for hotel-related business logic.
 * Handles validation and coordination between DAO and controllers.
 */
public class HotelService {

    private final HotelDAO hotelDAO = new HotelDAO();

    /**
     * Register a new hotel (creates a PENDING hotel)
     * Validates: Hotel name + location must be unique
     *
     * @param hotel Hotel object with required fields
     * @return hotel ID if successful, -1 if validation fails
     * @throws IllegalArgumentException if validation fails
     */
    public int registerHotel(Hotel hotel) throws IllegalArgumentException {
        // Validate required fields
        if (hotel.getName() == null || hotel.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Hotel name is required");
        }
        if (hotel.getLocation() == null || hotel.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Hotel location is required");
        }
        if (hotel.getType() == null || hotel.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Hotel type is required");
        }
        if (hotel.getHotelAdminId() <= 0) {
            throw new IllegalArgumentException("Valid hotel admin ID is required");
        }

        // Check for duplicate hotel name in same location
        if (hotelDAO.hotelExists(hotel.getName().trim(), hotel.getLocation().trim())) {
            throw new IllegalArgumentException("A hotel with this name already exists in " + hotel.getLocation());
        }

        // Attempt to add hotel to database
        return hotelDAO.addHotel(hotel);
    }

    /**
     * Get all pending hotels (for platform admin to review)
     */
    public List<Hotel> getPendingHotels() {
        return hotelDAO.getPendingHotels();
    }

    /**
     * Get all approved hotels
     */
    public List<Hotel> getApprovedHotels() {
        return hotelDAO.getApprovedHotels();
    }

    /**
     * Get all hotels regardless of status
     */
    public List<Hotel> getAllHotels() {
        return hotelDAO.getAllHotels();
    }
    /**
     * Get hotels by specific status (PENDING, APPROVED, REJECTED)
     */
    public List<Hotel> getHotelsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return List.of();
        }
        return hotelDAO.getHotelsByStatus(status.toUpperCase());
    }
    /**
     * Get hotels by location (only approved hotels)
     */
    public List<Hotel> getHotelsByLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            return List.of();
        }
        return hotelDAO.getHotelsByLocation(location.trim());
    }

    /**
     * Get hotels managed by a specific hotel admin
     */
    public List<Hotel> getHotelsByAdminId(int adminId) {
        return hotelDAO.getHotelsByAdminId(adminId);
    }

    /**
     * Get hotel by ID with full details
     */
    public Hotel getHotelById(int hotelId) {
        return hotelDAO.getHotelById(hotelId);
    }
    
    /**
     * Approve a hotel (platform admin action)
     *
     * @param hotelId Hotel ID to approve
     * @param platformAdminId User ID of the platform admin approving
     * @return true if successful
     */
    public boolean approveHotel(int hotelId, int platformAdminId) {
        Hotel hotel = hotelDAO.getHotelById(hotelId);
        if (hotel == null) {
            System.err.println("Hotel not found: " + hotelId);
            return false;
        }
        if (!hotel.isPending()) {
            System.err.println("Hotel is not in PENDING status");
            return false;
        }
        return hotelDAO.approveHotel(hotelId, platformAdminId);
    }

    /**
     * Reject a hotel (platform admin action)
     *
     * @param hotelId Hotel ID to reject
     * @param platformAdminId User ID of the platform admin rejecting
     * @return true if successful
     */
    public boolean rejectHotel(int hotelId, int platformAdminId) {
        Hotel hotel = hotelDAO.getHotelById(hotelId);
        if (hotel == null) {
            System.err.println("Hotel not found: " + hotelId);
            return false;
        }
        if (!hotel.isPending()) {
            System.err.println("Hotel is not in PENDING status");
            return false;
        }
        return hotelDAO.rejectHotel(hotelId, platformAdminId);
    }

    /**
     * Check if a hotel admin can manage a specific hotel
     */
    public boolean canManageHotel(int hotelId, int adminId) {
        Hotel hotel = hotelDAO.getHotelById(hotelId);
        if (hotel == null) return false;

        // Must be the hotel admin AND hotel must be approved
        return hotel.getHotelAdminId() == adminId && hotel.isApproved();
    }
}
