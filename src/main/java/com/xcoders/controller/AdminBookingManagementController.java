package com.xcoders.controller;

import com.xcoders.SessionManager;
import com.xcoders.model.BookingDetail;
import com.xcoders.model.Hotel;
import com.xcoders.model.User;
import com.xcoders.service.BookingService;
import com.xcoders.service.HotelService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for AdminBookingManagement.fxml
 * Displays booking management for hotel admins.
 * Only shows bookings for the admin's hotel.
 */
public class AdminBookingManagementController implements Initializable {

    @FXML private Label hotelNameLabel;
    @FXML private Label bookingCountLabel;
    @FXML private TableView<BookingDetail> bookingsTable;
    @FXML private TableColumn<BookingDetail, String> hotelColumn;
    @FXML private TableColumn<BookingDetail, String> roomColumn;
    @FXML private TableColumn<BookingDetail, String> typeColumn;
    @FXML private TableColumn<BookingDetail, String> checkInColumn;
    @FXML private TableColumn<BookingDetail, String> checkOutColumn;
    @FXML private TableColumn<BookingDetail, String> statusColumn;
    @FXML private TableColumn<BookingDetail, BookingDetail> actionColumn;

    private final BookingService bookingService = new BookingService();
    private final HotelService hotelService = new HotelService();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Hotel adminHotel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        // Check if user is logged in and is hotel admin
        if (currentUser == null) {
            bookingsTable.setPlaceholder(new Label("You must be logged in to view bookings."));
            return;
        }

        if (!currentUser.isHotelAdmin()) {
            bookingsTable.setPlaceholder(new Label("Only hotel admins can access this page."));
            return;
        }

        // Load hotel admin's hotel
        List<Hotel> adminHotels = hotelService.getHotelsByAdminId(currentUser.getUserId());
        if (adminHotels == null || adminHotels.isEmpty()) {
            bookingsTable.setPlaceholder(new Label("No hotel associated with this account."));
            hotelNameLabel.setText("No Hotel Assigned");
            return;
        }

        // Use approved hotel if available, otherwise first hotel
        adminHotel = adminHotels.stream()
                .filter(Hotel::isApproved)
                .findFirst()
                .orElse(adminHotels.get(0));

        // Display hotel info
        hotelNameLabel.setText("Bookings for: " + adminHotel.getName());

        // Setup table columns
        setupTableColumns();

        // Load bookings for this hotel
        loadHotelBookings(adminHotel.getHotelId());
    }

    /**
     * Configure the table columns with property value factories and formatters
     */
    private void setupTableColumns() {
        hotelColumn.setCellValueFactory(new PropertyValueFactory<>("hotelName"));
        roomColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        checkInColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(() -> 
                cellData.getValue().getCheckIn() != null ? 
                dateFormat.format(cellData.getValue().getCheckIn()) : "-"));
        checkOutColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(() -> 
                cellData.getValue().getCheckOut() != null ? 
                dateFormat.format(cellData.getValue().getCheckOut()) : "-"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Setup action column with cancel button
        setupActionColumn();

        // Apply styling to status column
        statusColumn.setCellFactory(col -> new TableCell<BookingDetail, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("CONFIRMED".equals(status)) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else if ("CANCELLED".equals(status)) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    /**
     * Setup the action column with cancel booking button
     */
    private void setupActionColumn() {
        if (actionColumn == null) {
            return;
        }

        actionColumn.setCellValueFactory(param -> new javafx.beans.property.ReadOnlyObjectWrapper<>(param.getValue()));
        actionColumn.setCellFactory(col -> new TableCell<BookingDetail, BookingDetail>() {
            private final Button cancelBtn = new Button("Cancel");

            {
                cancelBtn.setStyle("-fx-padding: 8 15; -fx-font-size: 11;");
                cancelBtn.setOnAction(event -> {
                    BookingDetail booking = getItem();
                    if (booking != null) {
                        handleCancelBooking(booking);
                    }
                });
            }

            @Override
            protected void updateItem(BookingDetail booking, boolean empty) {
                super.updateItem(booking, empty);
                if (empty || booking == null) {
                    setGraphic(null);
                } else {
                    // Only show cancel button for confirmed bookings
                    boolean canCancel = "CONFIRMED".equals(booking.getStatus());
                    setGraphic(canCancel ? cancelBtn : new Label("-"));
                }
            }
        });
    }

    /**
     * Load all bookings for the hotel
     */
    private void loadHotelBookings(int hotelId) {
        try {
            List<BookingDetail> bookings = bookingService.getHotelBookings(hotelId);

            if (bookings == null || bookings.isEmpty()) {
                bookingsTable.setPlaceholder(new Label("No bookings for this hotel yet."));
                bookingCountLabel.setText("Total Bookings: 0");
                return;
            }

            bookingsTable.setItems(FXCollections.observableArrayList(bookings));
            bookingCountLabel.setText("Total Bookings: " + bookings.size());

        } catch (Exception e) {
            System.err.println("[AdminBookingManagementController] Error loading bookings: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load bookings. Please try again later.");
            bookingsTable.setPlaceholder(new Label("Error loading bookings."));
        }
    }

    /**
     * Handle cancellation of a booking
     */
    private void handleCancelBooking(BookingDetail booking) {
        if (!"CONFIRMED".equals(booking.getStatus())) {
            showAlert("Invalid Action", "Only confirmed bookings can be cancelled.");
            return;
        }

        // Confirm cancellation
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Cancel Booking");
        confirmAlert.setHeaderText("Are you sure?");
        confirmAlert.setContentText(
            String.format("Cancel booking for Room %s from %s to %s?",
                booking.getRoomNumber(),
                dateFormat.format(booking.getCheckIn()),
                dateFormat.format(booking.getCheckOut()))
        );

        if (confirmAlert.showAndWait().orElse(null) == javafx.scene.control.ButtonType.OK) {
            if (bookingService.cancelBooking(booking.getBookingId())) {
                showAlert("Success", "Booking cancelled successfully.");
                // Reload bookings
                if (adminHotel != null) {
                    loadHotelBookings(adminHotel.getHotelId());
                }
            } else {
                showAlert("Error", "Failed to cancel booking. Please try again.");
            }
        }
    }

    /**
     * Display an alert message
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
