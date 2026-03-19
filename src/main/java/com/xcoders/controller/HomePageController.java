package com.xcoders.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Controller for Home.fxml
 * Manages the home page display with navigation and hero section.
 */
public class HomePageController implements Initializable {

    // ── Navigation ──
    @FXML private Button searchHotelsBtn;
    @FXML private Button loginBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    // ── Navigation Actions ──

    @FXML
    private void onHomeClick() {
        System.out.println("Home clicked");
    }

    @FXML
    private void onSearchHotelsClick() {
        System.out.println("Search Hotels clicked");
    }

    @FXML
    private void onContactClick() {
        System.out.println("Contact clicked");
    }

    @FXML
    private void onLoginRegisterClick() {
        System.out.println("Login/Register clicked");
        navigateToLogin();
    }

    @FXML
    private void onLoginClick() {
        System.out.println("Login button clicked");
        navigateToLogin();
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Failed to load Login page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
