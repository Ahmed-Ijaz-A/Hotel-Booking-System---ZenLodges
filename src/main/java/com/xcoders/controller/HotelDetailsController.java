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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for HotelDetails.fxml
 * Displays detailed information about a specific hotel with all photos
 */
public class HotelDetailsController implements Initializable {

    @FXML private VBox hotelDetailsBox;
    @FXML private VBox noHotelBox;
    @FXML private Button loginBtn;

    private HotelService hotelService;
    private HotelImageService imageService;
    private String searchQuery; // The search term used to find the hotel

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hotelService = new HotelService();
        imageService = new HotelImageService();
    }

    /**
     * Load and display hotel details based on search query
     */
    public void loadHotelDetails(String query) {
        this.searchQuery = query;
        
        if (query == null || query.trim().isEmpty()) {
            showNoHotelFound();
            return;
        }

        try {
            // Search for hotel by name or location
            Hotel foundHotel = findHotel(query.trim().toLowerCase());

            if (foundHotel == null) {
                showNoHotelFound();
                return;
            }

            displayHotelDetails(foundHotel);

        } catch (Exception e) {
            System.err.println("[HotelDetailsController] Error loading hotel details: " + e.getMessage());
            e.printStackTrace();
            showNoHotelFound();
        }
    }

    /**
     * Find hotel by name or location
     */
    private Hotel findHotel(String query) {
        try {
            List<Hotel> approvedHotels = hotelService.getApprovedHotels();
            
            if (approvedHotels == null) {
                return null;
            }

            // First, try exact name match
            for (Hotel hotel : approvedHotels) {
                if (hotel.getName().toLowerCase().equals(query)) {
                    return hotel;
                }
            }

            // Then, try contains match
            for (Hotel hotel : approvedHotels) {
                if (hotel.getName().toLowerCase().contains(query) || 
                    hotel.getLocation().toLowerCase().contains(query)) {
                    return hotel;
                }
            }

            return null;
        } catch (Exception e) {
            System.err.println("[HotelDetailsController] Error searching hotels: " + e.getMessage());
            return null;
        }
    }

    /**
     * Display hotel details with all information and photos
     */
    private void displayHotelDetails(Hotel hotel) {
        hotelDetailsBox.getChildren().clear();
        
        try {
            // Hotel Name
            Label nameLabel = new Label(hotel.getName());
            nameLabel.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: #003cfd;");
            hotelDetailsBox.getChildren().add(nameLabel);

            // Location and Type
            HBox infoBox = new HBox(20);
            Label locationLabel = new Label("📍 " + hotel.getLocation());
            locationLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #7f8c8d;");
            Label typeLabel = new Label("🏨 Type: " + hotel.getType());
            typeLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #7f8c8d;");
            infoBox.getChildren().addAll(locationLabel, typeLabel);
            hotelDetailsBox.getChildren().add(infoBox);

            // Description
            if (hotel.getDescription() != null && !hotel.getDescription().isEmpty()) {
                Label descLabel = new Label("Description");
                descLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #34495e; -fx-padding: 20 0 10 0;");
                hotelDetailsBox.getChildren().add(descLabel);

                Label descContentLabel = new Label(hotel.getDescription());
                descContentLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #555555; -fx-wrap-text: true;");
                descContentLabel.setWrapText(true);
                hotelDetailsBox.getChildren().add(descContentLabel);
            }

            // Main Photo
            HotelImage mainImage = imageService.getMainImage(hotel.getHotelId());
            if (mainImage != null && !mainImage.getImagePath().isEmpty()) {
                Label mainPhotoLabel = new Label("Main Photo");
                mainPhotoLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #34495e; -fx-padding: 20 0 10 0;");
                hotelDetailsBox.getChildren().add(mainPhotoLabel);

                ImageView mainImageView = new ImageView();
                mainImageView.setFitHeight(400);
                mainImageView.setFitWidth(800);
                mainImageView.setPreserveRatio(false);
                mainImageView.setSmooth(true);

                try {
                    String resourcePath = "/" + mainImage.getImagePath();
                    URL resourceUrl = getClass().getResource(resourcePath);
                    if (resourceUrl != null) {
                        Image image = new Image(resourceUrl.toExternalForm());
                        mainImageView.setImage(image);
                    }
                } catch (Exception e) {
                    System.err.println("[HotelDetails] Failed to load main image: " + e.getMessage());
                }

                hotelDetailsBox.getChildren().add(mainImageView);
            }

            // Reference Photos Gallery
            List<HotelImage> referenceImages = imageService.getReferenceImages(hotel.getHotelId());
            if (referenceImages != null && !referenceImages.isEmpty()) {
                Label galleryLabel = new Label("Photo Gallery");
                galleryLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #34495e; -fx-padding: 30 0 15 0;");
                hotelDetailsBox.getChildren().add(galleryLabel);

                // Create grid of reference photos
                HBox photoGrid = new HBox(15);
                photoGrid.setStyle("-fx-padding: 0;");
                
                for (HotelImage refImage : referenceImages) {
                    if (refImage.getImagePath() != null && !refImage.getImagePath().isEmpty()) {
                        try {
                            ImageView imgView = new ImageView();
                            imgView.setFitHeight(300);
                            imgView.setFitWidth(250);
                            imgView.setPreserveRatio(false);
                            imgView.setSmooth(true);
                            imgView.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1;");

                            String resourcePath = "/" + refImage.getImagePath();
                            URL resourceUrl = getClass().getResource(resourcePath);
                            if (resourceUrl != null) {
                                Image image = new Image(resourceUrl.toExternalForm());
                                imgView.setImage(image);
                            }

                            photoGrid.getChildren().add(imgView);
                        } catch (Exception e) {
                            System.err.println("[HotelDetails] Failed to load reference image: " + e.getMessage());
                        }
                    }
                }

                hotelDetailsBox.getChildren().add(photoGrid);
            }

        } catch (Exception e) {
            System.err.println("[HotelDetailsController] Error displaying hotel details: " + e.getMessage());
            e.printStackTrace();
            showNoHotelFound();
        }
    }

    /**
     * Show "no hotel found" message
     */
    private void showNoHotelFound() {
        hotelDetailsBox.setVisible(false);
        hotelDetailsBox.setManaged(false);
        noHotelBox.setVisible(true);
        noHotelBox.setManaged(true);
    }

    /**
     * Handle back button - navigate to home page
     */
    @FXML
    private void onBackClick() {
        try {
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Failed to load Home page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle login button - navigate to login page
     */
    @FXML
    private void onLoginClick() {
        try {
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Failed to load Login page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
