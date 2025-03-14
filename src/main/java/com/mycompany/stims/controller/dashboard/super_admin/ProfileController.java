package com.mycompany.stims.controller.dashboard.super_admin;

import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.database.AdminDAO;
import com.mycompany.stims.model.Admin;
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
 * The `ProfileController` class manages the functionality for viewing and
 * updating the profile of a super admin in the admin dashboard. It allows the
 * super admin to view their profile, change their username, and update their
 * password.
 */
public class ProfileController implements Initializable {

    @FXML
    private Button viewProfileButton;

    @FXML
    private Button changeUsernameButton;

    @FXML
    private Button changePasswordButton;

    private AdminDAO adminDAO;
    private Admin currentAdmin;

    /**
     * Initializes the controller after its root element has been completely
     * processed. This method sets up the database connection, loads the admin
     * profile, and configures button actions.
     *
     * @param url The location used to resolve relative paths for the root
     * object, or `null` if the location is not known.
     * @param rb The resources used to localize the root object, or `null` if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            adminDAO = new AdminDAO(connection);
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Unable to connect to the database.");
        }

        loadAdminProfile();

        viewProfileButton.setOnAction(event -> handleViewProfile());
        changeUsernameButton.setOnAction(event -> handleChangeUsername());
        changePasswordButton.setOnAction(event -> handleChangePassword());
    }

    /**
     * Loads the profile of the currently logged-in admin using the session
     * data. If no admin is logged in, an error alert is shown.
     */
    private void loadAdminProfile() {
        String loggedInEmailOrUsername = Session.getLoggedInAdminEmailOrUsername();

        if (loggedInEmailOrUsername == null || loggedInEmailOrUsername.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "No admin is currently logged in.");
            return;
        }

        currentAdmin = adminDAO.getAdminByEmailOrUsername(loggedInEmailOrUsername);

        if (currentAdmin == null) {
            showAlert(Alert.AlertType.WARNING, "Not Found", "No admin found with the given email/username.");
        }
    }

    /**
     * Handles the "View Profile" button action. Displays a dialog with the
     * admin's profile information.
     */
    private void handleViewProfile() {
        if (currentAdmin == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No admin profile loaded.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Admin Profile");
        dialog.setHeaderText("View Admin Information");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().add(ButtonType.CLOSE);

        dialogPane.setPrefSize(500, 400);

        // Create a GridPane for the form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Add form fields
        addLabelAndValue(grid, "Admin ID:", currentAdmin.getAdminIdNo(), 0);
        addLabelAndValue(grid, "Username:", currentAdmin.getUsername(), 1);
        addLabelAndValue(grid, "First Name:", currentAdmin.getFirstName(), 2);
        addLabelAndValue(grid, "Middle Name:", currentAdmin.getMiddleName(), 3);
        addLabelAndValue(grid, "Last Name:", currentAdmin.getLastName(), 4);
        addLabelAndValue(grid, "Sex:", currentAdmin.getSex(), 5);
        addLabelAndValue(grid, "Date of Birth:", currentAdmin.getDateOfBirth().toString(), 6);
        addLabelAndValue(grid, "Email:", currentAdmin.getEmail(), 7);
        addLabelAndValue(grid, "Phone Number:", currentAdmin.getPhoneNumber(), 8);
        addLabelAndValue(grid, "Address:", currentAdmin.getAddress(), 9);
        addLabelAndValue(grid, "Role:", adminDAO.getRoleNameById(currentAdmin.getRoleId()), 10);

        dialogPane.setContent(grid);

        // Show the dialog
        dialog.showAndWait();
    }

    /**
     * Adds a label and its corresponding value to the specified GridPane at the
     * given row.
     *
     * @param grid The GridPane to which the label and value will be added.
     * @param labelText The text for the label.
     * @param value The value to display.
     * @param row The row index in the GridPane.
     */
    private void addLabelAndValue(GridPane grid, String labelText, String value, int row) {
        grid.add(new Label(labelText), 0, row);
        grid.add(new Label(value), 1, row);
    }

    /**
     * Handles the "Change Username" button action. Displays a dialog to enter a
     * new username and updates the admin's username in the database.
     */
    private void handleChangeUsername() {
        TextInputDialog dialog = new TextInputDialog(currentAdmin.getUsername());
        dialog.setTitle("Change Username");
        dialog.setHeaderText("Enter a new username:");
        dialog.setContentText("Username:");
        dialog.getDialogPane().setPrefSize(400, 200);

        dialog.showAndWait().ifPresent(newUsername -> {
            if (newUsername != null && !newUsername.isEmpty()) {
                currentAdmin.setUsername(newUsername);
                try {
                    adminDAO.updateAdmin(currentAdmin);
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
     * Handles the "Change Password" button action. Displays a dialog to enter
     * and confirm a new password, and updates the admin's password in the
     * database.
     */
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
                            adminDAO.changePassword(currentAdmin.getAdminIdNo(), newPassword);
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
     * Creates a password input dialog with the specified header text and prompt
     * text.
     *
     * @param headerText The header text for the dialog.
     * @param promptText The prompt text for the password field.
     * @return A configured `Dialog` for password input.
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
     * Displays an alert dialog with the specified type, title, and message.
     *
     * @param alertType The type of alert (e.g., ERROR, INFORMATION).
     * @param title The title of the alert.
     * @param message The message to display in the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
