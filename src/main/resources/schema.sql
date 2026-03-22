-- ─────────────────────────────────────────────────────────────
-- ZenLodges – Database Schema
-- Run this once to set up the database on any machine.
-- ─────────────────────────────────────────────────────────────

CREATE DATABASE IF NOT EXISTS hotel_booking_system
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE hotel_booking_system;

-- ── Users ──────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    user_id    INT          NOT NULL AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(20)  NOT NULL DEFAULT 'USER',   -- 'PLATFORM_ADMIN' | 'HOTEL_ADMIN' | 'USER'
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE', -- 'ACTIVE' | 'BLOCKED'
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id)
);

-- Default platform admin account (password: admin123 – change after first login)
INSERT IGNORE INTO users (name, email, password, role, status)
VALUES ('Platform Admin', 'admin@zenlodges.com', 'admin123', 'PLATFORM_ADMIN', 'ACTIVE');

-- ── Hotels ──────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS hotels (
    hotel_id       INT          NOT NULL AUTO_INCREMENT,
    name           VARCHAR(150) NOT NULL,
    location       VARCHAR(100) NOT NULL,
    type           VARCHAR(50)  NOT NULL,              -- 'Luxury' | 'Budget' | 'Mid-range' | 'Resort' | etc.
    description    TEXT,
    status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING', -- 'PENDING' | 'APPROVED' | 'REJECTED'
    hotel_admin_id INT          NOT NULL,
    approved_by    INT,                                -- Platform Admin ID
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at    TIMESTAMP,
    PRIMARY KEY (hotel_id),
    UNIQUE KEY (name, location),
    FOREIGN KEY (hotel_admin_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    FOREIGN KEY (approved_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_status (status),
    INDEX idx_hotel_admin (hotel_admin_id),
    INDEX idx_location (location)
);

-- ── Rooms ───────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS rooms (
    room_id     INT           NOT NULL AUTO_INCREMENT,
    hotel_id    INT           NOT NULL,
    room_number VARCHAR(10)   NOT NULL,
    type        VARCHAR(50)   NOT NULL,               -- 'Single' | 'Double' | 'Deluxe' | 'Suite'
    price       DECIMAL(10,2) NOT NULL,
    status      VARCHAR(20)   NOT NULL DEFAULT 'AVAILABLE', -- 'AVAILABLE' | 'MAINTENANCE'
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (room_id),
    UNIQUE KEY (hotel_id, room_number),
    FOREIGN KEY (hotel_id) REFERENCES hotels(hotel_id) ON DELETE CASCADE
);

-- ── Hotel Images ─────────────────────────────────────────
CREATE TABLE IF NOT EXISTS hotel_images (
    image_id    INT           NOT NULL AUTO_INCREMENT,
    hotel_id    INT           NOT NULL,
    image_path  VARCHAR(255)  NOT NULL,
    image_type  VARCHAR(20)   NOT NULL,              -- 'MAIN' or 'REFERENCE'
    is_primary  BOOLEAN       DEFAULT FALSE,
    uploaded_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (image_id),
    FOREIGN KEY (hotel_id) REFERENCES hotels(hotel_id) ON DELETE CASCADE,
    INDEX idx_hotel (hotel_id),
    INDEX idx_type (image_type)
);
