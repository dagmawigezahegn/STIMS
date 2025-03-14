package com.mycompany.stims.controller.dashboard.admin.teacher_management;

import com.mycompany.stims.database.CourseOfferingDAO;
import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.database.TeacherCourseDAO;
import com.mycompany.stims.model.CourseOffering;
import com.mycompany.stims.model.TeacherCourse;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * The AssignCourseToTeacherController class manages the interface for assigning
 * courses to teachers. It allows administrators to add, update, delete, and
 * view teacher-course assignments.
 */
public class AssignCourseToTeacherController implements Initializable {

    @FXML
    private TextField teacherIdField;
    @FXML
    private TextField offeringIdField;
    @FXML
    private DatePicker assignedDatePicker;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private TableView<TeacherCourse> teacherCourseTable;
    @FXML
    private TableColumn<TeacherCourse, Integer> teacherCourseIdColumn;
    @FXML
    private TableColumn<TeacherCourse, Integer> teacherIdColumn;
    @FXML
    private TableColumn<TeacherCourse, Integer> offeringIdColumn;
    @FXML
    private TableColumn<TeacherCourse, Date> assignedDateColumn;
    @FXML
    private Button refreshButton;
    @FXML
    private Button backButton;
    @FXML
    private TextField searchField;
    @FXML
    private VBox contentArea;

    @FXML
    private TableView<CourseOffering> courseOfferingTable;
    @FXML
    private TableColumn<CourseOffering, Integer> courseOfferingIdColumn;
    @FXML
    private TableColumn<CourseOffering, String> courseCodeColumn;
    @FXML
    private TableColumn<CourseOffering, Integer> academicYearColumn;
    @FXML
    private TableColumn<CourseOffering, Integer> semesterColumn;

    private TeacherCourseDAO teacherCourseDAO;
    private CourseOfferingDAO courseOfferingDAO;
    private ObservableList<TeacherCourse> teacherCourseList;
    private ObservableList<CourseOffering> courseOfferingList;

    /**
     * Initializes the controller class. Sets up database connections,
     * initializes table columns, and refreshes the tables.
     *
     * @param url the location used to resolve relative paths for the root
     * object
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            teacherCourseDAO = new TeacherCourseDAO(connection);
            courseOfferingDAO = new CourseOfferingDAO(connection);
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to the database.");
        }

        initializeTableColumns();
        refreshTables();

        teacherCourseTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        populateInputFields(newValue);
                    } else {
                        clearFields();
                    }
                }
        );

        courseOfferingTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        offeringIdField.setText(String.valueOf(newValue.getOfferingId()));
                    } else {
                        clearFields();
                    }
                }
        );

        // Button actions
        addButton.setOnAction(event -> addTeacherCourse());
        updateButton.setOnAction(event -> updateTeacherCourse());
        deleteButton.setOnAction(event -> deleteTeacherCourse());
        refreshButton.setOnAction(event -> refreshTables());
    }

    /**
     * Initializes the table columns for both teacher-course assignments and
     * course offerings.
     */
    private void initializeTableColumns() {
        teacherCourseIdColumn.setCellValueFactory(new PropertyValueFactory<>("teacherCourseId"));
        teacherIdColumn.setCellValueFactory(new PropertyValueFactory<>("teacherId"));
        offeringIdColumn.setCellValueFactory(new PropertyValueFactory<>("offeringId"));
        assignedDateColumn.setCellValueFactory(new PropertyValueFactory<>("assignedDate"));

        courseOfferingIdColumn.setCellValueFactory(new PropertyValueFactory<>("offeringId"));
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        academicYearColumn.setCellValueFactory(new PropertyValueFactory<>("academicYear"));
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
    }

    /**
     * Refreshes both the teacher-course assignments table and the course
     * offerings table.
     */
    private void refreshTables() {
        refreshTeacherCourseTable();
        refreshCourseOfferingTable();
    }

