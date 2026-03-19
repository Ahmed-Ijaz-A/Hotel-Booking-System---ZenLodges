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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * Controller for PlatformAdminDashboard.fxml
 * Manages hotel registrations and approvals for platform admins
 */
public class PlatformAdminDashboardController implements Initializable {

    @FXML private TableView<Hotel> hotelsTable;
    @FXML private Label titleLabel;
    @FXML private Button approveBtn;
    @FXML private Button rejectBtn;

    private final HotelService hotelService = new HotelService();
    private User currentUser;
    private String currentView = "PENDING"; // PENDING | APPROVED | REJECTED

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load pending hotels on startup
        loadPendingHotels();
    }

    /**
     * Load and display pending hotels
     */
    @FXML
    private void onPendingClick() {
        currentView = "PENDING";
        loadPendingHotels();
        titleLabel.setText("Pending Hotel Registrations");
        approveBtn.setVisible(true);
        rejectBtn.setVisible(true);
    }

    private void loadPendingHotels() {
        List<Hotel> hotels = hotelService.getPendingHotels();
        hotelsTable.getItems().clear();
        hotelsTable.getItems().addAll(hotels);
    }

    /**
     * Load and display approved hotels
     */
    @FXML
    private void onApprovedClick() {
        currentView = "APPROVED";
        loadApprovedHotels();
        titleLabel.setText("Approved Hotels");
        approveBtn.setVisible(false);
        rejectBtn.setVisible(false);
    }

    private void loadApprovedHotels() {
        List<Hotel> hotels = hotelService.getApprovedHotels();
        hotelsTable.getItems().clear();
        hotelsTable.getItems().addAll(hotels);
    }

    /**
     * Load and display rejected hotels
     */
    @FXML
    private void onRejectedClick() {
        currentView = "REJECTED";
        loadRejectedHotels();
        titleLabel.setText("Rejected Hotels");
        approveBtn.setVisible(false);
        rejectBtn.setVisible(false);
    }

    private void loadRejectedHotels() {
        List<Hotel> hotels = hotelService.getHotelsByStatus("REJECTED");
        hotelsTable.getItems().clear();
        hotelsTable.getItems().addAll(hotels);
    }

    /**
     * Approve selected hotel
     */
    @FXML
    private void onApproveHotel() {
        Hotel selectedHotel = hotelsTable.getSelectionModel().getSelectedItem();
        if (selectedHotel == null) {
            showAlert(AlertType.WARNING, "No Selection", "Please select a hotel to approve.");
            return;
        }

        // Get current user ID from session
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showAlert(AlertType.ERROR, "Error", "Session expired. Please login again.");
            return;
        }

        int platformAdminId = currentUser.getUserId();
        boolean success = hotelService.approveHotel(selectedHotel.getHotelId(), platformAdminId);
        if (success) {
            showAlert(AlertType.INFORMATION, "Success", "Hotel '" + selectedHotel.getName() + "' has been approved!");
            loadPendingHotels(); // Refresh list
        } else {
            showAlert(AlertType.ERROR, "Error", "Failed to approve hotel. Please try again.");
        }
    }

    /**
     * Reject selected hotel
     */
    @FXML
    private void onRejectHotel() {
        Hotel selectedHotel = hotelsTable.getSelectionModel().getSelectedItem();
        if (selectedHotel == null) {
            showAlert(AlertType.WARNING, "No Selection", "Please select a hotel to reject.");
            return;
        }

        // Get current user ID from session
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showAlert(AlertType.ERROR, "Error", "Session expired. Please login again.");
            return;
        }

        int platformAdminId = currentUser.getUserId();
        boolean success = hotelService.rejectHotel(selectedHotel.getHotelId(), platformAdminId);
        if (success) {
            showAlert(AlertType.INFORMATION, "Success", "Hotel '" + selectedHotel.getName() + "' has been rejected.");
            loadPendingHotels(); // Refresh list
        } else {
            showAlert(AlertType.ERROR, "Error", "Failed to reject hotel. Please try again.");
        }
    }

    /**
     * Logout user and return to login page
     */
    @FXML
    private void onLogoutClick() {
        try {
        SessionManager.getInstance().clearSession();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Stage stage = (Stage) hotelsTable.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Error loading Login page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show alert dialog
     */
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
