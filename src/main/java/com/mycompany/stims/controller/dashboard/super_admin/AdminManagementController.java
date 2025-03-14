package com.mycompany.stims.controller.dashboard.super_admin;

import com.mycompany.stims.database.AdminDAO;
import com.mycompany.stims.model.Admin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Insets;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The AdminManagementController class handles the management of admin accounts.
 * It allows the super admin to add, edit, delete, and view admin details.
 */
public class AdminManagementController {

    @FXML
    private TextField firstNameField; // TextField for entering first name
    @FXML
    private TextField middleNameField; // TextField for entering middle name
    @FXML
    private TextField lastNameField; // TextField for entering last name
    @FXML
    private ComboBox<String> sexField; // ComboBox for selecting sex
    @FXML
    private DatePicker dateOfBirthField; // DatePicker for selecting date of birth
    @FXML
    private TextField emailField; // TextField for entering email
    @FXML
    private TextField phoneNumberField; // TextField for entering phone number
    @FXML
    private TextField addressField; // TextField for entering address
    @FXML
    private ComboBox<String> roleField; // ComboBox for selecting role

    @FXML
    private TableView<Admin> adminTable; // Table to display admins
    @FXML
    private TableColumn<Admin, Integer> idColumn; // Column for admin ID
    @FXML
    private TableColumn<Admin, String> firstNameColumn; // Column for first name
    @FXML
    private TableColumn<Admin, String> middleNameColumn; // Column for middle name
    @FXML
    private TableColumn<Admin, String> lastNameColumn; // Column for last name
    @FXML
    private TableColumn<Admin, String> sexColumn; // Column for sex
    @FXML
    private TableColumn<Admin, Date> dateOfBirthColumn; // Column for date of birth
    @FXML
    private TableColumn<Admin, String> emailColumn; // Column for email
    @FXML
    private TableColumn<Admin, String> phoneNumberColumn; // Column for phone number
    @FXML
    private TableColumn<Admin, String> addressColumn; // Column for address
    @FXML
    private TableColumn<Admin, Integer> roleColumn; // Column for role

    private AdminDAO adminDAO; // DAO for admin-related database operations
    private ObservableList<Admin> adminList; // List of admins

    private final Map<String, String> sexMap = Map.of(
            "Male", "M",
            "Female", "F"
    ); // Map for sex options

    private final Map<String, Integer> roleMap = Map.of(
            "Super Admin", 1,
            "Admin", 2
    ); // Map for role options

    private static final String TEMP_PASSWORD_FILE = "temporary_passwords_admins.txt"; // File to store temporary passwords

    /**
     * Initializes the controller class. Sets up the table columns, ComboBoxes,
     * and loads admin data.
     */
    @FXML
    public void initialize() {
        Connection connection = com.mycompany.stims.database.DatabaseConnection.getConnection();
        adminDAO = new AdminDAO(connection);

        // Initialize ComboBoxes
        sexField.setItems(FXCollections.observableArrayList(sexMap.keySet()));
        roleField.setItems(FXCollections.observableArrayList(roleMap.keySet()));

        // Initialize TableView columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("adminId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        middleNameColumn.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        sexColumn.setCellValueFactory(new PropertyValueFactory<>("sex"));
        dateOfBirthColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("roleId"));

        // Custom cell factories for sex and role columns
        sexColumn.setCellFactory(column -> new TableCell<Admin, String>() {
            @Override
            protected void updateItem(String sex, boolean empty) {
                super.updateItem(sex, empty);
                if (empty || sex == null) {
                    setText(null);
                } else {
                    setText(sex.equals("M") ? "Male" : "Female");
                }
            }
        });

        roleColumn.setCellFactory(column -> new TableCell<Admin, Integer>() {
            @Override
            protected void updateItem(Integer roleId, boolean empty) {
                super.updateItem(roleId, empty);
                if (empty || roleId == null) {
                    setText(null);
                } else {
                    setText(roleId == 1 ? "Super Admin" : "Admin");
                }
            }
        });

