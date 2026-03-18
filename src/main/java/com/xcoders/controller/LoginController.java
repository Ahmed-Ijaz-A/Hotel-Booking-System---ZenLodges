package com.xcoders.controller;

import java.io.IOException;

import com.xcoders.model.User;
import com.xcoders.service.UserService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Controller for Login.fxml.
 */
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private ImageView bgImageView;

    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        // Bind background image size to parent so it always fills the window
        bgImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                StackPane parent = (StackPane) bgImageView.getParent();
                bgImageView.fitWidthProperty().bind(parent.widthProperty());
                bgImageView.fitHeightProperty().bind(parent.heightProperty());
            }
        });
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("*Please enter both email and password.");
            return;
        }

        User user;
        try {
            user = userService.login(email, password);
        } catch (RuntimeException e) {
            errorLabel.setText("*Database error. Check db.properties credentials.");
            System.err.println("Login failed due to DB error: " + e.getMessage());
            return;
        }

        if (user == null) {
            errorLabel.setText("*Invalid email or password.");
            return;
        }

        errorLabel.setText("");

        // Both ADMIN and GUEST go to AdminDashboard for now
        // (GuestDashboard.fxml is not yet implemented)
        loadScene("/fxml/AdminDashboard.fxml");
    }

    @FXML
    private void handleCreateAccount() {
        loadScene("/fxml/Register.fxml");
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
}
