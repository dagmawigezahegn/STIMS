package com.mycompany.stims.controller.dashboard.admin.student_management;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

/**
 * The Student_ManagementController class manages the student management
 * interface for administrators. It provides navigation to various
 * student-related screens such as registration, profile editing, and academic
 * records.
 */
public class Student_ManagementController implements Initializable {

    // FXML Components
    @FXML
    private Button registerStudentButton;

    @FXML
    private Button editRegistrationButton;

    @FXML
    private Button viewStudentProfilesButton;

    @FXML
    private Button studentAcademicRecordsButton;

    @FXML
    private AnchorPane contentArea;
    @FXML
    private StackPane dynamicContentContainer;

    /**
     * Initializes the controller class. Sets up button actions for navigating
     * to different student management screens.
     *
     * @param url the location used to resolve relative paths for the root
     * object
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up button actions
        registerStudentButton.setOnAction(event -> handleRegisterStudent());
        editRegistrationButton.setOnAction(event -> handleEditRegistration());
        viewStudentProfilesButton.setOnAction(event -> handleViewStudentProfiles());
        studentAcademicRecordsButton.setOnAction(event -> handleViewStudentAcademicRecords());
    }

    /**
     * Handles the "Register Student" button click event. Loads the Register
     * Student screen.
     */
    private void handleRegisterStudent() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/student_management/RegisterStudent.fxml");
    }

    /**
     * Handles the "Edit Registration" button click event. Loads the Edit
     * Registration screen.
     */
    private void handleEditRegistration() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/student_management/Edit_Registration.fxml");
    }

    /**
     * Handles the "View Student Academic Records" button click event. Loads the
     * Student Academic Records screen.
     */
    private void handleViewStudentAcademicRecords() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/student_management/Student_Academic_Records.fxml");
    }

    /**
     * Handles the "View Student Profiles" button click event. Loads the View
     * Student Profiles screen.
     */
    private void handleViewStudentProfiles() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/student_management/View_Student_Profiles.fxml");
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
