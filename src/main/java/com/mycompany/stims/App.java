package com.mycompany.stims;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The `App` class is the main entry point for the STIMS (Student Information
 * Management System) JavaFX application. It initializes the application and
 * sets up the primary stage (window) with the login screen.
 */
public class App extends Application {

    /**
     * The `start` method is the main entry point for JavaFX applications. It
     * sets up the primary stage (window) and loads the initial FXML view (login
     * screen).
     *
     * @param stage The primary stage (window) for the application.
     * @throws IOException If an error occurs while loading the FXML file.
     */
    @Override
    public void start(Stage stage) throws IOException {
        setRoot(stage, "fxml/Login"); // Start with the login screen
        stage.setTitle("STIMS");
        stage.show();
    }

    /**
     * Sets the root node of the scene for the given stage by loading the
     * specified FXML file. This method is used to switch between different
     * views (screens) in the application.
     *
     * @param stage The stage (window) to set the scene for.
     * @param fxmlPath The path to the FXML file (without the `.fxml`
     * extension).
     * @throws IOException If an error occurs while loading the FXML file.
     */
    public static void setRoot(Stage stage, String fxmlPath) throws IOException {
        Parent root = loadFXML(fxmlPath);
        Scene scene = new Scene(root, 1280, 720);
        stage.setScene(scene);
    }

    /**
     * Loads an FXML file and returns the root node of the FXML hierarchy.
     *
     * @param fxmlPath The path to the FXML file (without the `.fxml`
     * extension).
     * @return The root node of the FXML hierarchy.
     * @throws IOException If an error occurs while loading the FXML file.
     */
    private static Parent loadFXML(String fxmlPath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/mycompany/stims/" + fxmlPath + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * The `main` method is the entry point for the application. It launches the
     * JavaFX application by calling the `launch` method.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        launch();
    }
}
