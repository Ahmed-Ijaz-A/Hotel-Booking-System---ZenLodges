package com.xcoders.controller;

import com.xcoders.SessionManager;
import com.xcoders.model.Hotel;
import com.xcoders.model.Room;
import com.xcoders.model.User;
import com.xcoders.service.HotelService;
import com.xcoders.service.RoomService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class ViewRoomsController {

    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, String> hotelColumn;

    private final RoomService roomService = new RoomService();

    @FXML
    private void initialize() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            roomsTable.setPlaceholder(new Label("You must be logged in to view rooms."));
            return;
        }

        if (currentUser.isPlatformAdmin()) {
            if (hotelColumn != null) hotelColumn.setVisible(true);
            List<Room> rooms = roomService.getAllRooms();
            roomsTable.setItems(FXCollections.observableArrayList(rooms));

        } else if (currentUser.isHotelAdmin()) {
            if (hotelColumn != null) hotelColumn.setVisible(false);
            HotelService hotelService = new HotelService();
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
        }

        if (roomsTable.getItems().isEmpty()) {
            roomsTable.setPlaceholder(new Label("No rooms found for this hotel."));
        }
    }
}