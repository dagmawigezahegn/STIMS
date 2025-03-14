package com.mycompany.stims.controller.dashboard.student.profile;

import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.database.StudentDAO;
import com.mycompany.stims.model.Student;
import com.mycompany.stims.utils.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

/**
 * Controller for the Student Profile feature in the student dashboard. This
 * class allows students to view their profile, change their username, and
 * update their password.
 */
public class ProfileController implements Initializable {

    /**
     * Data Access Object (DAO) for student-related database operations.
     */
    private StudentDAO studentDAO;

    /**
     * The currently logged-in student.
     */
    private Student currentStudent;

    /**
     * Initializes the controller class. Sets up the database connection and
     * loads the student profile.
     *
     * @param url the location used to resolve relative paths for the root
     * object, or null if the location is not known
     * @param rb the resources used to localize the root object, or null if the
     * root object was not localized
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            studentDAO = new StudentDAO(connection);
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Unable to connect to the database.");
        }
        loadStudentProfile();
    }

    /**
     * Loads the profile of the currently logged-in student.
     */
    private void loadStudentProfile() {
        String loggedInEmailOrUsername = Session.getLoggedInStudentEmailOrUsername();

        if (loggedInEmailOrUsername == null || loggedInEmailOrUsername.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "No student is currently logged in.");
            return;
        }

        currentStudent = studentDAO.getStudentByEmailOrUsername(loggedInEmailOrUsername);

        if (currentStudent == null) {
            showAlert(Alert.AlertType.WARNING, "Not Found", "No student found with the given email/username.");
        }
    }

    /**
     * Handles the action for viewing the student profile. Displays a dialog
     * with the student's details.
     */
    @FXML
    private void handleViewProfile() {
        if (currentStudent == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No student profile loaded.");
            return;
        }

        Dialog<ButtonType> dialog = createProfileDialog();
        dialog.showAndWait();
    }

    /**
     * Creates a dialog to display the student's profile information.
     *
     * @return the configured dialog
     */
    private Dialog<ButtonType> createProfileDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Student Profile");
        dialog.setHeaderText("View Student Information");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().add(ButtonType.CLOSE);
        dialogPane.setPrefSize(500, 400);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Add student details to the grid
        addLabelAndValue(grid, "Student ID No:", currentStudent.getStudentIdNo(), 0);
        addLabelAndValue(grid, "Username:", currentStudent.getUsername(), 1);
        addLabelAndValue(grid, "First Name:", currentStudent.getFirstName(), 2);
        addLabelAndValue(grid, "Middle Name:", currentStudent.getMiddleName(), 3);
        addLabelAndValue(grid, "Last Name:", currentStudent.getLastName(), 4);
        addLabelAndValue(grid, "Sex:", currentStudent.getSex(), 5);
        addLabelAndValue(grid, "Date of Birth:", currentStudent.getDateOfBirth().toString(), 6);
        addLabelAndValue(grid, "Email:", currentStudent.getEmail(), 7);
        addLabelAndValue(grid, "Department:", studentDAO.getDepartmentNameById(currentStudent.getDepartmentId()), 8);
        addLabelAndValue(grid, "Program:", studentDAO.getProgramNameById(currentStudent.getProgramId()), 9);
        addLabelAndValue(grid, "Enrollment Date:", currentStudent.getEnrollmentDate().toString(), 10);

        dialogPane.setContent(grid);
        return dialog;
    }

    /**
     * Adds a label and its corresponding value to the grid.
     *
     * @param grid the GridPane to which the label and value are added
     * @param labelText the text for the label
     * @param value the value to display
     * @param row the row in the grid where the label and value are added
     */
    private void addLabelAndValue(GridPane grid, String labelText, String value, int row) {
        grid.add(new Label(labelText), 0, row);
        grid.add(new Label(value), 1, row);
    }

    /**
     * Handles the action for changing the student's username. Displays a dialog
     * to input a new username and updates it in the database.
     */
    @FXML
    private void handleChangeUsername() {
        TextInputDialog dialog = new TextInputDialog(currentStudent.getUsername());
        dialog.setTitle("Change Username");
        dialog.setHeaderText("Enter a new username:");
        dialog.setContentText("Username:");
        dialog.getDialogPane().setPrefSize(400, 200);

        dialog.showAndWait().ifPresent(newUsername -> {
            if (newUsername != null && !newUsername.isEmpty()) {
                currentStudent.setUsername(newUsername);
                try {
                    studentDAO.updateStudent(currentStudent);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Username updated successfully.");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update username: " + e.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Username cannot be empty.");
            }
        });
    }

    /**
     * Handles the action for changing the student's password. Displays dialogs
     * to input and confirm a new password, then updates it in the database.
     */
    @FXML
    private void handleChangePassword() {
        Dialog<String> newPasswordDialog = createPasswordDialog("Enter a new password:", "New Password:");
        newPasswordDialog.showAndWait().ifPresent(newPassword -> {
            if (newPassword != null && !newPassword.isEmpty()) {
                Dialog<String> confirmPasswordDialog = createPasswordDialog("Confirm your new password:", "Confirm Password:");
                confirmPasswordDialog.showAndWait().ifPresent(confirmPassword -> {
                    if (newPassword.equals(confirmPassword)) {
                        try {
                            studentDAO.changePassword(currentStudent.getStudentIdNo(), newPassword);
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Password updated successfully.");
                        } catch (Exception e) {
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update password: " + e.getMessage());
                        }
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match. Please try again.");
                    }
                });
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Password cannot be empty.");
            }
        });
    }

    /**
     * Creates a dialog for entering a new or confirming a password.
     *
     * @param headerText the header text for the dialog
     * @param promptText the prompt text for the password field
     * @return the configured dialog
     */
    private Dialog<String> createPasswordDialog(String headerText, String promptText) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText(headerText);

        VBox dialogContent = new VBox(10);
        dialogContent.setPadding(new Insets(20));
        dialogContent.setAlignment(Pos.CENTER_LEFT);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(promptText);
        passwordField.setPrefWidth(250);

        dialogContent.getChildren().add(new Label(promptText));
        dialogContent.getChildren().add(passwordField);

        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().setPrefSize(400, 200);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return passwordField.getText();
            }
            return null;
        });

        return dialog;
    }

    /**
     * Utility method to display alerts.
     *
     * @param alertType the type of alert (e.g., ERROR, WARNING, INFORMATION)
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
