package com.mycompany.stims.controller.dashboard.admin.course_management;

import com.mycompany.stims.model.CourseOffering;
import com.mycompany.stims.database.CourseOfferingDAO;
import com.mycompany.stims.database.DatabaseConnection;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Controller class for managing course offerings in the admin dashboard. This
 * class handles the UI logic for adding, updating, deleting, and filtering
 * course offerings.
 */
public class CourseOfferingsController implements Initializable {

    @FXML
    private TextField courseCodeField;
    @FXML
    private TextField academicYearField;
    @FXML
    private ComboBox<String> yearComboBox;
    @FXML
    private ComboBox<String> semesterComboBox;
    @FXML
    private Button addOfferingButton;
    @FXML
    private ComboBox<String> courseCodeFilterComboBox;
    @FXML
    private Button applyFilterButton;
    @FXML
    private Button clearFilterButton;
    @FXML
    private Button updateOfferingButton;
    @FXML
    private Button deleteOfferingButton;

    @FXML
    private VBox contentArea;
    @FXML
    private TableView<CourseOffering> courseOfferingTable;
    @FXML
    private TableColumn<CourseOffering, Integer> offeringIdColumn;
    @FXML
    private TableColumn<CourseOffering, Integer> courseIdColumn;
    @FXML
    private TableColumn<CourseOffering, String> courseCodeColumn;
    @FXML
    private TableColumn<CourseOffering, Integer> academicYearColumn;
    @FXML
    private TableColumn<CourseOffering, Integer> yearColumn;
    @FXML
    private TableColumn<CourseOffering, Integer> semesterColumn;

    private CourseOfferingDAO courseOfferingDAO;
    private ObservableList<CourseOffering> courseOfferingList;
    private Map<String, Integer> courseCodeMap;

    /**
     * Initializes the controller and sets up the UI components.
     *
     * @param url the location used to resolve relative paths for the root
     * object
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            courseOfferingDAO = new CourseOfferingDAO(connection);

            // Set up table columns
            offeringIdColumn.setCellValueFactory(new PropertyValueFactory<>("offeringId"));
            courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("courseId"));
            courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
            academicYearColumn.setCellValueFactory(new PropertyValueFactory<>("academicYear"));
            yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
            semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));

            // Load course offerings and initialize filters
            loadCourseOfferings();
            initializeCourseCodeFilter();

            // Initialize year and semester ComboBoxes
            yearComboBox.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
            semesterComboBox.getItems().addAll("1", "2");

            // Add listener for table row selection
            courseOfferingTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    prefillUpdateFields(newSelection);
                }
            });
        } else {
            System.err.println("Failed to initialize controller due to database connection error.");
        }
    }

    /**
     * Loads all course offerings from the database and populates the table.
     */
    private void loadCourseOfferings() {
        courseOfferingList = FXCollections.observableArrayList(courseOfferingDAO.getAllCourseOfferings());
        courseOfferingTable.setItems(courseOfferingList);
    }

