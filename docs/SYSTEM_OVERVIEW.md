---

# System Overview

## 🌟 Introduction

The **Student Information Management System (STIMS)** is a comprehensive platform designed to manage student, teacher, and course-related data efficiently. It provides a **modular and scalable architecture**, ensuring clear separation of concerns and ease of maintenance. The system is built using **JavaFX** for the user interface and **MySQL** for database management. STIMS is tailored for educational institutions to streamline administrative tasks, academic record management, and reporting.

---

## ✨ Key Features

STIMS offers the following key features:

1. **User Management**:
   - Admin, Teacher, and Student roles with distinct permissions.
   - Secure authentication and password management.
   - Temporary password generation for new users.

2. **Course Management**:
   - Add, update, and delete courses.
   - Manage course offerings and enrollments.
   - Assign courses to teachers.

3. **Student Management**:
   - Register new students with unique student IDs.
   - Manage student academic records, including grades and transcripts.
   - Generate academic reports (e.g., semester report cards, academic transcripts).

4. **Teacher Management**:
   - Add, update, and delete teacher records.
   - Assign courses to teachers and manage their schedules.

5. **Reporting**:
   - Generate course reports, student reports, and academic transcripts.
   - Export reports in various formats (e.g., PDF, Excel).

---

## 🎓 How Students, Courses, and Records are Managed

### 1. **Student Management**
Students are at the core of the STIMS system. The system allows for efficient management of student data, including registration, academic records, and course enrollments.

- **Registration**:
  - New students are registered by admins, who input details such as name, date of birth, email, and program.
  - A unique student ID is generated automatically (e.g., `ID/GRA/25/001`).
  - A temporary password is created and stored in a file (`temporary_password_student.txt`).

- **Academic Records**:
  - Academic records include semester-wise details such as total credits, Semester Grade Point Average (SGPA), and Cumulative Grade Point Average (CGPA).
  - Admins can add, update, or delete academic records for students.
  - The system automatically calculates SGPA and CGPA based on the grades entered by teachers.

- **Course Enrollments**:
  - Students are enrolled in courses by admins.
  - Each enrollment is linked to a specific course offering, which includes details like academic year, year, and semester.
  - Students can view their enrolled courses and grades through their dashboard.

- **Profile Management**:
  - Students can update their profile information, such as email, phone number, and address.
  - They can also change their username and password.

---

### 2. **Course Management**
Courses are the building blocks of the academic system. STIMS provides tools to manage courses, course offerings, and enrollments.

- **Course Creation**:
  - Admins can create new courses by entering details such as course name, description, credits, level, year, semester, and department.
  - A unique course code is generated automatically (e.g., `CS101`).

- **Course Offerings**:
  - Course offerings are created for specific academic years, years, and semesters.
  - Each offering is linked to a course and has a unique offering ID.
  - Admins can manage course offerings and assign teachers to them.

- **Enrollments**:
  - Students are enrolled in course offerings by admins.
  - Each enrollment is recorded with details such as student ID, offering ID, and enrollment date.
  - Teachers can view enrolled students for their assigned courses.

- **Grade Management**:
  - Teachers enter grades for students enrolled in their courses.
  - Grades are stored in the database and used to calculate academic records (e.g., SGPA, CGPA).

---

### 3. **Academic Records Management**
Academic records are a critical part of the system, providing a comprehensive view of a student's academic performance.

- **Semester Records**:
  - Each semester record includes details such as academic year, year, semester, total credits, SGPA, and CGPA.
  - Admins can add, update, or delete semester records for students.
  - The system automatically calculates SGPA and CGPA based on the grades entered by teachers.

- **Report Generation**:
  - Admins can generate various reports, including:
    - **Student Reports**: Detailed reports for individual students.
    - **Course Reports**: Reports for specific courses, including statistics.
    - **Semester Report Cards**: Excel-based report cards for students.
    - **Academic Transcripts**: Comprehensive transcripts of a student's academic history.
  - Reports are exported in PDF or Excel format and stored in the `generated_reports` folder.

- **Data Integrity**:
  - Academic records are linked to student enrollments and grades, ensuring data consistency.
  - The system enforces constraints to prevent invalid data entry (e.g., duplicate enrollments, invalid grades).

---

### 4. **Teacher Management**
Teachers play a vital role in the academic system, and STIMS provides tools to manage their data and assignments.

- **Registration**:
  - New teachers are registered by admins, who input details such as name, email, and department.
  - A unique teacher ID is generated automatically.
  - A temporary password is created and stored in a file (`temporary_password_teacher.txt`).

- **Course Assignments**:
  - Teachers are assigned to course offerings by admins.
  - Each assignment is recorded with details such as teacher ID, offering ID, and assignment date.
  - Teachers can view their assigned courses and enrolled students through their dashboard.

- **Grade Entry**:
  - Teachers enter grades for students enrolled in their courses.
  - Grades are stored in the database and used to calculate academic records (e.g., SGPA, CGPA).

- **Profile Management**:
  - Teachers can update their profile information, such as email, phone number, and address.
  - They can also change their username and password.

---

## 👤 User Roles and Functionalities

### 1. **Super Admin**
The Super Admin has the highest level of access and can perform the following tasks:
- **Tasks**:
  - Create, Read, Update, and Delete (CRUD) admin accounts.
  - Oversee system-wide configurations.
