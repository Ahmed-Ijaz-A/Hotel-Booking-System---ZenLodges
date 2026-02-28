package com.xcoders.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for AdminDashboard.fxml.
 * Handles sidebar navigation by swapping content in the center area.
 */
public class AdminDashboardController {

    @FXML private StackPane contentArea;

    @FXML
    private void handleAddRoom() {
        loadContent("/fxml/AddRoom.fxml");
    }

    @FXML
    private void handleViewRooms() {
        loadContent("/fxml/ViewRooms.fxml");
    }

    @FXML
    private void handleLogout() {
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
        } catch (IOException e) {
            System.err.println("Error loading content: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
