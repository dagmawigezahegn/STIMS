---

# User Guide

## 📖 Introduction

This guide provides a step-by-step walkthrough of the **Student Information Management System (STIMS)**. It covers the functionalities available to each user role: **Super Admin**, **Admin**, **Teacher**, and **Student**. Follow this guide to understand how to navigate the system, perform tasks, and manage academic data efficiently.

---

## 📑 Table of Contents

1. **Getting Started**
   - Prerequisites
   - Database Setup
   - Creating the Super Admin
   - Running the Application

2. **Super Admin Guide**
   - Logging In
   - Admin Management
   - Profile & Settings
   - Logging Out

3. **Admin Guide**
   - Logging In
   - Student Management
   - Teacher Management
   - Course Management
   - Report Management
   - Profile & Settings
   - Logging Out

4. **Teacher Guide**
   - Logging In
   - Assigned Courses
   - Enrolled Students
   - Manage Grades
   - Profile & Settings
   - Logging Out

5. **Student Guide**
   - Logging In
   - My Courses
   - Grades
   - Academic Records
   - Profile & Settings
   - Logging Out

---

## 1. 🚀 Getting Started

### Prerequisites
Ensure you have the following installed:
- Java JDK 17+
- JavaFX SDK
- MySQL Server (Ensure `STIMS_DBA` database is created)
- JDBC Driver for MySQL
- NetBeans/IntelliJ IDEA (or any preferred IDE)

### Database Setup
1. Open MySQL and create the database:
   ```sql
   CREATE DATABASE STIMS_DBA;
   ```
2. Import the SQL schema (if available):
   ```bash
   mysql -u root -p STIMS_DBA < database/schema.sql
   ```
3. Update the database connection settings in `src/main/resources/config.properties`.

### Creating the Super Admin
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
3. Run the `STIMS.java` file. If the database connection is successful, you will see a log message like the following:
   ```
   Database connection established successfully.
   Database connection established.
   Admin added successfully with AdminIdNo: ID/ADM/001
   Temporary Password: bDniDUV0DC$h
   ----------------------------------------------------------------------
   ```
4. The Super Admin account is now created. The temporary password is saved in `temporary_password_admin.txt`.

### Running the Application
1. Clone the repository:
   ```bash
   git clone https://github.com/dagmawigezahegn/STIMS.git
   ```
2. Open the project in your preferred IDE (NetBeans/IntelliJ).
3. Build and run the application.

---

## 2. 👑 Super Admin Guide

### Logging In
1. Launch the STIMS application.
2. On the login screen, enter the email address or username and the temporary password provided during the Super Admin creation.
3. Click the **Login** button to access the Super Admin dashboard.

### Admin Management
1. **Register New Admin**:
   - Navigate to the **Admin Management** section.
   - Fill in the admin details (e.g., first name, last name, email, etc.).
   - Select the role to `Admin` for a regular admin.
   - Click **Register**.
   - A temporary password will be displayed and saved in `temporary_password_admin.txt`.

2. **Edit/Delete Admin**:
   - Double-click an admin from the list to view their details.
   - Use the **Edit** button to update their information or the **Delete** button to remove them.

### Profile & Settings
1. **View Profile**:
   - Navigate to **Profile & Settings**.
   - View your profile details.

2. **Change Username/Password**:
   - Use the **Change Username** or **Change Password** buttons to update your credentials.

### Logging Out
1. Click the **Logout** button to exit the system and return to the login screen.

---

## 3. 👨‍💼 Admin Guide

### Logging In
1. Launch the STIMS application.
2. On the login screen, enter the email address or username and the temporary password provided during the Admin creation.
3. Click the **Login** button to access the Admin dashboard.

### Student Management
1. **Register Student**:
   - Navigate to **Student Management > Register Student**.
   - Fill in the student details (e.g., name, date of birth, program, etc.).
   - Click **Register**.
   - A temporary password will be displayed and saved in `temporary_password_student.txt`.

2. **Edit Registration**:
   - Navigate to **Student Management > Edit Registration**.
   - Search for a student using their `studentId_No`.
   - Update their details and click **Save**.

3. **Student Academic Records**:
   - Navigate to **Student Management > Student Academic Records**.
   - Enter the `studentId_No` and academic details (e.g., academic year, year, semester).
   - Click **Add Record** to calculate SGPA and CGPA.
   - Select the record from the table and Click **Update Academic Records** for recalculating and updating records.

