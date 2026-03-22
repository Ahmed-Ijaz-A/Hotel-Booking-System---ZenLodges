package com.xcoders.dao;

import com.xcoders.DBConnection;
import com.xcoders.model.HotelImage;
import com.xcoders.util.DataSqlExporter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for hotel images.
 * Manages image records in the hotel_images table.
 */
public class HotelImageDAO {

    /**
     * Add a new hotel image record
     */
    public boolean addImage(HotelImage image) {
        String sql = "INSERT INTO hotel_images (hotel_id, image_path, image_type, is_primary) VALUES (?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, image.getHotelId());
                ps.setString(2, image.getImagePath());
                ps.setString(3, image.getImageType());
                ps.setBoolean(4, image.isPrimary());

                boolean success = ps.executeUpdate() > 0;
                if (success) {
                    DataSqlExporter.exportSnapshot();
                }
                return success;
            }
        } catch (SQLException e) {
            System.err.println("Error adding hotel image: " + e.getMessage());
            throw new RuntimeException("Database error while adding hotel image.", e);
        }
    }

    /**
     * Get main image for a hotel
     */
    public HotelImage getMainImage(int hotelId) {
        String sql = "SELECT * FROM hotel_images WHERE hotel_id = ? AND image_type = 'MAIN' LIMIT 1";

        try {
            Connection conn = DBConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, hotelId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return extractImage(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting main image: " + e.getMessage());
            throw new RuntimeException("Database error while fetching main image.", e);
        }
        return null;
    }

    /**
     * Get all reference images for a hotel
     */
    public List<HotelImage> getReferenceImages(int hotelId) {
        List<HotelImage> images = new ArrayList<>();
        String sql = "SELECT * FROM hotel_images WHERE hotel_id = ? AND image_type = 'REFERENCE' ORDER BY uploaded_at";

        try {
            Connection conn = DBConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, hotelId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        images.add(extractImage(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting reference images: " + e.getMessage());
            throw new RuntimeException("Database error while fetching reference images.", e);
        }
        return images;
    }

    /**
     * Get all images for a hotel
     */
    public List<HotelImage> getHotelImages(int hotelId) {
        List<HotelImage> images = new ArrayList<>();
        String sql = "SELECT * FROM hotel_images WHERE hotel_id = ? ORDER BY image_type DESC, uploaded_at";

        try {
            Connection conn = DBConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, hotelId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        images.add(extractImage(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting hotel images: " + e.getMessage());
            throw new RuntimeException("Database error while fetching hotel images.", e);
        }
        return images;
    }

    /**
     * Delete an image record
     */
    public boolean deleteImage(int imageId) {
        String sql = "DELETE FROM hotel_images WHERE image_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, imageId);
                boolean success = ps.executeUpdate() > 0;
                if (success) {
                    DataSqlExporter.exportSnapshot();
                }
                return success;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting image: " + e.getMessage());
            throw new RuntimeException("Database error while deleting image.", e);
        }
    }

    /**
     * Extract HotelImage from ResultSet
     */
    private HotelImage extractImage(ResultSet rs) throws SQLException {
        return new HotelImage(
                rs.getInt("image_id"),
                rs.getInt("hotel_id"),
                rs.getString("image_path"),
                rs.getString("image_type"),
                rs.getBoolean("is_primary"),
                rs.getTimestamp("uploaded_at")
        );
    }
}
