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
    role       VARCHAR(20)  NOT NULL DEFAULT 'USER',   -- 'ADMIN' | 'USER'
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE', -- 'ACTIVE' | 'BLOCKED'
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id)
);

-- Default admin account (password: admin123 – change after first login)
INSERT IGNORE INTO users (name, email, password, role, status)
VALUES ('Admin', 'admin@zenlodges.com', 'admin123', 'ADMIN', 'ACTIVE');

-- ── Rooms ───────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS rooms (
    room_id     INT           NOT NULL AUTO_INCREMENT,
    room_number VARCHAR(10)   NOT NULL UNIQUE,
    type        VARCHAR(50)   NOT NULL,               -- 'Single' | 'Double' | 'Deluxe' | 'Suite'
    price       DECIMAL(10,2) NOT NULL,
    status      VARCHAR(20)   NOT NULL DEFAULT 'AVAILABLE', -- 'AVAILABLE' | 'MAINTENANCE'
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (room_id)
);
