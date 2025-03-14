package com.mycompany.stims.controller.dashboard.admin.course_management;

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
 * Controller for the Course Management dashboard in the admin panel. This class
 * handles the navigation between different course management views, such as
 * courses, course offerings, and student enrollment.
 */
public class CourseManagementController implements Initializable {

    // FXML Components
    /**
     * Button to navigate to the Courses view.
     */
    @FXML
    private Button coursesButton;

    /**
     * Button to navigate to the Course Offerings view.
     */
    @FXML
    private Button courseOfferingsButton;

    /**
     * Button to navigate to the Enroll Students view.
     */
    @FXML
    private Button enrollStudentsButton;

    /**
     * AnchorPane used as a container for dynamic content.
     */
    @FXML
    private AnchorPane contentArea;

    /**
     * StackPane used to dynamically load and display FXML content.
     */
    @FXML
    private StackPane dynamicContentContainer;

    /**
     * Initializes the controller class. Sets up button actions to load the
     * corresponding FXML views when clicked.
     *
     * @param url the location used to resolve relative paths for the root
     * object, or null if the location is not known
     * @param rb the resources used to localize the root object, or null if the
     * root object was not localized
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up button actions
        coursesButton.setOnAction(event -> handlecoursesButton());
        courseOfferingsButton.setOnAction(event -> handlecourseOfferingsButton());
        enrollStudentsButton.setOnAction(event -> handleenrollStudentsButton());
    }

    /**
     * Handles the action for the Courses button. Loads the Courses FXML view
     * into the dynamic content container.
     */
    private void handlecoursesButton() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/course_management/Courses.fxml");
    }

    /**
     * Handles the action for the Course Offerings button. Loads the Course
     * Offerings FXML view into the dynamic content container.
     */
    private void handlecourseOfferingsButton() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/course_management/Course_Offerings.fxml");
    }

    /**
     * Handles the action for the Enroll Students button. Loads the Enroll
     * Students FXML view into the dynamic content container.
     */
    private void handleenrollStudentsButton() {
        loadUI("/com/mycompany/stims/fxml/dashboard/admin/course_management/Enroll_Students.fxml");
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
