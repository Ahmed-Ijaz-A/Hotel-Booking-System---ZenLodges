package com.xcoders.model;

import java.sql.Date;

/**
 * Data Transfer Object for booking details with related room and hotel information.
 * Used for displaying bookings in the customer dashboard.
 */
public class BookingDetail {

    private int bookingId;
    private String hotelName;
    private String roomNumber;
    private String roomType;
    private Date checkIn;
    private Date checkOut;
    private String status;

    // ── No-arg constructor ──────────────────────────────────
    public BookingDetail() { }

    // ── Full constructor ─────────────────────────────────────
    public BookingDetail(int bookingId, String hotelName, String roomNumber, 
                        String roomType, Date checkIn, Date checkOut, String status) {
        this.bookingId   = bookingId;
        this.hotelName   = hotelName;
        this.roomNumber  = roomNumber;
        this.roomType    = roomType;
        this.checkIn     = checkIn;
        this.checkOut    = checkOut;
        this.status      = status;
    }

    // ── Getters & Setters ──────────────────────────────────

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public Date getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Date checkIn) {
        this.checkIn = checkIn;
    }

    public Date getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Date checkOut) {
        this.checkOut = checkOut;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BookingDetail{" +
                "bookingId=" + bookingId +
                ", hotelName='" + hotelName + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                ", roomType='" + roomType + '\'' +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", status='" + status + '\'' +
                '}';
    }
}
