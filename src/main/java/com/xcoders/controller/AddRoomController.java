package com.xcoders.controller;

import com.xcoders.service.RoomService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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

    @FXML
    private void handleAddRoom() {
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
        boolean success = roomService.addRoom(roomNumber, type, price, status);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Room added successfully!");
            clearFields();
            showMessage("Room added successfully!", false);
        } else {
            showMessage("Room number already exists or could not save.", true);
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
