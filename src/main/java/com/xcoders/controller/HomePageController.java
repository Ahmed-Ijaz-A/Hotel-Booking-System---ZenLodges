package com.xcoders.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.xcoders.SessionManager;
import com.xcoders.model.Hotel;
import com.xcoders.model.HotelImage;
import com.xcoders.model.User;
import com.xcoders.service.HotelImageService;
import com.xcoders.service.HotelService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
    @FXML private Button heroBrowseRoomsBtn;
    @FXML private Button myBookingsBtn;

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
            StackPane hotelCard = createHotelCard(hotel); // Changed from VBox to StackPane
            hotelGridPane.add(hotelCard, col, row);

            col++;
            if (col >= 4) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * Create a hotel card UI component using the new StackPane edge-to-edge design
     */
    private StackPane createHotelCard(Hotel hotel) {
        StackPane card = new StackPane();
        card.setStyle("-fx-cursor: hand;");
        
        // --- 1. LOCK THE MAIN CARD SIZE ---
        // This stops the entire card from stretching inside the GridPane cell
        card.setMinSize(240, 160);
        card.setMaxSize(240, 160);
        card.setPrefSize(240, 160);

        // Subtle drop shadow for the whole card
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.color(0, 0, 0, 0.15));
        dropShadow.setRadius(15);
        dropShadow.setSpread(0.05);
        dropShadow.setOffsetY(8);
        card.setEffect(dropShadow);

        try {
            // Base fallback background just in case the image fails to load
            Region fallbackBg = new Region();
            fallbackBg.setStyle("-fx-background-color: #d0d0d0; -fx-background-radius: 30;");
            // --- 2. LOCK FALLBACK BACKGROUND SIZE ---
            fallbackBg.setMinSize(240, 160);
            fallbackBg.setMaxSize(240, 160);

            // Setup the Background Image
            ImageView imageView = new ImageView();
            imageView.setFitWidth(240);
            imageView.setFitHeight(160);
            imageView.setPreserveRatio(false);
            imageView.setSmooth(true);

            // Fetch image using your exact original logic
            HotelImage mainImage = imageService.getMainImage(hotel.getHotelId());
            if (mainImage != null && !mainImage.getImagePath().isEmpty()) {
                try {
                    String resourcePath = "/" + mainImage.getImagePath();
                    URL resourceUrl = getClass().getResource(resourcePath);
                    
                    if (resourceUrl != null) {
                        Image image = new Image(resourceUrl.toExternalForm());
                        imageView.setImage(image);
                    }
                } catch (Exception e) {
                    System.err.println("[Card] Failed to load image: " + e.getMessage());
                }
            }

            // Clip the image for rounded corners
            Rectangle clip = new Rectangle(240, 160);
            clip.setArcWidth(30);
            clip.setArcHeight(30);
            imageView.setClip(clip);

            // Dark Gradient Overlay (Makes text readable on bright photos)
            Region gradient = new Region();
            gradient.setStyle("-fx-background-color: linear-gradient(to top, rgba(0,0,0,0.9) 0%, transparent 70%); -fx-background-radius: 15;");
            // --- 3. LOCK THE GRADIENT OVERLAY SIZE ---
            // This is what was causing the "mismatched box" look!
            gradient.setMinSize(240, 160);
            gradient.setMaxSize(240, 160);

            // Text Overlay Container
            VBox textContainer = new VBox(2);
            textContainer.setAlignment(Pos.BOTTOM_LEFT);
            textContainer.setStyle("-fx-padding: 15;");
            // --- 4. LOCK THE TEXT CONTAINER SIZE ---
            textContainer.setMinSize(240, 160);
            textContainer.setMaxSize(240, 160);

            // Hotel Name
            Label nameLabel = new Label(hotel.getName());
            nameLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
            nameLabel.setWrapText(true);

            // Location
            Label locationLabel = new Label(hotel.getLocation());
            locationLabel.setStyle("-fx-font-size: 12; -fx-text-fill: rgba(255,255,255,0.9);");
            locationLabel.setWrapText(true);

            // Hotel Type 
            Label typeLabel = new Label("Type: " + hotel.getType());
            typeLabel.setStyle("-fx-font-size: 11; -fx-text-fill: rgba(255,255,255,0.6);");

            // Add labels to the VBox
            textContainer.getChildren().addAll(nameLabel, locationLabel, typeLabel);

            // Assemble the StackPane (Bottom to Top)
            card.getChildren().addAll(fallbackBg, imageView, gradient, textContainer);

        } catch (Exception e) {
            System.err.println("[Card] Error creating hotel card: " + e.getMessage());
            e.printStackTrace();

            // Show error in card if the layout assembly fails
            Label errorLabel = new Label("Error loading hotel details");
            errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-background-color: white; -fx-padding: 10;");
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