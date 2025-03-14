
# STIMS - Student Information Management System

## Project Overview
**STIMS (Student Information Management System)** is a JavaFX-based application designed to manage student records, course enrollments, and academic reports. It integrates with a MySQL database (`STIMS_DBA`) using JDBC. The system provides a user-friendly interface for administrators, teachers, and students to manage academic data efficiently.

  ## Features
  ### 1. **User Roles and Authentication**
  - **Super Admin**: Manage administrators and system-wide settings.
  - **Admin**: Manage students, courses, teachers, and generate reports.
  - **Teacher**: Manage assigned courses, view enrolled students, and submit grades.
  - **Student**: View enrolled courses, grades, and academic records.

  ### 2. **Student Management**
  - Register, update, and view student profiles.
  - Manage student academic records and course enrollments.
  - Generate temporary passwords for new students.

  ### 3. **Course Management**
  - Add, edit, and delete courses and course offerings.
  - Enroll students in courses and manage course assignments for teachers.
  - Generate unique course codes automatically.

  ### 4. **Academic Records**
  - Calculate and store grades, Semester Grade Point Average (SGPA), and Cumulative Grade Point Average (CGPA).
  - Generate academic transcripts and semester report cards.

  ### 5. **Reporting System**
  - Generate reports for students, courses, and overall academic performance.
  - Export reports in various formats (e.g., PDF, Excel).

  ### 6. **User-Friendly Interface**
  - Intuitive dashboards for each user role.
  - Responsive design with CSS styling.

  

## Installation & Setup
  ### 1. **Prerequisites**
  Ensure you have the following installed:
  - Java JDK 17+
  - JavaFX SDK
  - MySQL Server (Ensure `STIMS_DBA` database is created)
  - JDBC Driver for MySQL
  - NetBeans/IntelliJ IDEA (or any preferred IDE)

### 2. **Database Setup**
  1. Open MySQL and create the database:
  
      CREATE DATABASE STIMS_DBA;
      
  2. Import the SQL schema (if available):

      mysql -u root -p STIMS_DBA < database/schema.sql
    
  3. Update the database connection settings in `src/main/resources/config.properties`.

### 3. **Configuration**
  1. Copy the `config.properties.template` file to `config.properties`:
  
    cp src/main/resources/config.properties.template src/main/resources/config.properties
    
  2. Open `config.properties` and update the following fields:
    - **Database Configuration**:
      - `db.url`: Set the MySQL database URL (e.g., `jdbc:mysql://localhost:3306/STIMS_DBA`).
      - `db.username`: Set your MySQL username.
      - `db.password`: Set your MySQL password.


### 4. **Creating the Super Admin**
    To create the first Super Admin, follow these steps:

    1. Open the `STIMS.java` file located in the `src/main/java/com/mycompany/stims/` directory.
    2. Modify the `main` method to create a new Super Admin. Here is an example:

      ```java
      package com.mycompany.stims;
      import com.mycompany.stims.database.DatabaseConnection;
      import com.mycompany.stims.database.AdminDAO;
      import com.mycompany.stims.model.Admin;
      import java.sql.Connection;
      import java.util.Date;

      public class STIMS {
          public static void main(String[] args) {
              // Get a database connection
              Connection connection = DatabaseConnection.getConnection();

              if (connection != null) {
                  System.out.println("Database connection established.");

                  // Create an instance of AdminDAO
                  AdminDAO adminDAO = new AdminDAO(connection);

                  // Create a new admin 
                  Admin admin = new Admin();
                  admin.setFirstName("FirstName"); // Example: "FirstName"
                  admin.setMiddleName("MiddleName"); // Example: "MiddleName"
                  admin.setLastName("LastName"); // Example: "LastName"
                  admin.setSex("M");  // Sex M - Male , F - Female
                  admin.setDateOfBirth(new Date()); // Example: Current date
                  admin.setEmail("email@example.com"); // Example email
                  admin.setPhoneNumber("0987654321"); // Example phone number
                  admin.setAddress("Addis Ababa, Ethiopia"); // Example Address
                  admin.setRoleId(1); /*  +---------+-------------+
                                          | role_id | role_name   | 
                                          +---------+-------------+
                                          |       1 | Super Admin |
                                          |       2 | Admin       | 
                                          +---------+-------------+
                                                                      */

                  // Add the admin to the database
                  adminDAO.addAdmin(admin);

              } else {
                  System.err.println("Database connection failed.");
              }
          }
      }
      ```

3. Run the `STIMS.java` file. If the database connection is successful, you will see alog message like the following :

        Database connection established successfully.
        Database connection established.
        Admin added successfully with AdminIdNo: ID/ADM/001
        Temporary Password: bDniDUV0DC$h
        ----------------------------------------------------------------------
   

4. The Super Admin account is now created. The temporary password is saved in `temporary_password_admin.txt`.

