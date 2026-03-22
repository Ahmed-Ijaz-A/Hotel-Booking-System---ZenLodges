package com.xcoders.dao;

import com.xcoders.DBConnection;
import com.xcoders.model.Booking;
import com.xcoders.model.BookingDetail;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for booking operations.
 * Handles CRUD operations and availability checks.
 */
public class BookingDAO {

    /**
     * Creates a new booking in the database.
     *
     * @param booking the booking to insert
     * @return the generated booking ID, or -1 if insertion failed
     * @throws SQLException if a database error occurs
     */
    public int addBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings (room_id, user_id, check_in, check_out, status) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, booking.getRoomId());
            pstmt.setInt(2, booking.getUserId());
            pstmt.setDate(3, booking.getCheckIn());
            pstmt.setDate(4, booking.getCheckOut());
            pstmt.setString(5, booking.getStatus());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Retrieves a booking by its ID.
     *
     * @param bookingId the booking ID
     * @return the Booking object, or null if not found
     * @throws SQLException if a database error occurs
     */
    public Booking getBookingById(int bookingId) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookingId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractBooking(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all bookings for a specific room.
     *
     * @param roomId the room ID
     * @return a list of Booking objects
     * @throws SQLException if a database error occurs
     */
    public List<Booking> getBookingsByRoomId(int roomId) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE room_id = ? AND status = 'CONFIRMED' ORDER BY check_in ASC";

        List<Booking> bookings = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(extractBooking(rs));
                }
            }
        }
        return bookings;
    }

    /**
     * Retrieves all bookings for a specific user.
     *
     * @param userId the user ID
     * @return a list of Booking objects
     * @throws SQLException if a database error occurs
     */
    public List<Booking> getBookingsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY check_in DESC";

        List<Booking> bookings = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(extractBooking(rs));
                }
            }
        }
        return bookings;
    }

    /**
     * Checks if a room has overlapping bookings for the requested date range.
     * Uses the logic: (new_check_in < existing_check_out AND new_check_out > existing_check_in)
     *
     * @param roomId the room ID
     * @param checkIn the requested check-in date
     * @param checkOut the requested check-out date
     * @return true if there is an overlapping booking, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean hasOverlappingBooking(int roomId, Date checkIn, Date checkOut) throws SQLException {
        // Calculate overlap: (new_check_in < existing_check_out) AND (new_check_out > existing_check_in)
        String sql = "SELECT COUNT(*) as overlap_count FROM bookings " +
                     "WHERE room_id = ? " +
                     "AND status = 'CONFIRMED' " +
                     "AND ? < check_out " +
                     "AND ? > check_in";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            pstmt.setDate(2, checkIn);
            pstmt.setDate(3, checkOut);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("overlap_count") > 0;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a room is available for the requested date range.
     * A room is available if there are no overlapping confirmed bookings.
     *
     * @param roomId the room ID
     * @param checkIn the requested check-in date
     * @param checkOut the requested check-out date
     * @return true if the room is available, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean isRoomAvailable(int roomId, Date checkIn, Date checkOut) throws SQLException {
        return !hasOverlappingBooking(roomId, checkIn, checkOut);
    }

    /**
     * Cancels a booking by updating its status to 'CANCELLED'.
     *
     * @param bookingId the booking ID
     * @return true if the update was successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean cancelBooking(int bookingId) throws SQLException {
        String sql = "UPDATE bookings SET status = 'CANCELLED' WHERE booking_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookingId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves booking details for a user with associated hotel and room information.
     * Joins Booking → Room → Hotel tables.
     *
     * @param userId the user ID
     * @return a list of BookingDetail objects with hotel and room information
     * @throws SQLException if a database error occurs
     */
    public List<BookingDetail> getBookingDetailsForUser(int userId) throws SQLException {
        String sql = "SELECT b.booking_id, h.name as hotel_name, r.room_number, r.type as room_type, " +
                     "       b.check_in, b.check_out, b.status " +
                     "FROM bookings b " +
                     "JOIN rooms r ON b.room_id = r.room_id " +
                     "JOIN hotels h ON r.hotel_id = h.hotel_id " +
                     "WHERE b.user_id = ? " +
                     "ORDER BY b.check_in DESC";

        List<BookingDetail> bookingDetails = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookingDetails.add(extractBookingDetail(rs));
                }
            }
        }
        return bookingDetails;
    }

    /**
     * Extracts a BookingDetail object from a ResultSet row.
     *
     * @param rs the ResultSet
     * @return a BookingDetail object
     * @throws SQLException if a database error occurs
     */
    private BookingDetail extractBookingDetail(ResultSet rs) throws SQLException {
        return new BookingDetail(
                rs.getInt("booking_id"),
                rs.getString("hotel_name"),
                rs.getString("room_number"),
                rs.getString("room_type"),
                rs.getDate("check_in"),
                rs.getDate("check_out"),
                rs.getString("status")
        );
    }

    /**
     * Extracts a Booking object from a ResultSet row.
     *
     * @param rs the ResultSet
     * @return a Booking object
     * @throws SQLException if a database error occurs
     */
        /**
         * Retrieves booking details for a specific hotel with room information.
         * Joins Booking → Room → Hotel tables, filtering by hotel_id.
         *
         * @param hotelId the hotel ID
         * @return a list of BookingDetail objects for the hotel
         * @throws SQLException if a database error occurs
         */
        public List<BookingDetail> getBookingsByHotelId(int hotelId) throws SQLException {
            String sql = "SELECT b.booking_id, h.name as hotel_name, r.room_number, r.type as room_type, " +
                         "       b.check_in, b.check_out, b.status " +
                         "FROM bookings b " +
                         "JOIN rooms r ON b.room_id = r.room_id " +
                         "JOIN hotels h ON r.hotel_id = h.hotel_id " +
                         "WHERE r.hotel_id = ? " +
                         "ORDER BY b.check_in DESC";

            List<BookingDetail> bookingDetails = new ArrayList<>();

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, hotelId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        bookingDetails.add(extractBookingDetail(rs));
                    }
                }
            }
            return bookingDetails;
        }

    private Booking extractBooking(ResultSet rs) throws SQLException {
        return new Booking(
                rs.getInt("booking_id"),
                rs.getInt("room_id"),
                rs.getInt("user_id"),
                rs.getDate("check_in"),
                rs.getDate("check_out"),
                rs.getString("status"),
                rs.getTimestamp("created_at")
        );
    }
}
