package com.mycompany.stims.controller.dashboard.student.academic_records;

import com.mycompany.stims.database.DatabaseConnection;
import com.mycompany.stims.utils.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;

/**
 * The `ViewReportsController` class manages the functionality for viewing
 * academic reports in the student dashboard. It allows students to view their
 * latest academic report in PDF format.
 */
public class ViewReportsController implements Initializable {

    @FXML
    private StackPane dynamicContentContainer;

    @FXML
    private VBox pdfPagesContainer;

    @FXML
    private Button backButton;

    /**
     * Initializes the controller after its root element has been completely
     * processed. This method fetches the logged-in student's ID, retrieves the
     * latest PDF report from the database, and renders the PDF pages in the UI.
     *
     * @param url The location used to resolve relative paths for the root
     * object, or `null` if the location is not known.
     * @param rb The resources used to localize the root object, or `null` if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Get the logged-in student's email or username
        String loggedInStudentEmailOrUsername = Session.getLoggedInStudentEmailOrUsername();

        if (loggedInStudentEmailOrUsername == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No student is currently logged in.");
            return;
        }

        int studentId = getStudentIdFromDatabase(loggedInStudentEmailOrUsername);

        if (studentId == -1) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch student ID from the database.");
            return;
        }

        // Fetch the PDF content from the database for the logged-in student
        byte[] pdfContent = getPdfContentFromDatabase(studentId);

        if (pdfContent == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch PDF content from the database.");
            return;
        }

        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfContent))) {
            // Create a PDFRenderer to render the PDF pages
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            // Render each page and add it to the VBox
            for (int i = 0; i < document.getNumberOfPages(); i++) {

                BufferedImage bufferedImage = pdfRenderer.renderImage(i, 3f); // 2.0f = 200% scaling

                // Convert BufferedImage to JavaFX Image
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);

                // Create an ImageView to display the image
                ImageView imageView = new ImageView(image);
                imageView.setPreserveRatio(true); // Preserve the aspect ratio
                imageView.setFitWidth(870); // Set the width of the image 

                // Add the ImageView to the VBox
                pdfPagesContainer.getChildren().add(imageView);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to render PDF: " + e.getMessage());
        }
    }

    /**
     * Handles the "Back" button action. Navigates back to the Academic Records
     * screen.
     */
    @FXML
    private void handleBackButton() {
        try {
            // Load the Academic Records FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/stims/fxml/dashboard/student/academic_records/Academic_Records.fxml"));
            Parent academicRecordsPane = loader.load();

            // Clear the existing content and add the new content
            dynamicContentContainer.getChildren().clear();
            dynamicContentContainer.getChildren().add(academicRecordsPane);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load Academic Records screen: " + e.getMessage());
        }
    }

    /**
     * Fetches the student ID from the database using the logged-in email or
     * username.
     *
     * @param emailOrUsername The email or username of the logged-in student.
     * @return The student ID, or -1 if not found.
     */
    private int getStudentIdFromDatabase(String emailOrUsername) {
        String query = "SELECT student_id FROM student WHERE email = ? OR username = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, emailOrUsername);
            stmt.setString(2, emailOrUsername);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("student_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to fetch student ID: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Fetches the PDF content from the database for a specific student.
     *
     * @param studentId The ID of the student.
     * @return The PDF content as a byte array, or null if not found.
     */
    private byte[] getPdfContentFromDatabase(int studentId) {
        String query = "SELECT report_content FROM student_reports WHERE student_id = ? ORDER BY report_id DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("report_content");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to fetch PDF content: " + e.getMessage());
        }
        return null;
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