### 5. **Running the Application**
1. Clone the repository:
   
   git clone https://github.com/dagmawigezahegn/STIMS.git
   
2. Open the project in your preferred IDE (NetBeans/IntelliJ).
3. Build and run the application.


## Project Structure

        STIMS/
        │── nbactions.xml
        │── pom.xml
        │── README.md
        │   
        +---database/                 
        │       schema.sql             # SQL file containing the database schema
        │     
        +---docs/                     
        │       SYSTEM_OVERVIEW.md     # Explains how students, courses, and records are managed
        │       DATABASE_SCHEMA.md     # Contains MySQL table structures and relationships
        │       CODE_STRUCTURE.md      # Describes the Java package hierarchy
        │       USER_GUIDE.md          # Step-by-step guide for using the application
        │       
        │
        +---generated_reports/         # Folder for storing generated reports
        +---src/
        │   +---main/
        │   │   +---java/
        │   │   │   +---com/
        │   │   │       +---mycompany/
        │   │   │           +---stims/
        │   │   │               ¦   App.java                  # Main application entry point
        │   │   │               ¦   STIMS.java                # Application class
        │   │   │               +---controller/               # Controllers for FXML views
        │   │   │               +---database/                 # Database access objects (DAOs)
        │   │   │               +---model/                    # Data models (e.g., Student, Course)
        │   │   │               +---report/                   # Report generation classes
        │   │   │               +---utils/                    # Utility classes (e.g., Config, PasswordUtils)
        │   │   +---resources/
        │   │       ¦   config.properties.template            # Template configuration file for database connection and file path        
        │   │       +---com/
        │   │           +---mycompany/
        │   │               +---stims/
        │   │                   +---css/                      # CSS files for styling
        │   │                   +---fxml/                     # FXML files for UI views
        │   │                   +---img/                      # Images used in the application
        │   
        +---templates/                 # Templates for report generation (e.g., Excel files)
                report_card_template.xlsx
                transcript_template.xlsx


## Documentation
The project includes the following documentation files:
- **`SYSTEM_OVERVIEW.md`**: Explains how students, courses, and records are managed.
- **`DATABASE_SCHEMA.md`**: Contains MySQL table structures and relationships.
- **`CODE_STRUCTURE.md`**: Describes the Java package hierarchy.
- **`USER_GUIDE.md`**: Step-by-step guide for using the application.



## User Roles and Functionalities

### 1. **Super Admin**
- **Tasks**:
  - Create, Read, Update, and Delete (CRUD) admin accounts.
  - Oversee system-wide configurations.
- **UI Features**:
  - **Admin Management**: Register new admins, view/edit/delete existing admins.
  - **Profile & Settings**: Update profile, change username, and password.
  - **Logout**: Log out of the system.



### 2. **Admin**
- **Tasks**:
  - **Student Management**: Register, update, and delete students. Manage academic records and enrollments.
  - **Teacher Management**: Register, update, and delete teachers. Assign courses to teachers.
  - **Course Management**: Add, update, and delete courses. Manage course offerings and enrollments.
  - **Report Management**: Generate student reports, course reports, semester report cards, and academic transcripts.
- **UI Features**:
  - **Student Management**: Register students, edit registrations, manage academic records, and view student profiles.
  - **Teacher Management**: Add teachers, edit teacher details, assign courses, and view teacher profiles.
  - **Course Management**: Manage courses, course offerings, and student enrollments.
  - **Report Management**: Generate and export reports in PDF and Excel formats.
  - **Profile & Settings**: Update profile, change username, and password.
  - **Logout**: Log out of the system.



### 3. **Teacher**
- **Tasks**:
  - View assigned courses and enrolled students.
  - Manage student grades for assigned courses.
- **UI Features**:
  - **Assigned Courses**: View courses assigned by the admin.
  - **Enrolled Students**: View students enrolled in assigned courses.
  - **Manage Grades**: Enter and update student grades.
  - **Profile & Settings**: Update profile, change username, and password.
  - **Logout**: Log out of the system.



### 4. **Student**
- **Tasks**:
  - View enrolled courses and grades.
  - View academic records and reports.
- **UI Features**:
  - **My Courses**: View enrolled courses.
  - **Grades**: View grades for enrolled courses.
  - **Academic Records**: View academic records and generated reports.
  - **Profile & Settings**: Update profile, change username, and password.
  - **Logout**: Log out of the system.


## License
This project is licensed under the **MIT License**.


## Contact
For any inquiries, reach out to **dagimg8@gmail.com**.


## Built With
- **JavaFX**
- **MySQL**
- **JDBC**


## Conclusion
STIMS is a robust and scalable system designed to streamline the management of student, teacher, and course-related data. Its modular architecture, secure authentication, and comprehensive reporting features make it an ideal solution for educational institutions.

