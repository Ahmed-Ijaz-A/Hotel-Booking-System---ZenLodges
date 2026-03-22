package com.xcoders.controller;

import com.xcoders.SessionManager;
import com.xcoders.model.Room;
import com.xcoders.service.BookingService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;
import com.xcoders.model.User;

/**
 * Controller for room booking interface.
 * Handles date selection and availability checking, with booking confirmation.
 */
public class RoomBookingController {

    private final BookingService bookingService = new BookingService();
    private Room selectedRoom;

    @FXML
    private Label roomDetailsLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private DatePicker checkInDatePicker;

    @FXML
    private DatePicker checkOutDatePicker;

    @FXML
    private Label availabilityLabel;

    @FXML
    private Button bookButton;

    @FXML
    private Button cancelButton;

    /**
     * Initializes the controller. Sets up date picker constraints and event listeners.
     */
    @FXML
    public void initialize() {
        // Set minimum date to today
        LocalDate today = LocalDate.now();
        checkInDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(today));
            }
        });

        checkOutDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(today));
            }
        });

        // Listen for date changes to check availability
        checkInDatePicker.setOnAction(event -> checkAvailability());
        checkOutDatePicker.setOnAction(event -> checkAvailability());

        // Disable booking button initially
        bookButton.setDisable(true);
    }

    /**
     * Sets the room to display and book.
     *
     * @param room the Room object
     */
    public void setRoom(Room room) {
        this.selectedRoom = room;
        updateRoomDisplay();
    }

    /**
     * Updates the room details display.
     */
    private void updateRoomDisplay() {
        if (selectedRoom == null) {
            roomDetailsLabel.setText("No room selected");
            priceLabel.setText("$0.00");
            return;
        }

        roomDetailsLabel.setText(
                String.format("Room %s (%s) at Hotel ID: %d",
                        selectedRoom.getRoomNumber(),
                        selectedRoom.getType(),
                        selectedRoom.getHotelId())
        );
        priceLabel.setText(String.format("$%.2f/night", selectedRoom.getPrice()));
        availabilityLabel.setText("");
    }

    /**
     * Checks room availability for the selected dates and updates the UI.
     */
    @FXML
    private void checkAvailability() {
        LocalDate checkInLocal = checkInDatePicker.getValue();
        LocalDate checkOutLocal = checkOutDatePicker.getValue();

        // Reset UI state
        availabilityLabel.setText("");
        bookButton.setDisable(true);

        // Validate that both dates are selected
        if (checkInLocal == null || checkOutLocal == null) {
            availabilityLabel.setText("Please select both check-in and check-out dates.");
            availabilityLabel.setStyle("-fx-text-fill: #ff9800;");
            return;
        }

        // Validate that check-in is before check-out
        if (!checkInLocal.isBefore(checkOutLocal)) {
            availabilityLabel.setText("Check-out date must be after check-in date.");
            availabilityLabel.setStyle("-fx-text-fill: #f44336;");
            return;
        }

        // Convert to SQL Date
        Date checkIn = Date.valueOf(checkInLocal);
        Date checkOut = Date.valueOf(checkOutLocal);

        // Check availability
        if (bookingService.checkRoomAvailability(selectedRoom.getRoomId(), checkIn, checkOut)) {
            availabilityLabel.setText("✓ Room is available for these dates!");
            availabilityLabel.setStyle("-fx-text-fill: #4caf50;");
            bookButton.setDisable(false);
        } else {
            availabilityLabel.setText("✗ Room is unavailable for these dates.");
            availabilityLabel.setStyle("-fx-text-fill: #f44336;");
            bookButton.setDisable(true);
        }
    }

    /**
     * Confirms and creates the booking.
     */
    @FXML
    private void onBookRoom() {
        LocalDate checkInLocal = checkInDatePicker.getValue();
        LocalDate checkOutLocal = checkOutDatePicker.getValue();

        if (checkInLocal == null || checkOutLocal == null || selectedRoom == null) {
            showAlert("Error", "Please select both dates and ensure a room is selected.", Alert.AlertType.ERROR);
            return;
        }

        // Verify user is a guest (not an admin)
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showAlert("Error", "User session not found.", Alert.AlertType.ERROR);
            return;
        }

        if (!currentUser.isUser()) {
            showAlert("Access Denied", "Only guests can make room bookings. Admin accounts cannot book rooms.", Alert.AlertType.ERROR);
            return;
        }

        int currentUserId = SessionManager.getInstance().getCurrentUserId();
        if (currentUserId <= 0) {
            showAlert("Error", "You must be logged in to make a booking.", Alert.AlertType.ERROR);
            return;
        }

        Date checkIn = Date.valueOf(checkInLocal);
        Date checkOut = Date.valueOf(checkOutLocal);

        int bookingId = bookingService.bookRoom(selectedRoom.getRoomId(), currentUserId, checkIn, checkOut);

        if (bookingId > 0) {
            showAlert("Success",
                    String.format("Booking confirmed! Booking ID: %d\n\nCheck-in: %s\nCheck-out: %s",
                            bookingId, checkInLocal, checkOutLocal),
                    Alert.AlertType.INFORMATION);
            // Clear the form
            checkInDatePicker.setValue(null);
            checkOutDatePicker.setValue(null);
            availabilityLabel.setText("");
            bookButton.setDisable(true);
        } else {
            showAlert("Error", "Failed to create booking. Please try again.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Closes the booking dialog.
     */
    @FXML
    private void onCancel() {
        // Get the stage from any node
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Shows an alert dialog.
     *
     * @param title    the alert title
     * @param message  the message content
     * @param type     the AlertType
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
