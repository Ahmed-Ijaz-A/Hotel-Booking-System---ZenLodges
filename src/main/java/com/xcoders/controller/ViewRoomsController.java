package com.xcoders.controller;

import com.xcoders.model.Room;
import com.xcoders.service.RoomService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import java.util.List;

/**
 * Controller for ViewRooms.fxml.
 */
public class ViewRoomsController {

    @FXML private TableView<Room> roomsTable;

    private final RoomService roomService = new RoomService();

    @FXML
    private void initialize() {
        loadRooms();
    }

    private void loadRooms() {
        List<Room> rooms = roomService.getAllRooms();
        ObservableList<Room> data = FXCollections.observableArrayList(rooms);
        roomsTable.setItems(data);

        if (data.isEmpty()) {
            roomsTable.setPlaceholder(
                    new javafx.scene.control.Label("No rooms found.")
            );
        }
    }
}
