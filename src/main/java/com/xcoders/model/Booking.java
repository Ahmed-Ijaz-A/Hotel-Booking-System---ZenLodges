package com.xcoders.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a booking in the hotel booking system.
 * Maps to the {@code bookings} table.
 */
public class Booking {

    private int bookingId;
    private int roomId;
    private int userId;
    private Date checkIn;
    private Date checkOut;
    private String status;     // "CONFIRMED" or "CANCELLED"
    private Timestamp createdAt;

    // ── No-arg constructor ──────────────────────────────────
    public Booking() { }

    // ── Full constructor (including generated fields) ───────
    public Booking(int bookingId, int roomId, int userId, Date checkIn,
                   Date checkOut, String status, Timestamp createdAt) {
        this.bookingId  = bookingId;
        this.roomId     = roomId;
        this.userId     = userId;
        this.checkIn    = checkIn;
        this.checkOut   = checkOut;
        this.status     = status;
        this.createdAt  = createdAt;
    }

    // ── Constructor for creating a new booking (no id / timestamp) ─
    public Booking(int roomId, int userId, Date checkIn, Date checkOut, String status) {
        this.roomId  = roomId;
        this.userId  = userId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status  = status;
    }

    // ── Getters & Setters ──────────────────────────────────

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", roomId=" + roomId +
                ", userId=" + userId +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
