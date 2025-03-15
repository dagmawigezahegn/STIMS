---

# Code Structure

## 📂 Overview

The **STIMS (Student Information Management System)** project is organized into a **modular structure**, with a clear separation of concerns. The codebase is divided into packages based on functionality, such as **controllers**, **models**, **database access objects (DAOs)**, **utilities**, and **reports**. Below is a detailed breakdown of the project structure.

---

## 🗂️ Package Hierarchy

### 1. **Root Package: `com.mycompany.stims`**
**Description**: Contains the main application entry point and core application class.

**Files**:
- `App.java`: The main entry point for the JavaFX application.
- `STIMS.java`: The core application class that initializes and runs the system.

---

### 2. **`controller` Package**
**Description**: Contains all the controllers for the FXML views. Controllers handle user interactions and business logic for the UI.

**Subpackages**:

#### **`dashboard`**
- **`admin`**: Controllers for the admin dashboard and its features.
  - `Admin_DashboardController.java`: Main controller for the admin dashboard.
  - **`course_management`**: Controllers for managing courses and enrollments.
    - `CourseManagementController.java`: Manages course-related operations.
    - `CourseOfferingsController.java`: Handles course offerings.
    - `CoursesController.java`: Manages individual courses.
    - `EnrollStudentsController.java`: Handles student enrollments.
  - **`home`**: Controllers for the admin home screen.
    - `HomeController.java`: Handles the home view.
  - **`profile`**: Controllers for admin profile management.
    - `ProfileController.java`: Manages admin profile updates.
  - **`report_management`**: Controllers for generating and managing reports.
    - `AcademicTranscriptController.java`: Generates academic transcripts.
    - `CourseReportController.java`: Generates course reports.
    - `Report_ManagementController.java`: Manages report generation.
    - `SemesterReportCardController.java`: Generates semester report cards.
    - `StudentReportController.java`: Generates student reports.
  - **`student_management`**: Controllers for managing student records.
    - `EditRegistrationController.java`: Edits student registrations.
    - `RegisterStudentController.java`: Registers new students.
    - `StudentAcademicRecordsController.java`: Manages academic records.
    - `Student_ManagementController.java`: Main controller for student management.
    - `ViewStudentProfilesController.java`: Displays student profiles.
  - **`teacher_management`**: Controllers for managing teacher records.
    - `AddTeacherController.java`: Adds new teachers.
    - `AssignCourseToTeacherController.java`: Assigns courses to teachers.
    - `EditTeacherController.java`: Edits teacher records.
    - `TeacherManagementController.java`: Main controller for teacher management.
    - `ViewTeacherProfileController.java`: Displays teacher profiles.

- **`student`**: Controllers for the student dashboard and its features.
  - `Student_DashboardController.java`: Main controller for the student dashboard.
  - **`academic_records`**: Controllers for viewing academic records.
    - `AcademicRecordsController.java`: Displays academic records.
    - `ViewReportsController.java`: Displays reports.
  - **`home`**: Controllers for the student home screen.
    - `HomeController.java`: Handles the home view.
  - **`profile`**: Controllers for student profile management.
    - `ProfileController.java`: Manages student profile updates.
  - **`view_enrolled_courses`**: Controllers for viewing enrolled courses.
    - `View_Enrolled_CoursesController.java`: Displays enrolled courses.
  - **`view_grades`**: Controllers for viewing grades.
    - `View_GradesController.java`: Displays student grades.

- **`super_admin`**: Controllers for the super admin dashboard.
  - `AdminManagementController.java`: Manages admin accounts.
  - `HomeController.java`: Handles the home view.
  - `ProfileController.java`: Manages super admin profile updates.
  - `SuperAdminDashboardController.java`: Main controller for the super admin dashboard.

- **`teacher`**: Controllers for the teacher dashboard and its features.
  - `Teacher_DashboardController.java`: Main controller for the teacher dashboard.
  - **`assigned_courses`**: Controllers for viewing assigned courses.
    - `AssignedCoursesController.java`: Displays assigned courses.
    - `ViewEnrolledStudentsController.java`: Displays enrolled students.
  - **`home`**: Controllers for the teacher home screen.
    - `HomeController.java`: Handles the home view.
  - **`manage_grades`**: Controllers for managing grades.
    - `ManageGradesController.java`: Manages student grades.
  - **`profile`**: Controllers for teacher profile management.
    - `ProfileController.java`: Manages teacher profile updates.

