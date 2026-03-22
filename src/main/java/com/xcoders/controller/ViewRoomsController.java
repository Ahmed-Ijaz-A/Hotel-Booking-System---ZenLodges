package com.xcoders.controller;

import com.xcoders.SessionManager;
import com.xcoders.model.Hotel;
import com.xcoders.model.Room;
import com.xcoders.model.User;
import com.xcoders.service.HotelService;
import com.xcoders.service.RoomService;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.List;

public class ViewRoomsController {

    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, String> hotelColumn;
    @FXML private TableColumn<Room, Room> actionColumn;

    private final RoomService roomService = new RoomService();
    private final HotelService hotelService = new HotelService();

    @FXML
    private void initialize() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            roomsTable.setPlaceholder(new Label("You must be logged in to view rooms."));
            return;
        }

        if (currentUser.isPlatformAdmin()) {
            if (hotelColumn != null) {
                hotelColumn.setVisible(true);
            }
            List<Room> rooms = roomService.getAllRooms();
            roomsTable.setItems(FXCollections.observableArrayList(rooms));

        } else if (currentUser.isHotelAdmin()) {
            if (hotelColumn != null) {
                hotelColumn.setVisible(false);
            }
            List<Hotel> hotels = hotelService.getHotelsByAdminId(currentUser.getUserId());
            if (!hotels.isEmpty()) {
                Hotel selectedHotel = hotels.stream()
                        .filter(Hotel::isApproved)
                        .findFirst()
                        .orElse(hotels.get(0));
                int hotelId = selectedHotel.getHotelId();
                List<Room> rooms = roomService.getRoomsByHotelId(hotelId);
                roomsTable.setItems(FXCollections.observableArrayList(rooms));
            } else {
                roomsTable.setPlaceholder(new Label("No hotel is linked to this account yet."));
                return;
            }

        } else if (currentUser.isUser()) {
            if (hotelColumn != null) {
                hotelColumn.setVisible(true);
            }
            List<Room> availableRooms = roomService.getAvailableRoomsByApprovedHotels();
            roomsTable.setItems(FXCollections.observableArrayList(availableRooms));
        }

        if (roomsTable.getItems().isEmpty()) {
            roomsTable.setPlaceholder(new Label("No rooms found for this hotel."));
        }

        setupActionColumn(currentUser);
    }

    private void setupActionColumn(User currentUser) {
        if (actionColumn == null) {
            return;
        }

        actionColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button bookBtn = new Button("Book");

            {
                bookBtn.setStyle("-fx-padding: 8 15; -fx-font-size: 11;");
                bookBtn.setOnAction(event -> {
                    Room selectedRoom = getItem();
                    if (selectedRoom != null) {
                        handleBookRoom(currentUser, selectedRoom);
                    }
                });
            }

            @Override
            protected void updateItem(Room item, boolean empty) {
                super.updateItem(item, empty);
                boolean showButton = !empty && item != null && currentUser.isUser();
                setGraphic(showButton ? bookBtn : null);
            }
        });
    }

    private void handleBookRoom(User currentUser, Room room) {
        if (!currentUser.isUser()) {
            showAlert("Access Denied", "Only guests can book rooms.");
            return;
        }

        Hotel hotel = hotelService.getHotelById(room.getHotelId());
        if (hotel == null || !hotel.isApproved()) {
            showAlert("Hotel Not Approved", "This room's hotel is not approved for bookings.");
            return;
        }

        if (!"AVAILABLE".equals(room.getStatus())) {
            showAlert("Room Unavailable", "This room is currently unavailable.");
            return;
        }

        launchBookingDialog(room);
    }

    private void launchBookingDialog(Room room) {
        try (InputStream bookingDialogStream = getClass().getResourceAsStream("/fxml/RoomBooking.fxml")) {
            if (bookingDialogStream == null) {
                throw new IllegalStateException("RoomBooking.fxml not found on classpath");
            }

            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(bookingDialogStream);

            RoomBookingController controller = loader.getController();
            controller.setRoom(room);

            Stage stage = new Stage();
            stage.setTitle("Book Room " + room.getRoomNumber());
            stage.setScene(new Scene(root, 650, 550));
            stage.show();

        } catch (Exception e) {
            System.err.println("[ViewRoomsController] Error loading booking: " + e.getMessage());
            e.printStackTrace();
            Throwable rootCause = e;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }
            String details = rootCause.getMessage() != null
                    ? rootCause.getClass().getSimpleName() + ": " + rootCause.getMessage()
                    : e.toString();
            showAlert("Error", "Could not open booking dialog: " + details);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}