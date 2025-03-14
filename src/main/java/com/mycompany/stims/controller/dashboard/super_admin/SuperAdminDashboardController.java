package com.mycompany.stims.controller.dashboard.super_admin;

import com.mycompany.stims.utils.Session;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for the Super Admin Dashboard. This class handles navigation
 * between different views in the super admin dashboard, such as Home, Admin
 * Management, and Profile, and provides functionality to sign out.
 */
public class SuperAdminDashboardController implements Initializable {

    /**
     * VBox container for the content area.
     */
    @FXML
    private VBox contentArea;

    /**
     * StackPane used to dynamically load and display FXML content.
     */
    @FXML
    private StackPane dynamicContentContainer;

    /**
     * Initializes the controller class. Loads the Home view by default when the
     * dashboard is initialized.
     *
     * @param url the location used to resolve relative paths for the root
     * object, or null if the location is not known
     * @param rb the resources used to localize the root object, or null if the
     * root object was not localized
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Load the Home view by default on initialization
        showHome();
    }

    /**
     * Loads and displays the Home view in the dynamic content container.
     */
    @FXML
    public void showHome() {
        loadUI("/com/mycompany/stims/fxml/dashboard/super_admin/Home.fxml");
    }

    /**
     * Loads and displays the Admin Management view in the dynamic content
     * container.
     */
    @FXML
    public void showAdminManagement() {
        loadUI("/com/mycompany/stims/fxml/dashboard/super_admin/Admin_Management.fxml");
    }

    /**
     * Loads and displays the Profile view in the dynamic content container.
     */
    @FXML
    public void showProfile() {
        loadUI("/com/mycompany/stims/fxml/dashboard/super_admin/Profile.fxml");
    }

    /**
     * Loads the specified FXML file into the dynamic content container.
     *
     * @param fxmlPath the path to the FXML file to load
     */
    private void loadUI(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newContent = loader.load();

            // Clear the previous content
            dynamicContentContainer.getChildren().clear();

            // Add the new content
            dynamicContentContainer.getChildren().add(newContent);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle loading errors
        }
    }

    /**
     * Handles the action for signing out of the super admin dashboard. Clears
     * the session data and navigates back to the login screen.
     *
     * @param event the action event triggered by the sign-out button
     * @throws IOException if the login FXML file cannot be loaded
     */
    @FXML
    private void handleSignOut(ActionEvent event) throws IOException {
        // Clear the session data
        Session.clear();

        // Load the Login FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/stims/fxml/Login.fxml"));
        Parent loginRoot = loader.load();
        Scene loginScene = new Scene(loginRoot, 1280, 720); // Use the same dimensions as the initial login screen

        // Get the current stage
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Set the new scene to the current stage
        currentStage.setScene(loginScene);
        currentStage.centerOnScreen();
    }
}
