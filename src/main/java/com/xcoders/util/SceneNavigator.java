package com.xcoders.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Utility class for navigating between scenes in the application.
 */
public class SceneNavigator {

    private static Stage primaryStage;
    private static Scene currentScene;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Loads and displays a new FXML scene
     * @param fxmlPath path to the FXML file (e.g., "/fxml/Home.fxml")
     * @param title window title
     * @throws IOException if FXML file cannot be loaded
     */
    public static void loadScene(String fxmlPath, String title) throws IOException {
        Parent root = FXMLLoader.load(SceneNavigator.class.getResource(fxmlPath));
        currentScene = new Scene(root);
        currentScene.getStylesheets().add(
            SceneNavigator.class.getResource("/styles/style.css").toExternalForm()
        );
        
        primaryStage.setTitle(title);
        primaryStage.setScene(currentScene);
        primaryStage.show();
    }

    /**
     * Returns the currently displayed scene
     */
    public static Scene getCurrentScene() {
        return currentScene;
    }

    /**
     * Returns the primary stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