        // Load admin data
        loadAdmins();
    }

    /**
     * Loads all admins from the database and populates the table.
     */
    private void loadAdmins() {
        adminList = FXCollections.observableArrayList();
        List<Admin> admins = adminDAO.getAllAdmins();
        adminList.addAll(admins);
        adminTable.setItems(adminList);
    }

    /**
     * Handles the action when the add admin button is clicked. Validates the
     * input and adds a new admin to the database.
     */
    @FXML
    private void handleAddAdmin() {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()
                || emailField.getText().isEmpty() || phoneNumberField.getText().isEmpty()
                || addressField.getText().isEmpty() || dateOfBirthField.getValue() == null
                || sexField.getValue() == null || roleField.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "All fields are required!");
            return;
        }

        String sex = sexMap.get(sexField.getValue());
        int roleId = roleMap.get(roleField.getValue());

        Admin admin = new Admin(
                firstNameField.getText(),
                middleNameField.getText(),
                lastNameField.getText(),
                sex,
                Date.from(dateOfBirthField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                emailField.getText(),
                phoneNumberField.getText(),
                addressField.getText(),
                roleId
        );

        try {
            String temporaryPassword = adminDAO.addAdmin(admin);
            saveTemporaryPasswordToFile(admin.getAdminIdNo(), temporaryPassword);
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Admin added successfully!\nTemporary Password: " + temporaryPassword);
            loadAdmins();
            clearFields();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add admin: " + e.getMessage());
        }
    }

    /**
     * Handles the action when a row in the table is double-clicked. Displays
     * the admin profile dialog.
     *
     * @param event the mouse event
     */
    @FXML
    private void handleRowClick(MouseEvent event) {
        if (event.getClickCount() == 2) { // Check for double-click
            Admin selectedAdmin = adminTable.getSelectionModel().getSelectedItem();
            if (selectedAdmin != null) {
                showAdminProfileDialog(selectedAdmin);
            }
        }
    }

    /**
     * Displays a dialog with the details of the selected admin.
     *
     * @param admin the admin whose details are to be displayed
     */
    private void showAdminProfileDialog(Admin admin) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Admin Profile");
        dialog.setHeaderText("Admin Details");

        ButtonType editButtonType = new ButtonType("Edit", ButtonData.OK_DONE);
        ButtonType deleteButtonType = new ButtonType("Delete", ButtonData.OK_DONE);
        ButtonType closeButtonType = new ButtonType("Close", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(editButtonType, deleteButtonType, closeButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextArea profileText = new TextArea();
        profileText.setEditable(false);
        profileText.setText(
                "ID: " + admin.getAdminIdNo() + "\n"
                + "First Name: " + admin.getFirstName() + "\n"
                + "Middle Name: " + admin.getMiddleName() + "\n"
                + "Last Name: " + admin.getLastName() + "\n"
                + "Sex: " + (admin.getSex().equals("M") ? "Male" : "Female") + "\n"
                + "Date of Birth: " + admin.getDateOfBirth() + "\n"
                + "Email: " + admin.getEmail() + "\n"
                + "Phone Number: " + admin.getPhoneNumber() + "\n"
                + "Address: " + admin.getAddress() + "\n"
                + "Role: " + (admin.getRoleId() == 1 ? "Super Admin" : "Admin")
        );
        grid.add(profileText, 0, 0);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == editButtonType) {
            handleEditAdmin(admin);
        } else if (result.isPresent() && result.get() == deleteButtonType) {
            handleDeleteAdmin(admin);
        }
    }

    /**
     * Handles the action when the edit button is clicked. Displays a dialog to
     * edit the selected admin's details.
     *
     * @param admin the admin to be edited
     */
    private void handleEditAdmin(Admin admin) {
        // Create a custom dialog for editing
        Dialog<ButtonType> editDialog = new Dialog<>();
        editDialog.setTitle("Edit Admin");
        editDialog.setHeaderText("Edit Admin Information");

        // Set the dialog content
        DialogPane editDialogPane = editDialog.getDialogPane();
        editDialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create a GridPane for the form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Add form fields
        TextField firstNameField = new TextField(admin.getFirstName());
        TextField middleNameField = new TextField(admin.getMiddleName());
        TextField lastNameField = new TextField(admin.getLastName());
        ComboBox<String> sexField = new ComboBox<>(FXCollections.observableArrayList(sexMap.keySet()));
        sexField.setValue(admin.getSex().equals("M") ? "Male" : "Female");

        java.util.Date utilDate = new java.util.Date(admin.getDateOfBirth().getTime());
        LocalDate localDate = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DatePicker dateOfBirthField = new DatePicker(localDate);

        TextField emailField = new TextField(admin.getEmail());
        TextField phoneNumberField = new TextField(admin.getPhoneNumber());
        TextField addressField = new TextField(admin.getAddress());
        ComboBox<String> roleField = new ComboBox<>(FXCollections.observableArrayList(roleMap.keySet()));
        roleField.setValue(admin.getRoleId() == 1 ? "Super Admin" : "Admin");

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Middle Name:"), 0, 1);
        grid.add(middleNameField, 1, 1);
        grid.add(new Label("Last Name:"), 0, 2);
        grid.add(lastNameField, 1, 2);
        grid.add(new Label("Sex:"), 0, 3);
        grid.add(sexField, 1, 3);
        grid.add(new Label("Date of Birth:"), 0, 4);
        grid.add(dateOfBirthField, 1, 4);
        grid.add(new Label("Email:"), 0, 5);
        grid.add(emailField, 1, 5);
        grid.add(new Label("Phone Number:"), 0, 6);
        grid.add(phoneNumberField, 1, 6);
        grid.add(new Label("Address:"), 0, 7);
        grid.add(addressField, 1, 7);
        grid.add(new Label("Role:"), 0, 8);
        grid.add(roleField, 1, 8);

        editDialogPane.setContent(grid);

        // Show the dialog and wait for user input
        editDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationAlert.setTitle("Confirm Update");
                confirmationAlert.setHeaderText("Are you sure you want to update this admin?");
                confirmationAlert.setContentText("This action cannot be undone.");

                Optional<ButtonType> confirmationResult = confirmationAlert.showAndWait();
                if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {
                    // Update the admin object with the new values
                    admin.setFirstName(firstNameField.getText().trim());
                    admin.setMiddleName(middleNameField.getText().trim());
                    admin.setLastName(lastNameField.getText().trim());
                    admin.setSex(sexMap.get(sexField.getValue()));
                    admin.setDateOfBirth(java.sql.Date.valueOf(dateOfBirthField.getValue()));
                    admin.setEmail(emailField.getText().trim());
                    admin.setPhoneNumber(phoneNumberField.getText().trim());
                    admin.setAddress(addressField.getText().trim());
                    admin.setRoleId(roleMap.get(roleField.getValue()));

                    // Save the updated admin to the database
                    try {
                        adminDAO.updateAdmin(admin);
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Admin details updated successfully.");
                        loadAdmins(); // Refresh the table
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to update admin: " + e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * Handles the action when the delete button is clicked. Deletes the
     * selected admin from the database.
     *
     * @param admin the admin to be deleted
     */
    private void handleDeleteAdmin(Admin admin) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Delete");
        confirmationAlert.setHeaderText("Are you sure you want to delete this admin?");
        confirmationAlert.setContentText("This action cannot be undone.");

        Optional<ButtonType> confirmationResult = confirmationAlert.showAndWait();
        if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {
            try {
                adminDAO.deleteAdmin(admin.getAdminIdNo());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Admin deleted successfully.");
                loadAdmins();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete admin: " + e.getMessage());
            }
        }
    }

    /**
     * Saves the temporary password to a file.
     *
     * @param adminIdNo the admin ID number
     * @param temporaryPassword the temporary password
     */
    private void saveTemporaryPasswordToFile(String adminIdNo, String temporaryPassword) {
        Path path = Paths.get(TEMP_PASSWORD_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write("Admin ID: " + adminIdNo + ", Temporary Password: " + temporaryPassword);
            writer.newLine();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "File Error", "Failed to save temporary password to file: " + e.getMessage());
        }
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

    /**
     * Clears all input fields.
     */
    private void clearFields() {
        firstNameField.clear();
        middleNameField.clear();
        lastNameField.clear();
        sexField.getSelectionModel().clearSelection();
        dateOfBirthField.setValue(null);
        emailField.clear();
        phoneNumberField.clear();
        addressField.clear();
        roleField.getSelectionModel().clearSelection();
    }
}
