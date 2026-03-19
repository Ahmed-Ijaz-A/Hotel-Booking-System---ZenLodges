package com.xcoders.model;

import java.sql.Timestamp;

/**
 * Represents a hotel image (main or reference photos).
 * Maps to the hotel_images table.
 */
public class HotelImage {

    private int imageId;
    private int hotelId;
    private String imagePath;
    private String imageType;      // 'MAIN' or 'REFERENCE'
    private boolean isPrimary;
    private Timestamp uploadedAt;

    // ── Constructors ────────────────────────────────────

    public HotelImage() {
    }

    public HotelImage(int imageId, int hotelId, String imagePath, String imageType, boolean isPrimary, Timestamp uploadedAt) {
        this.imageId = imageId;
        this.hotelId = hotelId;
        this.imagePath = imagePath;
        this.imageType = imageType;
        this.isPrimary = isPrimary;
        this.uploadedAt = uploadedAt;
    }

    public HotelImage(int hotelId, String imagePath, String imageType) {
        this.hotelId = hotelId;
        this.imagePath = imagePath;
        this.imageType = imageType;
        this.isPrimary = imageType.equals("MAIN");
    }

    // ── Getters & Setters ───────────────────────────────

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public Timestamp getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Timestamp uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    // ── Helper Methods ──────────────────────────────────

    public boolean isMainImage() {
        return "MAIN".equals(imageType);
    }

    public boolean isReferenceImage() {
        return "REFERENCE".equals(imageType);
    }

    @Override
    public String toString() {
        return "HotelImage{" +
                "imageId=" + imageId +
                ", hotelId=" + hotelId +
                ", imagePath='" + imagePath + '\'' +
                ", imageType='" + imageType + '\'' +
                ", uploadedAt=" + uploadedAt +
                '}';
    }
}
