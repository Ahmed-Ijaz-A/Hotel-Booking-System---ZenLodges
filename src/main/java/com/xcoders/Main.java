package com.xcoders;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Home.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        stage.setTitle("ZenLodges – Hotel Booking System");
        stage.setScene(scene);
        stage.setWidth(900);
        stage.setHeight(700);
        stage.setResizable(true);
        stage.show();
    }

    @Override
    public void stop() {
        DBConnection.closeConnection();
    }

    public static void main(String[] args) {
        launch();
    }
}