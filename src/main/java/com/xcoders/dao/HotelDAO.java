package com.xcoders.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.xcoders.DBConnection;
import com.xcoders.model.Hotel;
import com.xcoders.util.DataSqlExporter;

/**
 * Data Access Object for Hotel operations.
 * Handles database interactions for the hotels table.
 */
public class HotelDAO {

    /**
     * Add a new hotel registration (status: PENDING)
     *
     * @param hotel Hotel object with name, location, type, description, hotelAdminId
     * @return hotel ID if successful, -1 if failed
     */
    public int addHotel(Hotel hotel) {
        String sql = "INSERT INTO hotels (name, location, type, description, status, hotel_admin_id, created_at) " +
                     "VALUES (?, ?, ?, ?, 'PENDING', ?, NOW())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, hotel.getName());
            pstmt.setString(2, hotel.getLocation());
            pstmt.setString(3, hotel.getType());
            pstmt.setString(4, hotel.getDescription());
            pstmt.setInt(5, hotel.getHotelAdminId());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        DataSqlExporter.exportSnapshot();
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding hotel: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Get all hotels regardless of status
     */
    public List<Hotel> getAllHotels() {
        List<Hotel> hotels = new ArrayList<>();
        String sql = "SELECT * FROM hotels ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                hotels.add(mapResultSetToHotel(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all hotels: " + e.getMessage());
        }
        return hotels;
    }

    /**
     * Get hotels by status (PENDING, APPROVED, REJECTED)
     */
    public List<Hotel> getHotelsByStatus(String status) {
        List<Hotel> hotels = new ArrayList<>();
        String sql = "SELECT * FROM hotels WHERE status = ? ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.toUpperCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    hotels.add(mapResultSetToHotel(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching hotels by status: " + e.getMessage());
        }
        return hotels;
    }

    /**
     * Get pending hotels (for platform admin approval)
     */
    public List<Hotel> getPendingHotels() {
        return getHotelsByStatus("PENDING");
    }

    /**
     * Get approved hotels
     */
    public List<Hotel> getApprovedHotels() {
        return getHotelsByStatus("APPROVED");
    }

    /**
     * Get hotels by location
     */
    public List<Hotel> getHotelsByLocation(String location) {
        List<Hotel> hotels = new ArrayList<>();
        String sql = "SELECT * FROM hotels WHERE location = ? AND status = 'APPROVED' ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, location);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    hotels.add(mapResultSetToHotel(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching hotels by location: " + e.getMessage());
        }
        return hotels;
    }

    /**
     * Get hotels by hotel admin ID (for hotel admin to see their hotel)
     */
    public List<Hotel> getHotelsByAdminId(int adminId) {
        List<Hotel> hotels = new ArrayList<>();
        String sql = "SELECT * FROM hotels WHERE hotel_admin_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, adminId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    hotels.add(mapResultSetToHotel(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching hotels by admin ID: " + e.getMessage());
        }
        return hotels;
    }

    /**
     * Get hotel by ID
     */
    public Hotel getHotelById(int hotelId) {
        String sql = "SELECT * FROM hotels WHERE hotel_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, hotelId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToHotel(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching hotel by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Approve a hotel (called by platform admin)
     */
    public boolean approveHotel(int hotelId, int platformAdminId) {
        String sql = "UPDATE hotels SET status = 'APPROVED', approved_by = ?, approved_at = NOW() WHERE hotel_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, platformAdminId);
            pstmt.setInt(2, hotelId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                DataSqlExporter.exportSnapshot();
            }
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error approving hotel: " + e.getMessage());
        }
        return false;
    }

    /**
     * Reject a hotel (called by platform admin)
     */
    public boolean rejectHotel(int hotelId, int platformAdminId) {
        String sql = "UPDATE hotels SET status = 'REJECTED', approved_by = ?, approved_at = NOW() WHERE hotel_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, platformAdminId);
            pstmt.setInt(2, hotelId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                DataSqlExporter.exportSnapshot();
            }
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error rejecting hotel: " + e.getMessage());
        }
        return false;
    }

    /**
     * Check if hotel name + location combination already exists
     */
    public boolean hotelExists(String name, String location) {
        String sql = "SELECT 1 FROM hotels WHERE name = ? AND location = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, location);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking if hotel exists: " + e.getMessage());
        }
        return false;
    }

    /**
     * Helper method to map ResultSet row to Hotel object
     */
    private Hotel mapResultSetToHotel(ResultSet rs) throws SQLException {
        return new Hotel(
            rs.getInt("hotel_id"),
            rs.getString("name"),
            rs.getString("location"),
            rs.getString("type"),
            rs.getString("description"),
            rs.getString("status"),
            rs.getInt("hotel_admin_id"),
            rs.getObject("approved_by") != null ? rs.getInt("approved_by") : null,
            rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
            rs.getTimestamp("approved_at") != null ? rs.getTimestamp("approved_at").toLocalDateTime() : null
        );
    }
}
