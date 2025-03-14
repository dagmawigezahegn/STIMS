package com.mycompany.stims.controller.dashboard.teacher;

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
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Controller class for the teacher dashboard. This class handles the UI logic
 * for navigating between different sections of the teacher dashboard, such as
 * home, assigned courses, enrolled students, managing grades, and profile.
 */
public class Teacher_DashboardController implements Initializable {

    @FXML
    private StackPane dynamicContentContainer;

    /**
     * Initializes the controller and loads the home view by default.
     *
     * @param url the location used to resolve relative paths for the root
     * object
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Load the Home view by default on initialization
        showHome();
    }

    /**
     * Loads the home view into the dynamic content container.
     */
    @FXML
    public void showHome() {
        loadUI("/com/mycompany/stims/fxml/dashboard/teacher/home/Home.fxml");
    }

    /**
     * Loads the assigned courses view into the dynamic content container.
     */
    @FXML
    public void showAssignedCourses() {
        loadUI("/com/mycompany/stims/fxml/dashboard/teacher/assigned_courses/Assigned_Courses.fxml");
    }

    /**
     * Loads the enrolled students view into the dynamic content container.
     */
    @FXML
    public void showEnrolledStudents() {
        loadUI("/com/mycompany/stims/fxml/dashboard/teacher/assigned_courses/View_Enrolled_Students.fxml");
    }

    /**
     * Loads the manage grades view into the dynamic content container.
     */
    @FXML
    public void showManageGrades() {
        loadUI("/com/mycompany/stims/fxml/dashboard/teacher/manage_grades/Manage_Grades.fxml");
    }

    /**
     * Loads the profile view into the dynamic content container.
     */
    @FXML
    public void showProfile() {
        loadUI("/com/mycompany/stims/fxml/dashboard/teacher/profile/Profile.fxml");
    }

    /**
     * Displays a notification dialog.
     */
    @FXML
    private void showNotificationDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifications");
        alert.setHeaderText(null);
        alert.setContentText("No new notifications.");
        alert.showAndWait();
    }

    /**
     * Handles the sign-out action. Clears the session data and navigates back
     * to the login screen.
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
}
