package com.mycompany.stims.controller.dashboard.admin;

import com.mycompany.stims.utils.Session;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

/**
 * The `Admin_DashboardController` class is the controller for the admin
 * dashboard. It manages the navigation between different views (e.g., home,
 * student management, teacher management, etc.) and handles user actions such
 * as signing out.
 */
public class Admin_DashboardController implements Initializable {

    @FXML
    private VBox contentArea;

    @FXML
    private StackPane dynamicContentContainer;

    /**
     * Initializes the controller after its root element has been completely
     * processed. This method loads the default "Home" view when the dashboard
     * is initialized.
     *
     * @param url The location used to resolve relative paths for the root
     * object, or `null` if the location is not known.
     * @param rb The resources used to localize the root object, or `null` if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Load the Home view by default on initialization
        showHome();
    }

    /**
     * Loads and displays the "Home" view in the dynamic content area.
     */
    @FXML
    public void showHome() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/home/Home.fxml");
    }

    /**
     * Loads and displays the "Student Management" view in the dynamic content
     * area.
     */
    @FXML
    public void showStudentManagement() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/student_management/Student_Management.fxml");
    }

    /**
     * Loads and displays the "Teacher Management" view in the dynamic content
     * area.
     */
    @FXML
    public void showTeacherManagement() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/teacher_management/Teacher_Management.fxml");
    }

    /**
     * Loads and displays the "Course Management" view in the dynamic content
     * area.
     */
    @FXML
    public void showCourseManagement() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/course_management/Course_Management.fxml");
    }

    /**
     * Loads and displays the "Report Management" view in the dynamic content
     * area.
     */
    @FXML
    public void showReportManagement() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/report_management/Report_Management.fxml");
    }

    /**
     * Loads and displays the "Profile" view in the dynamic content area.
     */
    @FXML
    public void showProfile() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/profile/Profile.fxml");
    }

    /**
     * Loads the specified FXML file and updates the dynamic content area with
     * the new view.
     *
     * @param fxmlPath The path to the FXML file to load.
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
     * Handles the "Sign Out" action. Clears the session data and navigates back
     * to the login screen.
     *
     * @param event The action event triggered by the user.
     * @throws IOException If an error occurs while loading the login FXML file.
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
