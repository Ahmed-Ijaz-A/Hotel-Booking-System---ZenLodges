package com.xcoders.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.xcoders.SessionManager;
import com.xcoders.model.Hotel;
import com.xcoders.model.User;
import com.xcoders.service.HotelService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for Hotel Registration form.
 * Allows Hotel Admins to submit hotel registration requests.
 */
public class HotelRegistrationController implements Initializable {

    @FXML private TextField hotelNameField;
    @FXML private TextField locationField;
    @FXML private ComboBox<String> hotelTypeCombo;
    @FXML private TextArea descriptionArea;
    @FXML private Label statusLabel;
    @FXML private VBox successBox;

    private HotelService hotelService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hotelService = new HotelService();
        
        // Set default hotel type
        if (hotelTypeCombo.getItems().isEmpty()) {
            hotelTypeCombo.getItems().addAll("Luxury", "Business", "Budget", "Boutique", "Resort");
        }
        hotelTypeCombo.setValue("Luxury");
        
        // Clear error initially
        statusLabel.setText("");
        successBox.setVisible(false);
    }

    /**
     * Handle form submission - register hotel
     */
    @FXML
    private void onSubmitClick() {
        // Validate form
        String validationError = validateForm();
        if (validationError != null) {
            showError(validationError);
            return;
        }

        // Check if user is logged in
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showError("Error: You must be logged in as a Hotel Admin to register a hotel.");
            return;
        }

        try {
            // Create hotel object
            Hotel hotel = new Hotel();
            hotel.setName(hotelNameField.getText().trim());
            hotel.setLocation(locationField.getText().trim());
            hotel.setType(hotelTypeCombo.getValue());
            hotel.setDescription(descriptionArea.getText().trim());
            hotel.setHotelAdminId(currentUser.getUserId());

            // Register hotel
            hotelService.registerHotel(hotel);

            // Show success message
            showSuccess();
            clearForm();

        } catch (Exception e) {
            showError("Error registering hotel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validate form fields
     */
    private String validateForm() {
        String hotelName = hotelNameField.getText().trim();
        String location = locationField.getText().trim();
        String hotelType = hotelTypeCombo.getValue();

        if (hotelName.isEmpty()) {
            return "Hotel Name is required";
        }
        if (hotelName.length() < 3) {
            return "Hotel Name must be at least 3 characters";
        }
        if (hotelName.length() > 100) {
            return "Hotel Name must be less than 100 characters";
        }

        if (location.isEmpty()) {
            return "Location/Address is required";
        }
        if (location.length() < 3) {
            return "Location must be at least 3 characters";
        }

        if (hotelType == null || hotelType.isEmpty()) {
            return "Hotel Type is required";
        }

        return null; // No errors
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        successBox.setVisible(false);
    }

    /**
     * Show success message
     */
    private void showSuccess() {
        statusLabel.setText("");
        successBox.setVisible(true);
    }

    /**
     * Clear form fields
     */
    private void clearForm() {
        hotelNameField.setText("");
        locationField.setText("");
        hotelTypeCombo.setValue("Luxury");
        descriptionArea.setText("");
    }

    /**
     * Handle Back button - return to previous scene
     */
    @FXML
    private void onBackClick() {
        try {
            Stage stage = (Stage) hotelNameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Error navigating back: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle Cancel button - clear form
     */
    @FXML
    private void onCancelClick() {
        clearForm();
        statusLabel.setText("");
        successBox.setVisible(false);
    }

    /**
     * Handle Return to Home button
     */
    @FXML
    private void onReturnHomeClick() {
        try {
            Stage stage = (Stage) hotelNameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Error returning to home: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle Logout button
     */
    @FXML
    private void onLogoutClick() {
        SessionManager.getInstance().clearSession();
        try {
            Stage stage = (Stage) hotelNameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
