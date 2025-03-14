package com.mycompany.stims.controller.dashboard.student;

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
import com.mycompany.stims.utils.Session;

/**
 * The Student_DashboardController class manages the student dashboard
 * interface. It provides navigation to various student-related screens such as
 * home, enrolled courses, grades, academic records, and profile. It also
 * handles user sign-out functionality.
 */
public class Student_DashboardController implements Initializable {

    @FXML
    private StackPane dynamicContentContainer;

    /**
     * Initializes the controller class. Loads the Home view by default when the
     * dashboard is initialized.
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
     * Loads the Home screen into the dynamic content container.
     */
    @FXML
    public void showHome() {
        loadUI("/com/mycompany/stims/fxml/dashboard/student/home/Home.fxml");
    }

    /**
     * Loads the View Enrolled Courses screen into the dynamic content
     * container.
     */
    @FXML
    public void showViewEnrolledCourses() {
        loadUI("/com/mycompany/stims/fxml/dashboard/student/view_enrolled_courses/View_Enrolled_Courses.fxml");
    }

    /**
     * Loads the View Grades screen into the dynamic content container.
     */
    @FXML
    public void showViewGrades() {
        loadUI("/com/mycompany/stims/fxml/dashboard/student/view_grades/View_Grades.fxml");
    }

    /**
     * Loads the Academic Records screen into the dynamic content container.
     */
    @FXML
    public void showAcademicRecords() {
        loadUI("/com/mycompany/stims/fxml/dashboard/student/academic_records/Academic_Records.fxml");
    }

    /**
     * Loads the Profile screen into the dynamic content container.
     */
    @FXML
    public void showProfile() {
        loadUI("/com/mycompany/stims/fxml/dashboard/student/profile/Profile.fxml");
    }

    /**
     * Displays a notification dialog to the user.
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
     * Handles the "Sign Out" button click event. Clears the session data and
     * navigates back to the login screen.
     *
     * @param event the action event triggered by the button click
     * @throws IOException if the login screen FXML file cannot be loaded
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
     * Loads a new UI screen into the dynamic content container.
     *
     * @param fxmlPath the path to the FXML file for the new UI screen
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
