package com.xcoders.controller;

import com.xcoders.model.Hotel;
import com.xcoders.model.User;
import com.xcoders.service.HotelService;
import com.xcoders.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for HotelAndAdminRegistration.fxml
 * Allows new hotel admins to register themselves and their hotel in one process
 */
public class HotelAndAdminRegistrationController implements Initializable {

    // Admin Details Fields
    @FXML private TextField adminNameField;
    @FXML private TextField adminEmailField;
    @FXML private TextField adminPhoneField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    // Hotel Details Fields
    @FXML private TextField hotelNameField;
    @FXML private TextField locationField;
    @FXML private ComboBox<String> hotelTypeCombo;
    @FXML private TextArea descriptionArea;

    // Status/Result Fields
    @FXML private Label statusLabel;
    @FXML private VBox successBox;

    private UserService userService;
    private HotelService hotelService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userService = new UserService();
        hotelService = new HotelService();
        
        // Set default hotel type
        if (hotelTypeCombo.getItems().isEmpty()) {
            hotelTypeCombo.getItems().addAll("Luxury", "Business", "Budget", "Boutique", "Resort");
        }
        hotelTypeCombo.setValue("Luxury");
        
        // Clear status initially
        statusLabel.setText("");
        successBox.setVisible(false);
    }

    /**
     * Handle form submission - register both admin and hotel
     */
    @FXML
    private void onSubmitClick() {
        // Validate both admin and hotel forms
        String validationError = validateForm();
        if (validationError != null) {
            showError(validationError);
            return;
        }

        try {
            // Step 1: Create the Hotel Admin User
            User hotelAdmin = new User();
            hotelAdmin.setName(adminNameField.getText().trim());
            hotelAdmin.setEmail(adminEmailField.getText().trim());
            hotelAdmin.setPassword(passwordField.getText());
            hotelAdmin.setRole("HOTEL_ADMIN");
            hotelAdmin.setStatus("ACTIVE");

            // Register the user and get back the created user with ID
            User createdUser = userService.registerUser(hotelAdmin);
            if (createdUser == null || createdUser.getUserId() == 0) {
                showError("Failed to create hotel admin account. Email may already exist.");
                return;
            }

            // Step 2: Create the Hotel with PENDING status
            Hotel hotel = new Hotel();
            hotel.setName(hotelNameField.getText().trim());
            hotel.setLocation(locationField.getText().trim());
            hotel.setType(hotelTypeCombo.getValue());
            hotel.setDescription(descriptionArea.getText().trim());
            hotel.setHotelAdminId(createdUser.getUserId());
            // Status defaults to PENDING

            hotelService.registerHotel(hotel);

            // Show success message
            showSuccess();
            clearForm();

        } catch (Exception e) {
            showError("Registration failed: " + e.getMessage());
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validate both admin and hotel form fields
     */
    private String validateForm() {
        // ─── Admin Validation ───
        String adminName = adminNameField.getText().trim();
        String adminEmail = adminEmailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (adminName.isEmpty()) {
            return "Full Name is required";
        }
        if (adminName.length() < 3) {
            return "Full Name must be at least 3 characters";
        }

        if (adminEmail.isEmpty()) {
            return "Email Address is required";
        }
        if (!adminEmail.contains("@")) {
            return "Please enter a valid email address";
        }

        if (username.isEmpty()) {
            return "Username is required";
        }
        if (username.length() < 4) {
            return "Username must be at least 4 characters";
        }

        if (password.isEmpty()) {
            return "Password is required";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters";
        }

        if (!password.equals(confirmPassword)) {
            return "Passwords do not match";
        }

        // ─── Hotel Validation ───
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
        adminNameField.setText("");
        adminEmailField.setText("");
        adminPhoneField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        hotelNameField.setText("");
        locationField.setText("");
        hotelTypeCombo.setValue("Luxury");
        descriptionArea.setText("");
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
     * Handle Back to Home button
     */
    @FXML
    private void onBackClick() {
        try {
            Stage stage = (Stage) adminNameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Error navigating to home: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle Go to Login button after success
     */
    @FXML
    private void onGoToLoginClick() {
        try {
            Stage stage = (Stage) adminNameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Error navigating to login: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
