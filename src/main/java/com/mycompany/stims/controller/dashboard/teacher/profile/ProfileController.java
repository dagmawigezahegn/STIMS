package com.mycompany.stims.controller.dashboard.teacher.profile;

import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.database.TeacherDAO;
import com.mycompany.stims.model.Teacher;
import com.mycompany.stims.utils.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

/**
 * Controller for the Teacher Profile feature in the teacher dashboard. This
 * class allows teachers to view their profile, change their username, and
 * update their password.
 */
public class ProfileController implements Initializable {

    /**
     * Data Access Object (DAO) for teacher-related database operations.
     */
    private TeacherDAO teacherDAO;

    /**
     * The currently logged-in teacher.
     */
    private Teacher currentTeacher;

    /**
     * Initializes the controller class. Sets up the database connection and
     * loads the teacher profile.
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
            teacherDAO = new TeacherDAO(connection);
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Unable to connect to the database.");
        }

        loadTeacherProfile();
    }

    /**
     * Loads the profile of the currently logged-in teacher.
     */
    private void loadTeacherProfile() {
        String loggedInEmailOrUsername = Session.getLoggedInTeacherEmailOrUsername();

        if (loggedInEmailOrUsername == null || loggedInEmailOrUsername.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "No teacher is currently logged in.");
            return;
        }

        currentTeacher = teacherDAO.getTeacherByEmailOrUsername(loggedInEmailOrUsername);

        if (currentTeacher == null) {
            showAlert(Alert.AlertType.WARNING, "Not Found", "No teacher found with the given email/username.");
        }
    }

    /**
     * Handles the action for viewing the teacher profile. Displays a dialog
     * with the teacher's details.
     */
    @FXML
    private void handleViewProfile() {
        if (currentTeacher == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No teacher profile loaded.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Teacher Profile");
        dialog.setHeaderText("View Teacher Information");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().add(ButtonType.CLOSE);

        dialogPane.setPrefSize(500, 400);

        // Create a GridPane for the form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Add form fields
        addLabelAndValue(grid, "Teacher ID:", String.valueOf(currentTeacher.getTeacherId()), 0);
        addLabelAndValue(grid, "Username:", currentTeacher.getUsername(), 1);
        addLabelAndValue(grid, "First Name:", currentTeacher.getFirstName(), 2);
        addLabelAndValue(grid, "Middle Name:", currentTeacher.getMiddleName(), 3);
        addLabelAndValue(grid, "Last Name:", currentTeacher.getLastName(), 4);
        addLabelAndValue(grid, "Sex:", currentTeacher.getSex(), 5);
        addLabelAndValue(grid, "Date of Birth:", currentTeacher.getDateOfBirth().toString(), 6);
        addLabelAndValue(grid, "Email:", currentTeacher.getEmail(), 7);
        addLabelAndValue(grid, "Phone Number:", currentTeacher.getPhoneNumber(), 8);
        addLabelAndValue(grid, "Address:", currentTeacher.getAddress(), 9);
        addLabelAndValue(grid, "Department:", teacherDAO.getDepartmentNameById(currentTeacher.getDepartmentId()), 10);
        addLabelAndValue(grid, "Last Login:", currentTeacher.getLastLogin() != null ? currentTeacher.getLastLogin().toString() : "Never", 11);

        dialogPane.setContent(grid);

        dialog.showAndWait();
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
     * Handles the action for changing the teacher's username. Displays a dialog
     * to input a new username and updates it in the database.
     */
    @FXML
    private void handleChangeUsername() {
        TextInputDialog dialog = new TextInputDialog(currentTeacher.getUsername());
        dialog.setTitle("Change Username");
        dialog.setHeaderText("Enter a new username:");
        dialog.setContentText("Username:");
        dialog.getDialogPane().setPrefSize(400, 200);

        dialog.showAndWait().ifPresent(newUsername -> {
            if (newUsername != null && !newUsername.isEmpty()) {
                currentTeacher.setUsername(newUsername);
                try {
                    teacherDAO.updateTeacher(currentTeacher);
                    Session.setLoggedInTeacherEmailOrUsername(newUsername); // Update session
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
     * Handles the action for changing the teacher's password. Displays dialogs
     * to input and confirm a new password, then updates it in the database.
     */
    @FXML
    private void handleChangePassword() {
        // Step 1: Enter new password
        Dialog<String> newPasswordDialog = createPasswordDialog("Enter a new password:", "New Password:");
        newPasswordDialog.showAndWait().ifPresent(newPassword -> {
            if (newPassword != null && !newPassword.isEmpty()) {
                // Step 2: Confirm new password
                Dialog<String> confirmPasswordDialog = createPasswordDialog("Confirm your new password:", "Confirm Password:");
                confirmPasswordDialog.showAndWait().ifPresent(confirmPassword -> {
                    if (newPassword.equals(confirmPassword)) {
                        try {
                            teacherDAO.changePassword(currentTeacher.getTeacherId(), newPassword);
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
