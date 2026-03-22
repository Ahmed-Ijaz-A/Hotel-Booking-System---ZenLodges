package com.xcoders.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.xcoders.SessionManager;
import com.xcoders.model.Hotel;
import com.xcoders.model.User;
import com.xcoders.service.HotelService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Controller for AdminDashboard.fxml.
 * Handles sidebar navigation with hotel approval status checks.
 */
public class AdminDashboardController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private Label hotelStatusLabel;

    private HotelService hotelService;
    private User currentUser;
    private Hotel userHotel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hotelService = new HotelService();
        currentUser = SessionManager.getInstance().getCurrentUser();
        
        // Load user's hotel
        if (currentUser != null) {
            List<Hotel> hotels = hotelService.getHotelsByAdminId(currentUser.getUserId());
            if (!hotels.isEmpty()) {
                userHotel = hotels.stream()
                        .filter(Hotel::isApproved)
                        .findFirst()
                        .orElse(hotels.get(0));
                updateHotelStatusDisplay();
            } else if (hotelStatusLabel != null) {
                hotelStatusLabel.setText("No hotel linked to this account");
                hotelStatusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }
        }
    }

    /**
     * Update the hotel status label
     */
    private void updateHotelStatusDisplay() {
        if (userHotel != null && hotelStatusLabel != null) {
            String statusText = "Hotel: " + userHotel.getName() + " (" + userHotel.getStatus() + ")";
            hotelStatusLabel.setText(statusText);
            
            // Color code the status
            if (userHotel.isPending()) {
                hotelStatusLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
            } else if (userHotel.isApproved()) {
                hotelStatusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            } else if (userHotel.isRejected()) {
                hotelStatusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }
        }
    }

    @FXML
    private void handleAddRoom() {
        // Check if hotel is approved
        if (userHotel == null) {
            showAlert(Alert.AlertType.WARNING, "No Hotel", "You don't have a registered hotel yet. Please register a hotel first.");
            return;
        }
        
        if (userHotel.isPending()) {
            showAlert(Alert.AlertType.WARNING, "Hotel Not Approved", 
                "Your hotel is pending approval from the platform administrator. You can add rooms after approval.");
            return;
        }
        
        if (userHotel.isRejected()) {
            showAlert(Alert.AlertType.ERROR, "Hotel Rejected", 
                "Your hotel registration was rejected. Please register a new hotel.");
            return;
        }
        
        loadContent("/fxml/AddRoom.fxml");
    }

    @FXML
    private void handleViewRooms() {
        // Check if hotel is approved
        if (userHotel == null) {
            showAlert(Alert.AlertType.WARNING, "No Hotel", "You don't have a registered hotel yet. Please register a hotel first.");
            return;
        }
        
        if (userHotel.isPending()) {
            showAlert(Alert.AlertType.WARNING, "Hotel Not Approved", 
                "Your hotel is pending approval from the platform administrator. You can view rooms after approval.");
            return;
        }
        
        if (userHotel.isRejected()) {
            showAlert(Alert.AlertType.ERROR, "Hotel Rejected", 
                "Your hotel registration was rejected. Please register a new hotel.");
            return;
        }
        
        loadContent("/fxml/ViewRooms.fxml");
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().clearSession();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Error loading Login scene: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads an FXML view into the center content area.
     */
    private void loadContent(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(node);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Load Error", "Could not open page: " + fxmlPath + "\n" + e.getMessage());
            System.err.println("Error loading content: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show alert dialog
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
