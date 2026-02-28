# Hotel Booking System

A desktop-based **Hotel Booking System** developed using **JavaFX**, **Java**, and **MySQL**.  
The system manages room availability and customer reservations through a structured, database-driven architecture.

This project is developed using the **Agile (Scrum) methodology**, with work organized into iterative sprints and clearly defined team roles.

---

## Project Overview

The Hotel Booking System allows users to:

- View available rooms  
- Create and manage reservations  
- Store and retrieve customer information  
- Maintain booking records in a MySQL database  

The application follows a layered architecture to ensure modularity, maintainability, and clean separation of concerns.

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Frontend | JavaFX |
| Backend | Java |
| Database | MySQL |
| Connectivity | JDBC |
| Methodology | Agile (Scrum) |

---

## System Architecture

The project follows a **Layered Architecture**:

### 1. Presentation Layer (UI)
- Built using JavaFX
- Handles user interaction
- Contains FXML files and controllers

### 2. Business Logic Layer
- Implements booking rules
- Validates input
- Processes reservation logic

### 3. Data Access Layer
- Uses JDBC for database connectivity
- Contains DAO classes
- Performs CRUD operations on MySQL database

---

## Project Structure
###Hotel-Booking-System/
-ui/ # JavaFX Controllers & FXML files
-service/ # Business logic layer
-dao/ # Database access objects
-model/ # Entity classes
-db/ # SQL scripts
-Readme.md

---
## Development Methodology

This project is developed using **Scrum**, an Agile framework.

### Scrum Implementation:
- Sprint-based development
- Defined team roles:
  - Frontend Developer (JavaFX)
  - Backend Developer (Logic + Integration)
  - Database Developer (MySQL + Schema Design)
- Sprint planning and task breakdown
- Incremental feature delivery



