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
            if (col >= 3) {
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
            fallbackBg.setPrefSize(330, 220);

            // 1. Setup the Background Image
            ImageView imageView = new ImageView();
            imageView.setFitWidth(330);
            imageView.setFitHeight(220);
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
                        System.out.println("[Card] Image loaded successfully: " + resourceUrl);
                    } else {
                        System.err.println("[Card] Image resource not found: " + resourcePath);
                    }
                } catch (Exception e) {
                    System.err.println("[Card] Failed to load image: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Clip the image for rounded corners
            Rectangle clip = new Rectangle(330, 220);
            clip.setArcWidth(30);
            clip.setArcHeight(30);
            imageView.setClip(clip);

            // 2. Dark Gradient Overlay (Makes text readable on bright photos)
            Region gradient = new Region();
            gradient.setStyle("-fx-background-color: linear-gradient(to top, rgba(0,0,0,0.85) 0%, transparent 60%); -fx-background-radius: 15;");
            gradient.setPrefSize(330, 220);

            // 3. Text Overlay Container
            VBox textContainer = new VBox(2);
            textContainer.setAlignment(Pos.BOTTOM_LEFT);
            textContainer.setStyle("-fx-padding: 20;");

            // Hotel Name
            Label nameLabel = new Label(hotel.getName());
            nameLabel.setStyle("-fx-font-size: 26; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
            nameLabel.setWrapText(true);

            // Location
            Label locationLabel = new Label(hotel.getLocation());
            locationLabel.setStyle("-fx-font-size: 15; -fx-text-fill: rgba(255,255,255,0.9);");
            locationLabel.setWrapText(true);

            // Hotel Type (Optional sub-label to preserve your data)
            Label typeLabel = new Label("Type: " + hotel.getType());
            typeLabel.setStyle("-fx-font-size: 12; -fx-text-fill: rgba(255,255,255,0.6);");

            // Add labels to the VBox
            textContainer.getChildren().addAll(nameLabel, locationLabel, typeLabel);

            // 4. Assemble the StackPane (Bottom to Top)
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