    /**
     * Initializes the course code filter ComboBox with course codes from the
     * database.
     */
    private void initializeCourseCodeFilter() {
        courseCodeMap = new HashMap<>();
        String sql = "SELECT course_id, course_code FROM Course";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement pstmt = connection.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int courseId = rs.getInt("course_id");
                String courseCode = rs.getString("course_code");
                courseCodeMap.put(courseCode, courseId);
                courseCodeFilterComboBox.getItems().add(courseCode);
            }
        } catch (SQLException e) {
            System.err.println("Error loading course codes: " + e.getMessage());
        }
    }

    /**
     * Handles the "Add Offering" button click event. Validates input and adds a
     * new course offering to the database.
     */
    @FXML
    private void handleAddOffering() {
        try {
            String courseCode = courseCodeField.getText();
            if (!courseCodeMap.containsKey(courseCode)) {
                showErrorDialog("Invalid Course Code", "The entered course code does not exist.");
                return;
            }

            int courseId = courseCodeMap.get(courseCode);
            int academicYear = Integer.parseInt(academicYearField.getText());
            int year = Integer.parseInt(yearComboBox.getValue());
            int semester = Integer.parseInt(semesterComboBox.getValue());

            Alert confirmation = new Alert(AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Add");
            confirmation.setHeaderText("Add New Course Offering");
            confirmation.setContentText("Are you sure you want to add this course offering?");
            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    CourseOffering newOffering = new CourseOffering(courseId, academicYear, year, semester);
                    newOffering.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    newOffering.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    courseOfferingDAO.addCourseOffering(newOffering);

                    loadCourseOfferings();
                    clearInputFields();
                }
            });
        } catch (NumberFormatException e) {
            showErrorDialog("Invalid Input", "Please enter valid numeric values for academic year, year, and semester.");
        }
    }

    /**
     * Handles the "Apply Filter" button click event. Filters the course
     * offerings table based on the selected course code.
     */
    @FXML
    private void handleApplyFilter() {
        String selectedCourseCode = courseCodeFilterComboBox.getSelectionModel().getSelectedItem();
        if (selectedCourseCode != null) {
            int courseId = courseCodeMap.get(selectedCourseCode);
            List<CourseOffering> filteredOfferings = courseOfferingDAO.getAllCourseOfferings().stream()
                    .filter(offering -> offering.getCourseId() == courseId)
                    .collect(Collectors.toList());
            courseOfferingList = FXCollections.observableArrayList(filteredOfferings);
            courseOfferingTable.setItems(courseOfferingList);
        }
    }

    /**
     * Handles the "Clear Filter" button click event. Clears the filter and
     * reloads all course offerings.
     */
    @FXML
    private void handleClearFilter() {
        courseCodeFilterComboBox.getSelectionModel().clearSelection();
        loadCourseOfferings();
    }

    /**
     * Handles the "Update Offering" button click event. Validates input and
     * updates the selected course offering in the database.
     */
    @FXML
    private void handleUpdateOffering() {
        CourseOffering selectedOffering = courseOfferingTable.getSelectionModel().getSelectedItem();
        if (selectedOffering != null) {
            try {
                String courseCode = courseCodeField.getText();
                if (!courseCodeMap.containsKey(courseCode)) {
                    showErrorDialog("Invalid Course Code", "The entered course code does not exist.");
                    return;
                }

                int courseId = courseCodeMap.get(courseCode);
                int academicYear = Integer.parseInt(academicYearField.getText());
                int year = Integer.parseInt(yearComboBox.getValue());
                int semester = Integer.parseInt(semesterComboBox.getValue());

                Alert confirmation = new Alert(AlertType.CONFIRMATION);
                confirmation.setTitle("Confirm Update");
                confirmation.setHeaderText("Update Course Offering");
                confirmation.setContentText("Are you sure you want to update this course offering?");
                confirmation.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        selectedOffering.setCourseId(courseId);
                        selectedOffering.setAcademicYear(academicYear);
                        selectedOffering.setYear(year);
                        selectedOffering.setSemester(semester);
                        selectedOffering.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

                        courseOfferingDAO.updateCourseOffering(selectedOffering);

                        loadCourseOfferings();
                        clearInputFields();
                    }
                });
            } catch (NumberFormatException e) {
                showErrorDialog("Invalid Input", "Please enter valid numeric values for academic year, year, and semester.");
            }
        } else {
            showErrorDialog("No Selection", "Please select a course offering to update.");
        }
    }

    /**
     * Handles the "Delete Offering" button click event. Deletes the selected
     * course offering from the database after confirmation.
     */
    @FXML
    private void handleDeleteOffering() {
        CourseOffering selectedOffering = courseOfferingTable.getSelectionModel().getSelectedItem();
        if (selectedOffering != null) {
            Alert confirmation = new Alert(AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Delete");
            confirmation.setHeaderText("Delete Course Offering");
            confirmation.setContentText("Are you sure you want to delete this course offering?");
            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean isDeleted = courseOfferingDAO.deleteCourseOffering(selectedOffering.getOfferingId());

                    if (isDeleted) {
                        loadCourseOfferings();
                        clearInputFields();

                        Alert successAlert = new Alert(AlertType.INFORMATION);
                        successAlert.setTitle("Success");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("Course offering deleted successfully.");
                        successAlert.showAndWait();
                    } else {
                        Alert errorAlert = new Alert(AlertType.ERROR);
                        errorAlert.setTitle("Deletion Failed");
                        errorAlert.setHeaderText(null);
                        errorAlert.setContentText("Course offering cannot be deleted because it is associated with student or teacher records.");
                        errorAlert.showAndWait();
                    }
                }
            });
        } else {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setTitle("No Selection");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Please select a course offering to delete.");
            errorAlert.showAndWait();
        }
    }

    /**
     * Handles the "Back" button click event. Navigates back to the Course
     * Management screen.
     */
    @FXML
    private void handleBackButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/stims/fxml/dashboard/admin/course_management/Course_Management.fxml"));
            Parent studentManagementPane = loader.load();

            StackPane parentContainer = (StackPane) contentArea.getParent();

            parentContainer.getChildren().clear();
            parentContainer.getChildren().add(studentManagementPane);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load Student Management screen: " + e.getMessage());
        }
    }

    /**
     * Prefills the input fields with the details of the selected course
     * offering.
     *
     * @param offering the selected course offering
     */
    private void prefillUpdateFields(CourseOffering offering) {
        // Find the course code for the given course ID
        String courseCode = courseCodeMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(offering.getCourseId()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("");

        courseCodeField.setText(courseCode);
        academicYearField.setText(String.valueOf(offering.getAcademicYear()));
        yearComboBox.setValue(String.valueOf(offering.getYear()));
        semesterComboBox.setValue(String.valueOf(offering.getSemester()));
    }

    /**
     * Clears all input fields.
     */
    private void clearInputFields() {
        courseCodeField.clear();
        academicYearField.clear();
        yearComboBox.getSelectionModel().clearSelection();
        semesterComboBox.getSelectionModel().clearSelection();
    }

    /**
     * Displays an error dialog with the specified title and message.
     *
     * @param title the title of the error dialog
     * @param message the message to display in the error dialog
     */
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an alert dialog with the specified type, title, and message.
     *
     * @param alertType the type of alert (e.g., ERROR, INFORMATION)
     * @param title the title of the alert
     * @param message the message to display in the alert
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
