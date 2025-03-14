package com.mycompany.stims.controller.dashboard.admin.teacher_management;

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
 * Controller class for managing teachers in the admin dashboard. This class
 * handles the UI logic for navigating between different teacher management
 * screens, such as adding, editing, viewing teacher profiles, and assigning
 * courses to teachers.
 */
public class TeacherManagementController implements Initializable {

    // FXML Components
    @FXML
    private Button addTeacherButton;

    @FXML
    private Button editTeacherButton;

    @FXML
    private Button viewTeacherProfileButton;

    @FXML
    private Button assignCourseToTeacherButton;

    @FXML
    private AnchorPane contentArea;

    @FXML
    private StackPane dynamicContentContainer;

    /**
     * Initializes the controller and sets up button actions.
     *
     * @param url the location used to resolve relative paths for the root
     * object
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up button actions
        addTeacherButton.setOnAction(event -> handleaddTeacherButton());
        editTeacherButton.setOnAction(event -> handleeditTeacherButton());
        viewTeacherProfileButton.setOnAction(event -> handleviewTeacherProfileButton());
        assignCourseToTeacherButton.setOnAction(event -> handleassignCourseToTeacherButton());
    }

    /**
     * Handles the "Add Teacher" button click event. Loads the "Add Teacher"
     * screen into the dynamic content container.
     */
    private void handleaddTeacherButton() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/teacher_management/Add_Teacher.fxml");
    }

    /**
     * Handles the "Edit Teacher" button click event. Loads the "Edit Teacher"
     * screen into the dynamic content container.
     */
    private void handleeditTeacherButton() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/teacher_management/Edit_Teacher.fxml");
    }

    /**
     * Handles the "View Teacher Profile" button click event. Loads the "View
     * Teacher Profile" screen into the dynamic content container.
     */
    private void handleviewTeacherProfileButton() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/teacher_management/View_Teacher_Profile.fxml");
    }

    /**
     * Handles the "Assign Course to Teacher" button click event. Loads the
     * "Assign Course to Teacher" screen into the dynamic content container.
     */
    private void handleassignCourseToTeacherButton() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/teacher_management/Assign_Course_To_Teacher.fxml");
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
        }
    }
}
