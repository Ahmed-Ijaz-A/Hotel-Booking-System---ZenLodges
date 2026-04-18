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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * Controller for HotelDetails.fxml
 * Displays detailed information about a specific hotel with all photos
 */
public class HotelDetailsController implements Initializable {

    // ── NEW FXML Injections ──
    @FXML private Label hotelNameLabel;
    @FXML private Label hotelLocationLabel;
    @FXML private Label hotelTypeLabel;
    @FXML private Label hotelDescriptionLabel;
    @FXML private ImageView mainImageView;
    @FXML private HBox referencePhotosBox;

    // ── Existing FXML Injections ──
    @FXML private VBox hotelDetailsBox; // Kept to prevent FXML load errors (legacy box)
    @FXML private VBox noHotelBox;
    @FXML private Button loginBtn;

    private HotelService hotelService;
    private HotelImageService imageService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hotelService = new HotelService();
        imageService = new HotelImageService();
    }

    /**
     * Load and display hotel details based on search query
     */
    public void loadHotelDetails(String query) {
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
     * Display hotel details with all information and photos into the specific UI elements
     */
    private void displayHotelDetails(Hotel hotel) {
        // Hide the "No Hotel" message
        noHotelBox.setVisible(false);
        noHotelBox.setManaged(false);

        // Ensure the main info section is visible
        hotelNameLabel.getParent().setVisible(true);
        hotelNameLabel.getParent().setManaged(true);
        
        try {
            // 1. Set Hotel Text Details
            hotelNameLabel.setText(hotel.getName());
            hotelLocationLabel.setText("📍 " + hotel.getLocation());
            hotelTypeLabel.setText("Type: " + hotel.getType());
            
            if (hotel.getDescription() != null && !hotel.getDescription().isEmpty()) {
                hotelDescriptionLabel.setText(hotel.getDescription());
                hotelDescriptionLabel.setVisible(true);
                hotelDescriptionLabel.setManaged(true);
            } else {
                hotelDescriptionLabel.setVisible(false);
                hotelDescriptionLabel.setManaged(false);
            }

            // 2. Set Main Photo
            HotelImage mainImage = imageService.getMainImage(hotel.getHotelId());
            if (mainImage != null && !mainImage.getImagePath().isEmpty()) {
                try {
                    String resourcePath = "/" + mainImage.getImagePath();
                    URL resourceUrl = getClass().getResource(resourcePath);
                    if (resourceUrl != null) {
                        mainImageView.setImage(new Image(resourceUrl.toExternalForm()));
                        // Ensure the card wrapper is visible
                        mainImageView.getParent().setVisible(true);
                        mainImageView.getParent().setManaged(true);
                    }
                } catch (Exception e) {
                    System.err.println("[HotelDetails] Failed to load main image: " + e.getMessage());
                    mainImageView.getParent().setVisible(false);
                    mainImageView.getParent().setManaged(false);
                }
            } else {
                // Hide the main image card entirely if there is no image
                mainImageView.getParent().setVisible(false);
                mainImageView.getParent().setManaged(false);
            }

            // 3. Set Reference Photos Gallery
            referencePhotosBox.getChildren().clear();
            List<HotelImage> referenceImages = imageService.getReferenceImages(hotel.getHotelId());
            
            if (referenceImages != null && !referenceImages.isEmpty()) {
                // Show the gallery card
                referencePhotosBox.getParent().setVisible(true);
                referencePhotosBox.getParent().setManaged(true);
                
                for (HotelImage refImage : referenceImages) {
                    if (refImage.getImagePath() != null && !refImage.getImagePath().isEmpty()) {
                        try {
                            ImageView imgView = new ImageView();
                            imgView.setFitHeight(180);
                            imgView.setFitWidth(250);
                            imgView.setPreserveRatio(false);
                            imgView.setSmooth(true);

                            // Apply beautiful rounded corners to each gallery image!
                            Rectangle clip = new Rectangle(250, 180);
                            clip.setArcWidth(20);
                            clip.setArcHeight(20);
                            imgView.setClip(clip);

                            String resourcePath = "/" + refImage.getImagePath();
                            URL resourceUrl = getClass().getResource(resourcePath);
                            if (resourceUrl != null) {
                                Image image = new Image(resourceUrl.toExternalForm());
                                imgView.setImage(image);
                            }

                            referencePhotosBox.getChildren().add(imgView);
                        } catch (Exception e) {
                            System.err.println("[HotelDetails] Failed to load reference image: " + e.getMessage());
                        }
                    }
                }
            } else {
                // Hide the gallery card entirely if there are no reference photos
                referencePhotosBox.getParent().setVisible(false);
                referencePhotosBox.getParent().setManaged(false);
            }

        } catch (Exception e) {
            System.err.println("[HotelDetailsController] Error displaying hotel details: " + e.getMessage());
            e.printStackTrace();
            showNoHotelFound();
        }
    }

    /**
     * Show "no hotel found" message and hide the details cards
     */
    private void showNoHotelFound() {
        // Hide the detail wrappers
        hotelNameLabel.getParent().setVisible(false);
        hotelNameLabel.getParent().setManaged(false);
        mainImageView.getParent().setVisible(false);
        mainImageView.getParent().setManaged(false);
        referencePhotosBox.getParent().setVisible(false);
        referencePhotosBox.getParent().setManaged(false);
        
        // Show the error message
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