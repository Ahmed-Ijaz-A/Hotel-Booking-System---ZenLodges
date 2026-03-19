package com.xcoders.controller;

import java.io.IOException;

import com.xcoders.SessionManager;
import com.xcoders.model.Role;
import com.xcoders.model.User;
import com.xcoders.service.UserService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Controller for Login.fxml
 * Handles user authentication with role-based routing
 */
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Label errorLabel;
    @FXML private ImageView bgImageView;

    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        // Initialize role combo box
        roleCombo.getItems().addAll(
            Role.PLATFORM_ADMIN.getDisplayName(),
            Role.HOTEL_ADMIN.getDisplayName(),
            Role.USER.getDisplayName()
        );
        roleCombo.getSelectionModel().selectFirst(); // Default to Platform Admin

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
        String selectedRoleDisplay = roleCombo.getValue();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("*Please enter both email and password.");
            return;
        }

        if (selectedRoleDisplay == null || selectedRoleDisplay.isEmpty()) {
            errorLabel.setText("*Please select a role.");
            return;
        }

        // Convert display name to Role enum
        Role selectedRole = Role.fromString(selectedRoleDisplay.replace(" ", "_").toUpperCase());

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

        // Validate that user's actual role matches selected role
        Role userActualRole = Role.fromString(user.getRole());
        if (userActualRole != selectedRole) {
            errorLabel.setText("*Your account is not a " + selectedRoleDisplay + ".");
            return;
        }

        errorLabel.setText("");

        // Store user in session
        SessionManager.getInstance().setCurrentUser(user);

        // Route to appropriate dashboard based on role
        routeToDashboard(userActualRole, user);
    }

    /**
     * Route user to appropriate dashboard based on their role
     */
    private void routeToDashboard(Role role, User user) {
        String fxmlPath;

        switch (role) {
            case PLATFORM_ADMIN:
                fxmlPath = "/fxml/PlatformAdminDashboard.fxml";
                break;
            case HOTEL_ADMIN:
                fxmlPath = "/fxml/AdminDashboard.fxml";
                break;
            case USER:
                fxmlPath = "/fxml/Home.fxml"; // Regular users go to home page
                break;
            default:
                fxmlPath = "/fxml/Home.fxml";
        }

        loadScene(fxmlPath);
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
