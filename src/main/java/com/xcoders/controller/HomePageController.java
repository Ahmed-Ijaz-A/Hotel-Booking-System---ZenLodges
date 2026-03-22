package com.xcoders.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.xcoders.SessionManager;
import com.xcoders.model.User;
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
import javafx.scene.control.ComboBox;
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

    // ── Header/Search ──
    @FXML private ComboBox<String> searchComboBox;
    @FXML private Button searchBtn;
    @FXML private Button loginBtn;
    @FXML private Button browseRoomsBtn;
    @FXML private Button myBookingsBtn;
    @FXML private Button heroBrowseRoomsBtn;

    // ── Hotel Display ──
    @FXML private GridPane hotelGridPane;
    @FXML private Label noHotelsLabel;

    private HotelService hotelService;
    private HotelImageService imageService;
    private List<Hotel> allHotels; // Store all approved hotels for filtering

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hotelService = new HotelService();
        imageService = new HotelImageService();

        // Load and display approved hotels
        loadApprovedHotels();
        
        // Setup autocomplete listener
        setupSearchAutocomplete();

        // Show room browsing path to logged-in customers
        updateHeaderActions();
    }

    private void updateHeaderActions() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        boolean isLoggedInUser = currentUser != null && currentUser.isUser();

        loginBtn.setVisible(!isLoggedInUser);
        loginBtn.setManaged(!isLoggedInUser);

        browseRoomsBtn.setVisible(isLoggedInUser);
        browseRoomsBtn.setManaged(isLoggedInUser);

        myBookingsBtn.setVisible(isLoggedInUser);
        myBookingsBtn.setManaged(isLoggedInUser);
    }

    /**
     * Setup autocomplete for the search box
     */
    private void setupSearchAutocomplete() {
        searchComboBox.setEditable(true);
        searchComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            updateSearchSuggestions(newVal);
        });
    }

    /**
     * Update search suggestions based on user input
     */
    private void updateSearchSuggestions(String query) {
        if (query == null || query.trim().isEmpty()) {
            searchComboBox.getItems().clear();
            return;
        }

        String lowerQuery = query.toLowerCase();
        List<String> suggestions = new java.util.ArrayList<>();
        
        // Filter and collect suggestions
        for (Hotel hotel : allHotels) {
            String hotelName = hotel.getName();
            String hotelLocation = hotel.getLocation();
            
            if (hotelName.toLowerCase().contains(lowerQuery)) {
                suggestions.add(hotelName + " - " + hotelLocation);
            } else if (hotelLocation.toLowerCase().contains(lowerQuery)) {
                suggestions.add(hotelName + " - " + hotelLocation);
            }
        }

        // Update ComboBox items
        searchComboBox.getItems().clear();
        searchComboBox.getItems().addAll(suggestions);
        
        // Show dropdown if there are suggestions
        if (!suggestions.isEmpty()) {
            searchComboBox.show();
        } else {
            searchComboBox.hide();
        }
    }

    /**
     * Load approved hotels and display as cards
     */
    private void loadApprovedHotels() {
        try {
            allHotels = hotelService.getApprovedHotels();

            if (allHotels == null || allHotels.isEmpty()) {
                hotelGridPane.setVisible(false);
                noHotelsLabel.setVisible(true);
                return;
            }

            hotelGridPane.setVisible(true);
            noHotelsLabel.setVisible(false);

            displayHotels(allHotels);

        } catch (Exception e) {
            System.err.println("[HomePageController] Error loading hotels: " + e.getMessage());
            e.printStackTrace();
            noHotelsLabel.setText("Error loading hotels. Please try again later.");
            noHotelsLabel.setVisible(true);
        }
    }

    /**
     * Display hotels in the grid
     */
    private void displayHotels(List<Hotel> hotels) {
        hotelGridPane.getChildren().clear();

        if (hotels == null || hotels.isEmpty()) {
            hotelGridPane.setVisible(false);
            noHotelsLabel.setVisible(true);
            noHotelsLabel.setText("No hotels found.");
            return;
        }

        hotelGridPane.setVisible(true);
        noHotelsLabel.setVisible(false);

        // Display hotels as cards (3 columns)
        int row = 0;
        int col = 0;
        for (Hotel hotel : hotels) {
            VBox hotelCard = createHotelCard(hotel);
            hotelGridPane.add(hotelCard, col, row);

            col++;
            if (col >= 3) {
                col = 0;
                row++;
            }
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

    // ── Search and Navigation ──

    @FXML
    private void onSearchClick() {
        String searchQuery = searchComboBox.getEditor().getText().trim();
        
        if (searchQuery.isEmpty()) {
            return;
        }

        // Extract hotel name from formatted suggestion (e.g., "Hotel Name - Location" -> "Hotel Name")
        if (searchQuery.contains(" - ")) {
            searchQuery = searchQuery.split(" - ")[0].trim();
        }

        try {
            // Navigate to hotel details page
            Stage stage = (Stage) searchBtn.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HotelDetails.fxml"));
            Parent root = loader.load();
            
            // Get the controller and pass the search query
            HotelDetailsController controller = loader.getController();
            controller.loadHotelDetails(searchQuery);
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Failed to load Hotel Details page: " + e.getMessage());
            e.printStackTrace();
        }
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

    @FXML
    private void onBrowseRoomsClick() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.isUser()) {
            SessionManager.getInstance().setPendingPostLoginPath("/fxml/ViewRooms.fxml");
            navigateToLogin();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewRooms.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

            Stage stage = (Stage) browseRoomsBtn.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Failed to load View Rooms page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onMyBookingsClick() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.isUser()) {
            SessionManager.getInstance().setPendingPostLoginPath("/fxml/BookingDashboard.fxml");
            navigateToLogin();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BookingDashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

            Stage stage = (Stage) myBookingsBtn.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Failed to load Booking Dashboard page: " + e.getMessage());
            e.printStackTrace();
        }
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
