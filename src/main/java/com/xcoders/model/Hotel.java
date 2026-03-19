package com.xcoders.model;

import java.time.LocalDateTime;

/**
 * Model class representing a Hotel in the ZenLodges system.
 */
public class Hotel {
    private int hotelId;
    private String name;
    private String location;
    private String type;
    private String description;
    private String status;           // 'PENDING', 'APPROVED', 'REJECTED'
    private int hotelAdminId;
    private Integer approvedBy;      // Platform Admin ID (nullable)
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt; // nullable

    // ── Constructors ──
    public Hotel() {
    }

    public Hotel(String name, String location, String type, String description, int hotelAdminId) {
        this.name = name;
        this.location = location;
        this.type = type;
        this.description = description;
        this.hotelAdminId = hotelAdminId;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    public Hotel(int hotelId, String name, String location, String type, String description, 
                 String status, int hotelAdminId, Integer approvedBy, LocalDateTime createdAt, LocalDateTime approvedAt) {
        this.hotelId = hotelId;
        this.name = name;
        this.location = location;
        this.type = type;
        this.description = description;
        this.status = status;
        this.hotelAdminId = hotelAdminId;
        this.approvedBy = approvedBy;
        this.createdAt = createdAt;
        this.approvedAt = approvedAt;
    }

    // ── Getters & Setters ──
    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getHotelAdminId() {
        return hotelAdminId;
    }

    public void setHotelAdminId(int hotelAdminId) {
        this.hotelAdminId = hotelAdminId;
    }

    public Integer getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    // ── Helper Methods ──
    public boolean isPending() {
        return "PENDING".equals(status);
    }

    public boolean isApproved() {
        return "APPROVED".equals(status);
    }

    public boolean isRejected() {
        return "REJECTED".equals(status);
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "hotelId=" + hotelId +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", hotelAdminId=" + hotelAdminId +
                ", createdAt=" + createdAt +
                '}';
    }
}
