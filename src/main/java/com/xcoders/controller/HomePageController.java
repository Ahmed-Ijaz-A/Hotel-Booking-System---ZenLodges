package com.xcoders.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.xcoders.model.Hotel;
import com.xcoders.model.HotelImage;
import com.xcoders.service.HotelImageService;
import com.xcoders.service.HotelService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for Home.fxml
 * Manages the home page display with navigation and hero section.
 */
public class HomePageController implements Initializable {

    // ── Navigation ──
    @FXML private Button searchHotelsBtn;
    @FXML private Button loginBtn;

    // ── Hotel Display ──
    @FXML private GridPane hotelGridPane;
    @FXML private Label noHotelsLabel;

    private HotelService hotelService;
    private HotelImageService imageService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hotelService = new HotelService();
        imageService = new HotelImageService();

        // Load and display approved hotels
        loadApprovedHotels();
    }

    /**
     * Load approved hotels and display as cards
     */
    private void loadApprovedHotels() {
        try {
            List<Hotel> approvedHotels = hotelService.getApprovedHotels();

            if (approvedHotels == null || approvedHotels.isEmpty()) {
                hotelGridPane.setVisible(false);
                noHotelsLabel.setVisible(true);
                return;
            }

            hotelGridPane.setVisible(true);
            noHotelsLabel.setVisible(false);

            // Display hotels as cards (3 columns)
            int row = 0;
            int col = 0;
            for (Hotel hotel : approvedHotels) {
                VBox hotelCard = createHotelCard(hotel);
                hotelGridPane.add(hotelCard, col, row);

                col++;
                if (col >= 3) {
                    col = 0;
                    row++;
                }
            }

        } catch (Exception e) {
            System.err.println("[HomePageController] Error loading hotels: " + e.getMessage());
            e.printStackTrace();
            noHotelsLabel.setText("Error loading hotels. Please try again later.");
            noHotelsLabel.setVisible(true);
        }
    }

    /**
     * Create a hotel card UI component
     */
    private VBox createHotelCard(Hotel hotel) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5; "
                + "-fx-background-color: #ffffff; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");
        card.setPrefHeight(350);
        card.setPrefWidth(280);
        card.setMaxWidth(280);

        try {
            // Get main image for hotel
            HotelImage mainImage = imageService.getMainImage(hotel.getHotelId());

            ImageView imageView = new ImageView();
            imageView.setFitHeight(200);
            imageView.setFitWidth(250);
            imageView.setPreserveRatio(false);
            imageView.setSmooth(true);

            if (mainImage != null && !mainImage.getImagePath().isEmpty()) {
                // Try to load the image from classpath
                try {
                    // Get the resource URL from classpath
                    // imagePath is like "hotel-images/hotel_1_main_abc12345.jpg"
                    String resourcePath = "/" + mainImage.getImagePath();
                    URL resourceUrl = getClass().getResource(resourcePath);
                    
                    if (resourceUrl != null) {
                        Image image = new Image(resourceUrl.toExternalForm());
                        imageView.setImage(image);
                        System.out.println("[Card] Image loaded successfully: " + resourceUrl);
                    } else {
                        System.err.println("[Card] Image resource not found: " + resourcePath);
                        imageView.setStyle("-fx-background-color: #d0d0d0;");
                    }
                } catch (Exception e) {
                    // Use placeholder if image fails to load
                    System.err.println("[Card] Failed to load image: " + e.getMessage());
                    e.printStackTrace();
                    imageView.setStyle("-fx-background-color: #d0d0d0;");
                }
            } else {
                // No main image available
                imageView.setStyle("-fx-background-color: #d0d0d0;");
            }

            card.getChildren().add(imageView);

            // Hotel name
            Label nameLabel = new Label(hotel.getName());
            nameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #003cfd;");
            nameLabel.setWrapText(true);
            card.getChildren().add(nameLabel);

            // Location
            Label locationLabel = new Label(hotel.getLocation());
            locationLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666666;");
            locationLabel.setWrapText(true);
            card.getChildren().add(locationLabel);

            // Hotel type
            Label typeLabel = new Label("Type: " + hotel.getType());
            typeLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #95a5a6;");
            card.getChildren().add(typeLabel);

        } catch (Exception e) {
            System.err.println("[Card] Error creating hotel card: " + e.getMessage());
            e.printStackTrace();

            // Show error in card
            Label errorLabel = new Label("Error loading hotel details");
            errorLabel.setStyle("-fx-text-fill: #e74c3c;");
            card.getChildren().add(errorLabel);
        }

        return card;
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

    @FXML
    private void onRegisterHotelClick() {
        System.out.println("Register Hotel button clicked");
        navigateToHotelAndAdminRegistration();
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

    private void navigateToHotelRegistration() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HotelRegistration.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Failed to load Hotel Registration page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToHotelAndAdminRegistration() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HotelAndAdminRegistration.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Failed to load Hotel and Admin Registration page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
