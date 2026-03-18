package com.xcoders.controller;

import com.xcoders.service.UserService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for Register.fxml.
 */
public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleRegister() {
        String name     = nameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm  = confirmPasswordField.getText();

        // Validate empty fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: #dc3545;");
            messageLabel.setText("All fields are required.");
            return;
        }

        // Validate password match
        if (!password.equals(confirm)) {
            messageLabel.setStyle("-fx-text-fill: #dc3545;");
            messageLabel.setText("Passwords do not match.");
            return;
        }

        // Validate duplicate email with a clear, user-facing message
        if (userService.emailExists(email)) {
            messageLabel.setStyle("-fx-text-fill: #dc3545;");
            messageLabel.setText("Email is already registered. Please use another email.");
            return;
        }

        // Attempt registration
        boolean success;
        try {
            success = userService.register(name, email, password);
        } catch (RuntimeException e) {
            messageLabel.setStyle("-fx-text-fill: #dc3545;");
            messageLabel.setText("Database error. Check db.properties credentials.");
            System.err.println("Registration failed due to DB error: " + e.getMessage());
            return;
        }

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Account created! You can now log in.");
            loadScene("/fxml/Login.fxml");
        } else {
            messageLabel.setStyle("-fx-text-fill: #dc3545;");
            messageLabel.setText("Registration failed. Please try again.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        loadScene("/fxml/Login.fxml");
    }

    private void loadScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Error loading scene: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
