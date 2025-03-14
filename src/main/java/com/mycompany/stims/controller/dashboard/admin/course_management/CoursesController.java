package com.mycompany.stims.controller.dashboard.admin.course_management;

import com.mycompany.stims.model.Course;
import com.mycompany.stims.database.CourseDAO;
import com.mycompany.stims.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The CoursesController class manages the course management interface for
 * administrators. It allows adding, updating, deleting, and filtering courses,
 * and displays them in a table.
 */
public class CoursesController implements Initializable {

    // FXML Components
    @FXML
    private TextField courseNameField;
    @FXML
    private TextArea courseDescriptionTextArea;
    @FXML
    private ComboBox<Integer> credietHrComboBox;
    @FXML
    private ComboBox<Integer> courseLevelComboBox;
    @FXML
    private ComboBox<Integer> yearComboBox;
    @FXML
    private ComboBox<String> departmentComboBox;
    @FXML
    private ComboBox<Integer> semesterComboBox;
    @FXML
    private Button addCourseButton;
    @FXML
    private ComboBox<String> departmentFilterComboBox;
    @FXML
    private Button applyFilterButton;
    @FXML
    private Button clearFilterButton;
    @FXML
    private Button updateCourseButton;
    @FXML
    private Button deleteCourseButton;
    @FXML
    private VBox contentArea;
    @FXML
    private TableView<Course> courseTable;
    @FXML
    private TableColumn<Course, Integer> courseIdColumn;
    @FXML
    private TableColumn<Course, String> courseCodeColumn;
    @FXML
    private TableColumn<Course, String> courseNameColumn;
    @FXML
    private TableColumn<Course, Integer> creditsColumn;
    @FXML
    private TableColumn<Course, Integer> courseLevelColumn;
    @FXML
    private TableColumn<Course, Integer> yearColumn;
    @FXML
    private TableColumn<Course, Integer> semesterColumn;
    @FXML
    private TableColumn<Course, String> departmentNameColumn;

    private CourseDAO courseDAO;
    private ObservableList<Course> courseList;
    private Map<String, Integer> departmentMap; // Maps department names to IDs
    private Connection connection;

    /**
     * Initializes the controller class. Sets up the database connection,
     * initializes UI components, and loads course data into the table.
     *
     * @param url the location used to resolve relative paths for the root
     * object
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connection = DatabaseConnection.getConnection();
        if (connection != null) {
            courseDAO = new CourseDAO(connection);

            // Initialize table columns
            courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("courseId"));
            courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
            courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
            creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));
            courseLevelColumn.setCellValueFactory(new PropertyValueFactory<>("courseLevel"));
            yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
            semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));

            departmentNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Course, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Course, String> param) {
                    int departmentId = param.getValue().getDepartmentId();
                    String departmentName = getDepartmentNameById(departmentId);
                    return new javafx.beans.property.SimpleStringProperty(departmentName);
                }
            });

            // Load all courses
            loadCourses();

            // Initialize ComboBoxes
            initializeComboBoxes();

            // Initialize department filter
            initializeDepartmentFilter();

            // Add listener for table selection
            courseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    prefillUpdateFields(newSelection);
                }
            });
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to the database.");
        }
    }

    /**
     * Initializes the ComboBoxes with appropriate values.
     */
    private void initializeComboBoxes() {
        // Populate credit hours, course level, and year ComboBoxes with numbers 1-10
        ObservableList<Integer> numbers = FXCollections.observableArrayList();
        for (int i = 1; i <= 10; i++) {
            numbers.add(i);
        }
        credietHrComboBox.setItems(numbers);
        courseLevelComboBox.setItems(numbers);
        yearComboBox.setItems(numbers);

        // Populate semester ComboBox with 1 and 2
        ObservableList<Integer> semesters = FXCollections.observableArrayList(1, 2);
        semesterComboBox.setItems(semesters);

        // Populate department ComboBox
        departmentMap = new HashMap<>();
        String sql = "SELECT department_id, department_name FROM Department";
        try (PreparedStatement pstmt = connection.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int departmentId = rs.getInt("department_id");
                String departmentName = rs.getString("department_name");
                departmentMap.put(departmentName, departmentId);
                departmentComboBox.getItems().add(departmentName);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load departments: " + e.getMessage());
        }
    }

    /**
     * Loads all courses from the database and populates the table.
     */
    private void loadCourses() {
        courseList = FXCollections.observableArrayList(courseDAO.getAllCourses());
        courseTable.setItems(courseList);
    }

    /**
     * Initializes the department filter ComboBox.
     */
    private void initializeDepartmentFilter() {
        departmentFilterComboBox.setItems(departmentComboBox.getItems());
    }

