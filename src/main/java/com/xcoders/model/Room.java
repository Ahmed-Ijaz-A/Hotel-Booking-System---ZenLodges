package com.xcoders.model;

import java.sql.Timestamp;

/**
 * Represents a room in the hotel booking system.
 * Maps to the {@code rooms} table.
 */
public class Room {

    private int roomId;
    private int hotelId;
    private String roomNumber;
    private String type;       // e.g. "Single", "Double", "Deluxe", "Suite"
    private double price;
    private String status;     // "AVAILABLE" or "MAINTENANCE"
    private Timestamp createdAt;

    // ── No-arg constructor ──────────────────────────────────
    public Room() { }

    // ── Full constructor (including generated fields) ───────
    public Room(int roomId, int hotelId, String roomNumber, String type,
                double price, String status, Timestamp createdAt) {
        this.roomId     = roomId;
        this.hotelId    = hotelId;
        this.roomNumber = roomNumber;
        this.type       = type;
        this.price      = price;
        this.status     = status;
        this.createdAt  = createdAt;
    }

    // ── Constructor for creating a new room (no id / timestamp) ─
    public Room(int hotelId, String roomNumber, String type, double price, String status) {
        this.hotelId    = hotelId;
        this.roomNumber = roomNumber;
        this.type       = type;
        this.price      = price;
        this.status     = status;
    }

    // ── Getters & Setters ──────────────────────────────────

    public int getHotelId() { return hotelId; }
    public void setHotelId(int hotelId) { this.hotelId = hotelId; }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    // ── toString ───────────────────────────────────────────

    @Override
    public String toString() {
        return "Room{" +
                "roomId=" + roomId +
                ", roomNumber='" + roomNumber + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