4. **View Student Profiles**:
   - Navigate to **Student Management > View Student Profiles**.
   - Search for students by `studentId_No` or name.

### Teacher Management
1. **Add Teacher**:
   - Navigate to **Teacher Management > Add Teacher**.
   - Fill in the teacher details (e.g., name, email, department).
   - Click **Register**.
   - A temporary password will be displayed and saved in `temporary_password_teacher.txt`.

2. **Edit Teacher**:
   - Navigate to **Teacher Management > Edit Teacher**.
   - Search for a teacher using their `teacher_id`.
   - Update their details and click **Save**.

3. **Assign Course to Teacher**:
   - Navigate to **Teacher Management > Assign Course to Teacher**.
   - Select a course offering and then assign it to a teacher by entering a `teacher_id`.
   - Click **Add Assignment**.

4. **View Teacher Profiles**:
   - Navigate to **Teacher Management > View Teacher Profiles**.
   - Search for teachers by `teacher_id` or name.

### Course Management
1. **Courses**:
   - Navigate to **Course Management > Courses**.
   - Add, update, or delete courses.
   - Filter courses by department.

2. **Course Offerings**:
   - Navigate to **Course Management > Course Offerings**.
   - Create course offerings for specific academic years, year, and semester.

3. **Enroll Students to Courses**:
   - Navigate to **Course Management > Enroll Students to Courses**.
   - Enroll students in course offerings by entering their `studentId_No` and `offering_id`.

### Report Management
1. **Student Report**:
   - Navigate to **Report Management > Student Report**.
   - Generate a PDF report for a specific student or all students.

2. **Course Report**:
   - Navigate to **Report Management > Course Report**.
   - Generate a PDF report for a specific course, all courses, and course statistics.

3. **Semester Report Card**:
   - Navigate to **Report Management > Semester Report Card**.
   - Generate an Excel report card for a student for a specific semester by entering the academic year, year, and semester.

4. **Academic Transcript**:
   - Navigate to **Report Management > Academic Transcript**.
   - Generate an Excel transcript for a student.

### Profile & Settings
1. **View Profile**:
   - Navigate to **Profile & Settings**.
   - View your profile details.

2. **Change Username/Password**:
   - Use the **Change Username** or **Change Password** buttons to update your credentials.

### Logging Out
1. Click the **Logout** button to exit the system and return to the login screen.

---

## 4. 👩‍🏫 Teacher Guide

### Logging In
1. Launch the STIMS application.
2. On the login screen, enter the email address or username and the temporary password provided during the Teacher creation.
3. Click the **Login** button to access the Teacher dashboard.

### Assigned Courses
1. Navigate to **Assigned Courses**.
2. View the list of courses assigned to you.

### Enrolled Students
1. Navigate to **Enrolled Students**.
2. View the list of students enrolled in your assigned courses.

### Manage Grades
1. Navigate to **Manage Grades**.
2. Select a course and enter grades for enrolled students.
3. Click **Submit** to save the grades.

### Profile & Settings
1. **View Profile**:
   - Navigate to **Profile & Settings**.
   - View your profile details.

2. **Change Username/Password**:
   - Use the **Change Username** or **Change Password** buttons to update your credentials.

### Logging Out
1. Click the **Logout** button to exit the system and return to the login screen.

---

## 5. 🎓 Student Guide

### Logging In
1. Launch the STIMS application.
2. On the login screen, enter the email address or username and the temporary password provided during the Student creation.
3. Click the **Login** button to access the Student dashboard.

### My Courses
1. Navigate to **My Courses**.
2. View the list of courses you are enrolled in.

### Grades
1. Navigate to **Grades**.
2. View your grades for enrolled courses.

### Academic Records
1. Navigate to **Academic Records**.
2. View your academic records, including SGPA and CGPA.
3. Click **View Report** to see your academic report.

### Profile & Settings
1. **View Profile**:
   - Navigate to **Profile & Settings**.
   - View your profile details.

2. **Change Username/Password**:
   - Use the **Change Username** or **Change Password** buttons to update your credentials.

### Logging Out
1. Click the **Logout** button to exit the system and return to the login screen.

---

## 🎯 Conclusion

This guide provides a comprehensive overview of how to use the STIMS application for each user role. For further assistance, refer to the **SYSTEM_OVERVIEW.md** and **README.md** files or contact the system administrator.

---