    /**
     * Handles the "Add Course" button click event. Validates input and adds a
     * new course to the database.
     */
    @FXML
    private void handleAddCourse() {
        try {
            String courseName = courseNameField.getText();
            String courseDescription = courseDescriptionTextArea.getText();
            int credits = credietHrComboBox.getValue();
            int courseLevel = courseLevelComboBox.getValue();
            int year = yearComboBox.getValue();
            int semester = semesterComboBox.getValue();
            int departmentId = departmentMap.get(departmentComboBox.getValue());

            // Confirmation dialog
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Add");
            confirmation.setHeaderText("Add New Course");
            confirmation.setContentText("Are you sure you want to add this course?");
            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Course newCourse = new Course(courseName, courseDescription, credits, courseLevel, year, semester, departmentId);
                    courseDAO.addCourse(newCourse);
                    loadCourses();
                    clearInputFields();
                }
            });
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill all fields correctly.");
        }
    }

    /**
     * Handles the "Apply Filter" button click event. Filters courses by the
     * selected department.
     */
    @FXML
    private void handleApplyFilter() {
        String selectedDepartment = departmentFilterComboBox.getValue();
        if (selectedDepartment != null) {
            int departmentId = departmentMap.get(selectedDepartment);
            List<Course> filteredCourses = courseDAO.getCoursesByDepartment(departmentId);
            courseList = FXCollections.observableArrayList(filteredCourses);
            courseTable.setItems(courseList);
        }
    }

    /**
     * Handles the "Clear Filter" button click event. Clears the filter and
     * reloads all courses.
     */
    @FXML
    private void handleClearFilter() {
        departmentFilterComboBox.getSelectionModel().clearSelection();
        loadCourses();
    }

    /**
     * Handles the "Update Course" button click event. Validates input and
     * updates the selected course.
     */
    @FXML
    private void handleUpdateCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            try {
                String courseName = courseNameField.getText();
                String courseDescription = courseDescriptionTextArea.getText();
                int credits = credietHrComboBox.getValue();
                int courseLevel = courseLevelComboBox.getValue();
                int year = yearComboBox.getValue();
                int semester = semesterComboBox.getValue();
                int departmentId = departmentMap.get(departmentComboBox.getValue());

                // Confirmation dialog
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirm Update");
                confirmation.setHeaderText("Update Course");
                confirmation.setContentText("Are you sure you want to update this course?");
                confirmation.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        selectedCourse.setCourseName(courseName);
                        selectedCourse.setCourseDescription(courseDescription);
                        selectedCourse.setCredits(credits);
                        selectedCourse.setCourseLevel(courseLevel);
                        selectedCourse.setYear(year);
                        selectedCourse.setSemester(semester);
                        selectedCourse.setDepartmentId(departmentId);

                        courseDAO.updateCourse(selectedCourse.getCourseId(), selectedCourse);
                        loadCourses();
                        clearInputFields();
                    }
                });
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill all fields correctly.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "No Selection", "Please select a course to update.");
        }
    }

    /**
     * Handles the "Delete Course" button click event. Deletes the selected
     * course from the database.
     */
    @FXML
    private void handleDeleteCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Delete");
            confirmation.setHeaderText("Delete Course");
            confirmation.setContentText("Are you sure you want to delete this course?");
            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    courseDAO.deleteCourse(selectedCourse.getCourseId());
                    loadCourses();
                    clearInputFields();
                }
            });
        } else {
            showAlert(Alert.AlertType.ERROR, "No Selection", "Please select a course to delete.");
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
     * Prefills the input fields with data from the selected course.
     *
     * @param course the selected course
     */
    private void prefillUpdateFields(Course course) {
        courseNameField.setText(course.getCourseName());
        courseDescriptionTextArea.setText(course.getCourseDescription());
        credietHrComboBox.setValue(course.getCredits());
        courseLevelComboBox.setValue(course.getCourseLevel());
        yearComboBox.setValue(course.getYear());
        semesterComboBox.setValue(course.getSemester());
        departmentComboBox.setValue(getDepartmentNameById(course.getDepartmentId()));
    }

    /**
     * Clears all input fields.
     */
    private void clearInputFields() {
        courseNameField.clear();
        courseDescriptionTextArea.clear();
        credietHrComboBox.getSelectionModel().clearSelection();
        courseLevelComboBox.getSelectionModel().clearSelection();
        yearComboBox.getSelectionModel().clearSelection();
        semesterComboBox.getSelectionModel().clearSelection();
        departmentComboBox.getSelectionModel().clearSelection();
    }

    /**
     * Retrieves the department name by its ID.
     *
     * @param departmentId the department ID
     * @return the department name, or null if not found
     */
    private String getDepartmentNameById(int departmentId) {
        String sql = "SELECT department_name FROM Department WHERE department_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, departmentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("department_name");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to fetch department name: " + e.getMessage());
        }
        return null;
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
}