- **UI Features**:
  - **Admin Management**: Register new admins, view/edit/delete existing admins.
  - **Profile & Settings**: Update profile, change username, and password.
  - **Logout**: Log out of the system.

---

### 2. **Admin**
Admins have access to manage students, teachers, courses, and reports. Their tasks include:
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

---

### 3. **Teacher**
Teachers have access to manage their assigned courses and student grades. Their tasks include:
- **Tasks**:
  - View assigned courses and enrolled students.
  - Manage student grades for assigned courses.
- **UI Features**:
  - **Assigned Courses**: View courses assigned by the admin.
  - **Enrolled Students**: View students enrolled in assigned courses.
  - **Manage Grades**: Enter and update student grades.
  - **Profile & Settings**: Update profile, change username, and password.
  - **Logout**: Log out of the system.

---

### 4. **Student**
Students have access to view their academic information. Their tasks include:
- **Tasks**:
  - View enrolled courses and grades.
  - View academic records and reports.
- **UI Features**:
  - **My Courses**: View enrolled courses.
  - **Grades**: View grades for enrolled courses.
  - **Academic Records**: View academic records and generated reports.
  - **Profile & Settings**: Update profile, change username, and password.
  - **Logout**: Log out of the system.

---

## 🏗️ System Architecture
The system follows a **Model-View-Controller (MVC)** architecture, ensuring a clear separation between the user interface, business logic, and data access layers.

### Key Components:
1. **Controllers**:
   - Handle user interactions and business logic.
   - Manage navigation between different views (e.g., dashboards, forms).

2. **Models**:
   - Represent the data entities (e.g., Student, Course, Teacher).
   - Encapsulate the business logic and data validation.

3. **Database Access Objects (DAOs)**:
   - Provide methods for interacting with the MySQL database.
   - Handle CRUD (Create, Read, Update, Delete) operations for all entities.

4. **Utilities**:
   - Utility classes for common functionality (e.g., password hashing, file path handling).

5. **Reports**:
   - Classes for generating and managing reports (e.g., academic transcripts, course reports).

---

## 🗃️ Database Schema
The system uses a MySQL database with the following key tables:

1. **`admin`**: Stores admin user details.
2. **`course`**: Stores course details.
3. **`courseoffering`**: Stores course offerings for specific academic years.
4. **`department`**: Stores academic department details.
5. **`grade`**: Stores student grades for specific courses.
6. **`program`**: Stores academic program details.
7. **`role`**: Stores user roles (e.g., admin, teacher, student).
8. **`student`**: Stores student details.
9. **`student_reports`**: Stores student-generated reports.
10. **`studentacademicrecord`**: Stores academic records for students.
11. **`studentcourse`**: Stores student enrollments in courses.
12. **`teacher`**: Stores teacher details.
13. **`teachercourse`**: Stores teacher assignments to courses.

### Default Values in the Database
The `schema.sql` file comes with default values for certain tables to ensure the system is functional upon initial setup:

1. **Role Table**:
   - The `role` table is pre-populated with the following roles:
     - **Super Admin**
     - **Admin**
   - These roles are essential for system administration and cannot be modified or added through the user interface.

2. **Program Table**:
   - The `program` table is pre-populated with the following programs:
     - **Undergraduate**
     - **Graduate**
   - Similar to roles, these programs are fixed and cannot be modified or added through the user interface. 

3. **Department Table**:
   - The `department` table is pre-populated with the following departments:
     - **Software Engineering**
     - **Information Technology**        
     - **Computer Science**               
     - **Information Technology Systems** 
   - Unlike roles and programs, departments can be added directly through the database. This flexibility allows institutions to customize the system according to their organizational structure.

---

## 🔄 Workflow and Processes

1. **User Authentication**:
   - Users log in using their username/email and password.
   - Temporary passwords are generated for new users.

2. **Course Enrollment**:
   - Admins or students enroll students in course offerings.
   - Teachers are assigned to course offerings.

3. **Grade Management**:
   - Teachers enter grades for students.
   - Academic records (e.g., SGPA, CGPA) are automatically updated.

4. **Report Generation**:
   - Admins generate reports (e.g., academic transcripts, course reports).
   - Reports are exported in various formats (e.g., PDF, Excel).

---

## 🔒 Security and Data Privacy
1. **Password Hashing**:
   - Passwords are hashed using secure algorithms (e.g., bcrypt).
   - Temporary passwords are securely generated and hashed before storage.

2. **Session Management**:
   - User sessions are managed securely to prevent unauthorized access.

3. **Data Validation**:
   - Input data is validated to prevent SQL injection and other attacks.

---

## 🚀 Deployment and Maintenance
1. **Deployment**:
   - The system can be deployed on a local server or cloud-based environment.
   - MySQL database is required for data storage.

2. **Maintenance**:
   - Regular database backups are recommended.
   - System updates can be applied through version control (e.g., Git).

---

## 🎯 Conclusion
STIMS is a **robust and scalable system** designed to streamline the management of student, teacher, and course-related data. Its **modular architecture**, **secure authentication**, and **comprehensive reporting features** make it ideal for educational institutions. Default values for roles (Super Admin, Admin) and programs (Undergraduate, Graduate) ensure smooth setup but cannot be modified, as the core logic depends on them. However, departments can be added or customized, providing flexibility while maintaining system stability.

---