---

### 3. **`database` Package**
**Description**: Contains database access objects (DAOs) for interacting with the MySQL database.

**Files**:
- `AdminDAO.java`: Handles admin-related database operations.
- `CourseDAO.java`: Manages course-related database operations.
- `CourseOfferingDAO.java`: Handles course offering-related database operations.
- `DatabaseConnection.java`: Manages the database connection.
- `GradeDAO.java`: Manages grade-related database operations.
- `RoleDAO.java`: Handles role-related database operations.
- `StudentAcademicRecordsDAO.java`: Manages academic record-related database operations.
- `StudentCourseDAO.java`: Handles student-course relationship database operations.
- `StudentDAO.java`: Manages student-related database operations.
- `TeacherCourseDAO.java`: Handles teacher-course relationship database operations.
- `TeacherDAO.java`: Manages teacher-related database operations.

---

### 4. **`model` Package**
**Description**: Contains data models representing entities in the system.

**Files**:
- `Admin.java`: Represents an admin user.
- `Course.java`: Represents a course.
- `CourseOffering.java`: Represents a course offering.
- `Grade.java`: Represents a grade.
- `Role.java`: Represents a user role.
- `Student.java`: Represents a student.
- `StudentAcademicRecord.java`: Represents a student's academic record.
- `StudentCourse.java`: Represents the relationship between a student and a course.
- `Teacher.java`: Represents a teacher.
- `TeacherCourse.java`: Represents the relationship between a teacher and a course.

---

### 5. **`report` Package**
**Description**: Contains classes for generating reports.

**Files**:
- `CourseReport.java`: Generates course-related reports.
- `ReportCardGenerator.java`: Generates report cards.
- `StudentReport.java`: Generates student-related reports.
- `Transcript_Generator.java`: Generates academic transcripts.

---

### 6. **`utils` Package**
**Description**: Contains utility classes for common functionality.

**Files**:
- `Config.java`: Manages configuration settings (e.g., database connection).
- `FilePathUtils.java`: Provides utilities for file path handling.
- `PasswordUtils.java`: Provides utilities for password hashing and validation.
- `Session.java`: Manages user session data.

---

### 7. **`resources` Folder**
**Description**: Contains non-code resources such as configuration files, FXML views, CSS styles, and images.

**Files**:
- `config.properties.template`: Template configuration file for database connection settings.
- **`css`**: Contains CSS files for styling the UI.
  - `dashboardDesign.css`: Styles for the dashboard.
- **`fxml`**: Contains FXML files for defining the UI views.
  - `Login.fxml`: Login view.
  - **`dashboard`**: Contains FXML files for dashboard views.
    - **`admin`**: Admin dashboard views.
    - **`student`**: Student dashboard views.
    - **`super_admin`**: Super admin dashboard views.
    - **`teacher`**: Teacher dashboard views.
- **`img`**: Contains images used in the application.
  - `cover.png`, `logo.png`, etc.

---

### 8. **`templates` Folder**
**Description**: Contains templates for report generation (e.g., Excel files).

**Files**:
- `report_card_template.xlsx`: Template for generating report cards.
- `transcript_template.xlsx`: Template for generating academic transcripts.

---

### 9. **`generated_reports` Folder**
**Description**: Stores reports generated by the application (e.g., PDFs, Excel files).

---

### 10. **`database` Folder (Root Level)**
**Description**: Contains SQL scripts for database setup.

**Files**:
- `schema.sql`: SQL script for creating the database schema.

---

### 11. **`docs` Folder (Root Level)**
**Description**: Contains project documentation.

**Files**:
- `SYSTEM_OVERVIEW.md`: Explains the system's functionality and features.
- `DATABASE_SCHEMA.md`: Describes the database schema and relationships.
- `CODE_STRUCTURE.md`: Describes the code structure and package hierarchy.
- `USER_GUIDE.md`: Provides a step-by-step guide for using the application.

---

## 🎯 Conclusion

This structure ensures a **clean separation of concerns**, making the codebase **modular**, **maintainable**, and **scalable**. Each package and folder has a specific purpose, and the documentation provides clear guidance for developers working on the project.

---

