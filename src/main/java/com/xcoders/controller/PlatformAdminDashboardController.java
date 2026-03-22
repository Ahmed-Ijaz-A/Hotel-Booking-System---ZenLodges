package com.xcoders.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.xcoders.SessionManager;
import com.xcoders.model.Hotel;
import com.xcoders.model.Room;
import com.xcoders.model.User;
import com.xcoders.service.HotelService;
import com.xcoders.service.RoomService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Controller for PlatformAdminDashboard.fxml
 * Manages hotel registrations and approvals for platform admins
 */
public class PlatformAdminDashboardController implements Initializable {

    @FXML private TableView<Hotel> hotelsTable;
    @FXML private TableView<Room> roomsTable;
    @FXML private Label titleLabel;
    @FXML private Button approveBtn;
    @FXML private Button rejectBtn;
    @FXML private HBox roomSearchBox;
    @FXML private TextField roomSearchField;

    @FXML private TableColumn<Room, Number> roomIdColumn;
    @FXML private TableColumn<Room, Number> roomHotelIdColumn;
    @FXML private TableColumn<Room, String> roomHotelNameColumn;
    @FXML private TableColumn<Room, String> roomNumberColumn;
    @FXML private TableColumn<Room, String> roomTypeColumn;
    @FXML private TableColumn<Room, Number> roomPriceColumn;
    @FXML private TableColumn<Room, String> roomStatusColumn;

    private final HotelService hotelService = new HotelService();
    private final RoomService roomService = new RoomService();
    private String currentView = "PENDING"; // PENDING | APPROVED | REJECTED | ROOMS
    private List<Room> allRooms = List.of();
    private final Map<Integer, String> hotelNameById = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureRoomTable();
        // Load pending hotels on startup
        loadPendingHotels();
        setHotelsMode(true);
    }

    private void configureRoomTable() {
        if (roomHotelNameColumn != null) {
            roomHotelNameColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(resolveHotelName(cellData.getValue().getHotelId())));
        }
    }

    private String resolveHotelName(int hotelId) {
        return hotelNameById.getOrDefault(hotelId, "Unknown Hotel");
    }

    private void setHotelsMode(boolean showActionButtons) {
        hotelsTable.setVisible(true);
        hotelsTable.setManaged(true);
        roomsTable.setVisible(false);
        roomsTable.setManaged(false);
        roomSearchBox.setVisible(false);
        roomSearchBox.setManaged(false);
        approveBtn.setVisible(showActionButtons);
        approveBtn.setManaged(showActionButtons);
        rejectBtn.setVisible(showActionButtons);
        rejectBtn.setManaged(showActionButtons);
    }

    private void setRoomsMode() {
        hotelsTable.setVisible(false);
        hotelsTable.setManaged(false);
        roomsTable.setVisible(true);
        roomsTable.setManaged(true);
        roomSearchBox.setVisible(true);
        roomSearchBox.setManaged(true);
        approveBtn.setVisible(false);
        approveBtn.setManaged(false);
        rejectBtn.setVisible(false);
        rejectBtn.setManaged(false);
    }

    /**
     * Load and display pending hotels
     */
    @FXML
    private void onPendingClick() {
        currentView = "PENDING";
        loadPendingHotels();
        titleLabel.setText("Pending Hotel Registrations");
        setHotelsMode(true);
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
        setHotelsMode(false);
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
        setHotelsMode(false);
    }

    private void loadRejectedHotels() {
        List<Hotel> hotels = hotelService.getHotelsByStatus("REJECTED");
        hotelsTable.getItems().clear();
        hotelsTable.getItems().addAll(hotels);
    }

    @FXML
    private void onAllRoomsClick() {
        currentView = "ROOMS";
        titleLabel.setText("All Hotel Rooms");
        setRoomsMode();
        loadAllRooms();
    }

    private void loadAllRooms() {
        refreshHotelNameMap();
        allRooms = roomService.getAllRooms();
        applyRoomSearchFilter();
    }

    private void refreshHotelNameMap() {
        hotelNameById.clear();
        for (Hotel hotel : hotelService.getAllHotels()) {
            hotelNameById.put(hotel.getHotelId(), hotel.getName());
        }
    }

    @FXML
    private void onRoomSearch() {
        if (!"ROOMS".equals(currentView)) {
            return;
        }
        applyRoomSearchFilter();
    }

    @FXML
    private void onClearRoomSearch() {
        roomSearchField.clear();
        applyRoomSearchFilter();
    }

    private void applyRoomSearchFilter() {
        String query = roomSearchField.getText() == null ? "" : roomSearchField.getText().trim();
        if (query.isEmpty()) {
            roomsTable.setItems(FXCollections.observableArrayList(allRooms));
            return;
        }

        String lower = query.toLowerCase();
        Integer numericQuery = null;
        try {
            numericQuery = Integer.valueOf(query);
        } catch (NumberFormatException ignored) {
        }

        final Integer numericFilter = numericQuery;
        List<Room> filtered = allRooms.stream()
                .filter(room -> {
                    String hotelName = resolveHotelName(room.getHotelId()).toLowerCase();
                    boolean textMatch = hotelName.contains(lower)
                            || room.getRoomNumber().toLowerCase().contains(lower);
                    boolean numericMatch = numericFilter != null
                            && (room.getRoomId() == numericFilter || room.getHotelId() == numericFilter);
                    return textMatch || numericMatch;
                })
                .collect(Collectors.toCollection(ArrayList::new));

        roomsTable.setItems(FXCollections.observableArrayList(filtered));
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
