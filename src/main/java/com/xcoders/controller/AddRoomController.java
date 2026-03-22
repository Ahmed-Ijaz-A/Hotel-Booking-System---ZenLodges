package com.xcoders.controller;

import com.xcoders.SessionManager;
import com.xcoders.model.Hotel;
import com.xcoders.model.User;
import com.xcoders.service.HotelService;
import com.xcoders.service.RoomService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * Controller for AddRoom.fxml.
 */
public class AddRoomController {

    @FXML private TextField roomNumberField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Label messageLabel;

    private final RoomService roomService = new RoomService();
    private int hotelId = -1;
    private boolean canManageRooms = false;

    @FXML
    private void initialize() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showMessage("You must be logged in to add rooms.", true);
            return;
        }

        HotelService hotelService = new HotelService();
        List<Hotel> hotels = hotelService.getHotelsByAdminId(currentUser.getUserId());
        if (hotels.isEmpty()) {
            showMessage("No hotel found for your account. Register a hotel first.", true);
            return;
        }

        Hotel selectedHotel = hotels.stream()
                .filter(Hotel::isApproved)
                .findFirst()
                .orElse(hotels.get(0));

        hotelId = selectedHotel.getHotelId();
        canManageRooms = selectedHotel.isApproved();

        if (!canManageRooms) {
            showMessage("Your hotel is not approved yet. You can add rooms after approval.", true);
        }
    }

    @FXML
    private void handleAddRoom() {
        if (!canManageRooms) {
            showMessage("Your hotel is not approved yet. You can add rooms after approval.", true);
            return;
        }

        if (hotelId <= 0) {
            showMessage("No hotel found for your account.", true);
            return;
        }

        String roomNumber = roomNumberField.getText().trim();
        String type       = typeCombo.getValue();
        String priceText  = priceField.getText().trim();
        String status     = statusCombo.getValue();

        // Validate empty fields
        if (roomNumber.isEmpty() || type == null || priceText.isEmpty() || status == null) {
            showMessage("All fields are required.", true);
            return;
        }

        // Validate price is numeric
        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price < 0) {
                showMessage("Price cannot be negative.", true);
                return;
            }
        } catch (NumberFormatException e) {
            showMessage("Price must be a valid number.", true);
            return;
        }

        // Attempt to add room
        boolean success = roomService.addRoom(hotelId, roomNumber, type, price, status);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Room added successfully!");
            clearFields();
            showMessage("Room added successfully!", false);
        } else {
            showMessage("Room number already exists in this hotel or could not save.", true);
        }
    }

    private void showMessage(String text, boolean isError) {
        messageLabel.setText(text);
        messageLabel.setStyle(isError
                ? "-fx-text-fill: #dc3545;"
                : "-fx-text-fill: #28a745;");
    }

    private void clearFields() {
        roomNumberField.clear();
        typeCombo.getSelectionModel().clearSelection();
        typeCombo.setPromptText("Select type");
        priceField.clear();
        statusCombo.getSelectionModel().clearSelection();
        statusCombo.setPromptText("Select status");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}