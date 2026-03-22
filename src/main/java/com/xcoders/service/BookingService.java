package com.xcoders.service;

import com.xcoders.dao.BookingDAO;
import com.xcoders.model.Booking;
import com.xcoders.model.BookingDetail;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Service layer for booking operations.
 * Encapsulates business logic and availability checks.
 */
public class BookingService {

    private final BookingDAO bookingDAO = new BookingDAO();

    /**
     * Creates a new booking after verifying the room is available.
     *
     * @param roomId the room ID
     * @param userId the user ID
     * @param checkIn the check-in date
     * @param checkOut the check-out date
     * @return the booking ID if successful, -1 if the room is unavailable or an error occurs
     */
    public int bookRoom(int roomId, int userId, Date checkIn, Date checkOut) {
        try {
            // Validate dates
            if (checkIn == null || checkOut == null) {
                System.err.println("[BookingService] Check-in and check-out dates are required.");
                return -1;
            }

            if (!checkIn.before(checkOut)) {
                System.err.println("[BookingService] Check-in date must be before check-out date.");
                return -1;
            }

            // Check availability
            if (!bookingDAO.isRoomAvailable(roomId, checkIn, checkOut)) {
                System.err.println("[BookingService] Room " + roomId + " is not available for the requested dates.");
                return -1;
            }

            // Create the booking
            Booking booking = new Booking(roomId, userId, checkIn, checkOut, "CONFIRMED");
            return bookingDAO.addBooking(booking);

        } catch (SQLException e) {
            System.err.println("[BookingService] Error creating booking: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Checks if a room is available for the requested date range.
     *
     * @param roomId the room ID
     * @param checkIn the check-in date
     * @param checkOut the check-out date
     * @return true if available, false otherwise
     */
    public boolean checkRoomAvailability(int roomId, Date checkIn, Date checkOut) {
        try {
            if (checkIn == null || checkOut == null) {
                return false;
            }
            if (!checkIn.before(checkOut)) {
                return false;
            }
            return bookingDAO.isRoomAvailable(roomId, checkIn, checkOut);
        } catch (SQLException e) {
            System.err.println("[BookingService] Error checking availability: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all bookings for a specific room.
     *
     * @param roomId the room ID
     * @return a list of bookings
     */
    public List<Booking> getRoomBookings(int roomId) {
        try {
            return bookingDAO.getBookingsByRoomId(roomId);
        } catch (SQLException e) {
            System.err.println("[BookingService] Error retrieving room bookings: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Retrieves all bookings for a specific user.
     *
     * @param userId the user ID
     * @return a list of bookings
     */
    public List<Booking> getUserBookings(int userId) {
        try {
            return bookingDAO.getBookingsByUserId(userId);
        } catch (SQLException e) {
            System.err.println("[BookingService] Error retrieving user bookings: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Retrieves a booking by ID.
     *
     * @param bookingId the booking ID
     * @return the Booking object, or null if not found
     */
    public Booking getBookingById(int bookingId) {
        try {
            return bookingDAO.getBookingById(bookingId);
        } catch (SQLException e) {
            System.err.println("[BookingService] Error retrieving booking: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Cancels a booking.
     *
     * @param bookingId the booking ID
     * @return true if successful, false otherwise
     */
    public boolean cancelBooking(int bookingId) {
        try {
            return bookingDAO.cancelBooking(bookingId);
        } catch (SQLException e) {
            System.err.println("[BookingService] Error cancelling booking: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves booking details for a user with associated hotel and room information.
     *
     * @param userId the user ID
     * @return a list of BookingDetail objects
     */
    public List<BookingDetail> getUserBookingDetails(int userId) {
        try {
            return bookingDAO.getBookingDetailsForUser(userId);
        } catch (SQLException e) {
            System.err.println("[BookingService] Error retrieving booking details: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
        /**
         * Retrieves all bookings for a specific hotel (for hotel admin).
         *
         * @param hotelId the hotel ID
         * @return a list of BookingDetail objects for the hotel
         */
        public List<BookingDetail> getHotelBookings(int hotelId) {
            try {
                return bookingDAO.getBookingsByHotelId(hotelId);
            } catch (SQLException e) {
                System.err.println("[BookingService] Error retrieving hotel bookings: " + e.getMessage());
                e.printStackTrace();
                return List.of();
            }
        }
}