    /**
     * Refreshes the teacher-course assignments table by fetching data from the
     * database.
     */
    private void refreshTeacherCourseTable() {
        try {
            List<TeacherCourse> teacherCourses = teacherCourseDAO.getAllTeacherCourses();
            teacherCourseList = FXCollections.observableArrayList(teacherCourses);
            teacherCourseTable.setItems(teacherCourseList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load teacher-course assignments: " + e.getMessage());
        }
    }

    /**
     * Refreshes the course offerings table by fetching data from the database.
     */
    private void refreshCourseOfferingTable() {
        try {
            List<CourseOffering> courseOfferings = courseOfferingDAO.getAllCourseOfferings();
            courseOfferingList = FXCollections.observableArrayList(courseOfferings);
            courseOfferingTable.setItems(courseOfferingList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load course offerings: " + e.getMessage());
        }
    }

    /**
     * Validates user inputs for teacher ID, offering ID, and assigned date.
     *
     * @return true if inputs are valid, false otherwise
     */
    private boolean validateInputs() {
        if (teacherIdField.getText().isEmpty() || offeringIdField.getText().isEmpty() || assignedDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "All fields are required.");
            return false;
        }

        try {
            Integer.parseInt(teacherIdField.getText());
            Integer.parseInt(offeringIdField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Teacher ID and Offering ID must be numeric.");
            return false;
        }

        return true;
    }

    /**
     * Handles the "Add" button click event. Adds a new teacher-course
     * assignment to the database.
     */
    private void addTeacherCourse() {
        if (!validateInputs()) {
            return;
        }
        try {
            int teacherId = Integer.parseInt(teacherIdField.getText());
            int offeringId = Integer.parseInt(offeringIdField.getText());
            Date assignedDate = Date.valueOf(assignedDatePicker.getValue());

            String confirmationMessage = String.format(
                    "Are you sure you want to add the following assignment?\n\nTeacher ID: %d\nOffering ID: %d\nAssigned Date: %s",
                    teacherId, offeringId, assignedDate
            );

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Addition");
            confirmationAlert.setHeaderText("Add Teacher-Course Assignment");
            confirmationAlert.setContentText(confirmationMessage);

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                TeacherCourse teacherCourse = new TeacherCourse(0, teacherId, offeringId, assignedDate);
                teacherCourseDAO.addTeacherCourse(teacherCourse);
                refreshTeacherCourseTable();
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Teacher-course assignment added successfully.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid input or database error: " + e.getMessage());
        }
    }

    /**
     * Handles the "Update" button click event. Updates an existing
     * teacher-course assignment in the database.
     */
    private void updateTeacherCourse() {
        try {
            TeacherCourse selectedTeacherCourse = teacherCourseTable.getSelectionModel().getSelectedItem();
            if (selectedTeacherCourse == null) {
                showAlert(Alert.AlertType.WARNING, "Selection Error", "No teacher-course assignment selected.");
                return;
            }

            if (!validateInputs()) {
                return;
            }

            int teacherId = Integer.parseInt(teacherIdField.getText());
            int offeringId = Integer.parseInt(offeringIdField.getText());
            Date assignedDate = Date.valueOf(assignedDatePicker.getValue());

            String confirmationMessage = String.format(
                    "Are you sure you want to update the following assignment?\n\nTeacher ID: %d\nOffering ID: %d\nAssigned Date: %s",
                    teacherId, offeringId, assignedDate
            );

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Update");
            confirmationAlert.setHeaderText("Update Teacher-Course Assignment");
            confirmationAlert.setContentText(confirmationMessage);

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                selectedTeacherCourse.setTeacherId(teacherId);
                selectedTeacherCourse.setOfferingId(offeringId);
                selectedTeacherCourse.setAssignedDate(assignedDate);

                teacherCourseDAO.updateTeacherCourse(selectedTeacherCourse);
                refreshTables();
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Teacher-course assignment updated successfully.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid input or database error: " + e.getMessage());
        }
    }

    /**
     * Handles the "Delete" button click event. Deletes a teacher-course
     * assignment from the database.
     */
    @FXML
    private void deleteTeacherCourse() {
        try {
            TeacherCourse selectedTeacherCourse = teacherCourseTable.getSelectionModel().getSelectedItem();
            if (selectedTeacherCourse == null) {
                showAlert(Alert.AlertType.WARNING, "Selection Error", "No teacher-course assignment selected.");
                return;
            }

            String confirmationMessage = String.format(
                    "Are you sure you want to delete the following assignment?\n\nTeacher ID: %d\nOffering ID: %d\nAssigned Date: %s",
                    selectedTeacherCourse.getTeacherId(), selectedTeacherCourse.getOfferingId(), selectedTeacherCourse.getAssignedDate()
            );

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText("Delete Teacher-Course Assignment");
            confirmationAlert.setContentText(confirmationMessage);

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                teacherCourseDAO.deleteTeacherCourse(selectedTeacherCourse.getTeacherCourseId());
                refreshTeacherCourseTable();
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Teacher-course assignment deleted successfully.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete teacher-course assignment: " + e.getMessage());
        }
    }

    /**
     * Handles the "Back" button click event. Navigates back to the Teacher
     * Management screen.
     */
    @FXML
    private void handleBackButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/stims/fxml/dashboard/admin/teacher_management/Teacher_Management.fxml"));
            Parent teacherManagementPane = loader.load();

            StackPane parentContainer = (StackPane) contentArea.getParent();
            parentContainer.getChildren().clear();
            parentContainer.getChildren().add(teacherManagementPane);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load Teacher Management screen: " + e.getMessage());
        }
    }

    /**
     * Clears all input fields.
     */
    private void clearFields() {
        teacherIdField.clear();
        offeringIdField.clear();
        assignedDatePicker.setValue(null);
    }

    /**
     * Displays an alert dialog with the specified type, title, and message.
     *
     * @param alertType the type of alert
     * @param title the title of the alert
     * @param message the message to display
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Populates the input fields with data from the selected teacher-course
     * assignment.
     *
     * @param teacherCourse the selected teacher-course assignment
     */
    private void populateInputFields(TeacherCourse teacherCourse) {
        if (teacherCourse != null) {
            teacherIdField.setText(String.valueOf(teacherCourse.getTeacherId()));
            offeringIdField.setText(String.valueOf(teacherCourse.getOfferingId()));
            assignedDatePicker.setValue(teacherCourse.getAssignedDate().toLocalDate());
        } else {
            clearFields();
        }
    }
}
