package com.xcoders.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.xcoders.model.Hotel;
import com.xcoders.model.User;
import com.xcoders.service.HotelImageService;
import com.xcoders.service.HotelService;
import com.xcoders.service.UserService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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

    // Image Upload Fields
    @FXML private Button chooseMainImageButton;
    @FXML private Label mainImageLabel;
    @FXML private Button chooseReferenceImagesButton;
    @FXML private Label referenceImagesLabel;
    @FXML private VBox selectedFilesContainer;

    // Status/Result Fields
    @FXML private Label statusLabel;
    @FXML private VBox successBox;

    // File storage
    private File selectedMainImageFile;
    private List<File> selectedReferenceImageFiles = new ArrayList<>();
    
    private UserService userService;
    private HotelService hotelService;
    private HotelImageService imageService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userService = new UserService();
        hotelService = new HotelService();
        imageService = new HotelImageService();
        
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

            int createdHotelId = hotelService.registerHotel(hotel);
            if (createdHotelId <= 0) {
                showError("Failed to create hotel record. Please try again.");
                return;
            }

            Hotel createdHotel = hotelService.getHotelById(createdHotelId);
            if (createdHotel == null) {
                showError("Hotel record could not be loaded after creation. Please try again.");
                return;
            }

            // Step 3: Upload hotel images
            if (!imageService.uploadImage(createdHotel.getHotelId(), selectedMainImageFile, "MAIN")) {
                showError("Hotel created but failed to upload main image. Please re-upload from hotel admin panel.");
                return;
            }

            // Upload reference images if provided
            for (File refFile : selectedReferenceImageFiles) {
                if (!imageService.uploadImage(createdHotel.getHotelId(), refFile, "REFERENCE")) {
                    System.err.println("Warning: Failed to upload reference image: " + refFile.getName());
                }
            }

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

        // ─── Image Validation ───
        if (selectedMainImageFile == null || !selectedMainImageFile.exists()) {
            return "Main hotel photo is required";
        }
        if (!HotelImageService.isValidImageFile(selectedMainImageFile)) {
            return "Main photo must be a valid image file (JPG, PNG, GIF, BMP)";
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

    /**
     * Handle Choose Main Image button
     */
    @FXML
    private void onChooseMainImageClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Main Hotel Photo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        File selectedFile = fileChooser.showOpenDialog(chooseMainImageButton.getScene().getWindow());
        if (selectedFile != null) {
            if (HotelImageService.isValidImageFile(selectedFile)) {
                selectedMainImageFile = selectedFile;
                mainImageLabel.setText(selectedFile.getName());
                mainImageLabel.setStyle("-fx-text-fill: #27ae60;");
            } else {
                showError("Invalid image format. Please select JPG, PNG, GIF, or BMP.");
                mainImageLabel.setText("No valid file selected");
            }
        }
    }

    /**
     * Handle Choose Reference Images button
     */
    @FXML
    private void onChooseReferenceImagesClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Reference Photos (up to 5)");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(chooseReferenceImagesButton.getScene().getWindow());
        if (selectedFiles != null) {
            // Limit to 5 files
            if (selectedFiles.size() > 5) {
                showError("Please select no more than 5 reference photos. Selected " + selectedFiles.size());
                return;
            }
            
            // Validate all files
            List<File> validFiles = new ArrayList<>();
            for (File file : selectedFiles) {
                if (HotelImageService.isValidImageFile(file)) {
                    validFiles.add(file);
                } else {
                    System.err.println("Skipping invalid image: " + file.getName());
                }
            }
            
            selectedReferenceImageFiles = validFiles;
            updateReferenceImagesDisplay();
        }
    }

    /**
     * Update reference images display
     */
    private void updateReferenceImagesDisplay() {
        referenceImagesLabel.setText(selectedReferenceImageFiles.size() + "/5 photos selected");
        if (selectedReferenceImageFiles.size() > 0) {
            referenceImagesLabel.setStyle("-fx-text-fill: #27ae60;");
        }
        
        // Display selected filenames
        selectedFilesContainer.getChildren().clear();
        for (File file : selectedReferenceImageFiles) {
            Label fileLabel = new Label("• " + file.getName());
            fileLabel.setStyle("-fx-text-fill: #27ae60;");
            selectedFilesContainer.getChildren().add(fileLabel);
        }
    }
}
