# Database Schema Documentation

This document provides an overview of the database schema for the **STIMS (Student Information Management System)** project. The database is designed to manage student, teacher, course, and administrative data efficiently.

---

## Database: `stims_dba`

The database `stims_dba` contains the following tables:

1. **`admin`**
2. **`course`**
3. **`courseoffering`**
4. **`department`**
5. **`grade`**
6. **`program`**
7. **`role`**
8. **`student`**
9. **`student_reports`**
10. **`studentacademicrecord`**
11. **`studentcourse`**
12. **`teacher`**
13. **`teachercourse`**

---

## Table Details

### 1. `admin`
Stores information about system administrators.

| Column Name       | Data Type        | Constraints                          | Description                          |
|-------------------|------------------|--------------------------------------|--------------------------------------|
| `admin_id`        | `INT`            | `PRIMARY KEY`, `AUTO_INCREMENT`      | Unique ID for the admin.             |
| `adminId_No`      | `VARCHAR(50)`    | `NOT NULL`, `UNIQUE`                 | Admin identification number.         |
| `username`        | `VARCHAR(50)`    | `NOT NULL`, `UNIQUE`                 | Admin username.                      |
| `password_hash`   | `VARCHAR(255)`   | `NOT NULL`                           | Hashed password.                     |
| `first_name`      | `VARCHAR(50)`    | `NOT NULL`                           | Admin's first name.                  |
| `middle_name`     | `VARCHAR(50)`    |                                      | Admin's middle name.                 |
| `last_name`       | `VARCHAR(50)`    | `NOT NULL`                           | Admin's last name.                   |
| `sex`             | `ENUM('M', 'F')` |                                      | Admin's gender.                      |
| `date_of_birth`   | `DATE`           |                                      | Admin's date of birth.               |
| `email`           | `VARCHAR(100)`   | `UNIQUE`                             | Admin's email address.               |
| `phone_number`    | `VARCHAR(15)`    |                                      | Admin's phone number.                |
| `address`         | `VARCHAR(255)`   |                                      | Admin's address.                     |
| `role_id`         | `INT`            | `FOREIGN KEY (role_id)`              | Role ID (references `role` table).   |
| `last_login`      | `DATETIME`       |                                      | Timestamp of last login.             |
| `created_at`      | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP`          | Timestamp of record creation.        |
| `updated_at`      | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP ON UPDATE`| Timestamp of last update.            |

---

### 2. `course`
Stores information about courses offered by the institution.

| Column Name          | Data Type        | Constraints                          | Description                          |
|----------------------|------------------|--------------------------------------|--------------------------------------|
| `course_id`          | `INT`            | `PRIMARY KEY`, `AUTO_INCREMENT`      | Unique ID for the course.            |
| `course_code`        | `VARCHAR(20)`    | `NOT NULL`, `UNIQUE`                 | Course code (e.g., CS101).           |
| `course_name`        | `VARCHAR(100)`   | `NOT NULL`                           | Name of the course.                  |
| `course_description` | `TEXT`           |                                      | Description of the course.           |
| `credits`            | `INT`            |                                      | Number of credits for the course.    |
| `course_level`       | `TINYINT`        |                                      | Level of the course (e.g., 1, 2).    |
| `year`               | `INT`            |                                      | Year of the course.                  |
| `semester`           | `INT`            |                                      | Semester of the course.              |
| `department_id`      | `INT`            | `FOREIGN KEY (department_id)`        | Department ID (references `department` table). |
| `created_at`         | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP`          | Timestamp of record creation.        |
| `updated_at`         | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP ON UPDATE`| Timestamp of last update.            |

---

### 3. `courseoffering`
Stores information about course offerings for specific academic years.

| Column Name       | Data Type        | Constraints                          | Description                          |
|-------------------|------------------|--------------------------------------|--------------------------------------|
| `offering_id`     | `INT`            | `PRIMARY KEY`, `AUTO_INCREMENT`      | Unique ID for the course offering.   |
| `course_id`       | `INT`            | `FOREIGN KEY (course_id)`            | Course ID (references `course` table).|
| `academic_year`   | `YEAR`           | `NOT NULL`                           | Academic year of the offering.       |
| `year`            | `INT`            |                                      | Year of the offering.                |
| `semester`        | `INT`            | `CHECK (semester IN (1, 2, 3))`      | Semester of the offering.            |
| `created_at`      | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP`          | Timestamp of record creation.        |
| `updated_at`      | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP ON UPDATE`| Timestamp of last update.            |

---

### 4. `department`
Stores information about academic departments.

| Column Name       | Data Type        | Constraints                          | Description                          |
|-------------------|------------------|--------------------------------------|--------------------------------------|
| `department_id`   | `INT`            | `PRIMARY KEY`, `AUTO_INCREMENT`      | Unique ID for the department.        |
| `department_name` | `VARCHAR(100)`   | `NOT NULL`, `UNIQUE`                 | Name of the department.              |
| `updated_at`      | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP ON UPDATE`| Timestamp of last update.            |

---

### 5. `grade`
Stores student grades for specific courses.

| Column Name          | Data Type        | Constraints                          | Description                          |
|----------------------|------------------|--------------------------------------|--------------------------------------|
| `grade_id`           | `INT`            | `PRIMARY KEY`, `AUTO_INCREMENT`      | Unique ID for the grade.             |
| `student_course_id`  | `INT`            | `FOREIGN KEY (student_course_id)`    | Student-course ID (references `studentcourse` table). |
| `grade`              | `CHAR(2)`        | `NOT NULL`                           | Grade (e.g., A, B, C).               |
| `created_at`         | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP`          | Timestamp of record creation.        |
| `updated_at`         | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP ON UPDATE`| Timestamp of last update.            |

---

### 6. `program`
Stores information about academic programs.

| Column Name       | Data Type        | Constraints                          | Description                          |
|-------------------|------------------|--------------------------------------|--------------------------------------|
| `program_id`      | `INT`            | `PRIMARY KEY`, `AUTO_INCREMENT`      | Unique ID for the program.           |
| `program_name`    | `VARCHAR(100)`   | `NOT NULL`, `UNIQUE`                 | Name of the program.                 |
| `updated_at`      | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP ON UPDATE`| Timestamp of last update.            |

---

### 7. `role`
Stores user roles (e.g., admin, teacher, student).

| Column Name       | Data Type        | Constraints                          | Description                          |
|-------------------|------------------|--------------------------------------|--------------------------------------|
| `role_id`         | `INT`            | `PRIMARY KEY`, `AUTO_INCREMENT`      | Unique ID for the role.              |
| `role_name`       | `VARCHAR(50)`    | `NOT NULL`, `UNIQUE`                 | Name of the role.                    |
| `updated_at`      | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP ON UPDATE`| Timestamp of last update.            |

---

### 8. `student`
Stores information about students.

| Column Name          | Data Type        | Constraints                          | Description                          |
|----------------------|------------------|--------------------------------------|--------------------------------------|
| `student_id`         | `INT`            | `PRIMARY KEY`, `AUTO_INCREMENT`      | Unique ID for the student.           |
| `studentId_No`       | `VARCHAR(255)`   | `UNIQUE`                             | Student identification number.       |
| `username`           | `VARCHAR(50)`    | `NOT NULL`, `UNIQUE`                 | Student username.                    |
| `password_hash`      | `VARCHAR(255)`   | `NOT NULL`                           | Hashed password.                     |
| `first_name`         | `VARCHAR(50)`    | `NOT NULL`                           | Student's first name.                |
| `middle_name`        | `VARCHAR(50)`    |                                      | Student's middle name.               |
| `last_name`          | `VARCHAR(50)`    | `NOT NULL`                           | Student's last name.                 |
| `email`              | `VARCHAR(100)`   | `UNIQUE`                             | Student's email address.             |
| `phone_number`       | `VARCHAR(15)`    |                                      | Student's phone number.              |
| `address`            | `VARCHAR(255)`   |                                      | Student's address.                   |
| `date_of_birth`      | `DATE`           |                                      | Student's date of birth.             |
| `program_id`         | `INT`            | `FOREIGN KEY (program_id)`           | Program ID (references `program` table). |
| `department_id`      | `INT`            | `FOREIGN KEY (department_id)`        | Department ID (references `department` table). |
| `sex`                | `ENUM('M', 'F')` | `NOT NULL`                           | Student's gender.                    |
| `enrollment_date`    | `DATE`           | `NOT NULL`                           | Date of enrollment.                  |
| `graduation_date`    | `DATE`           |                                      | Date of graduation.                  |
| `created_at`         | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP`          | Timestamp of record creation.        |
| `updated_at`         | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP ON UPDATE`| Timestamp of last update.            |

---

### 9. `student_reports`
Stores student-generated reports.

| Column Name          | Data Type        | Constraints                          | Description                          |
|----------------------|------------------|--------------------------------------|--------------------------------------|
| `report_id`          | `INT`            | `PRIMARY KEY`, `AUTO_INCREMENT`      | Unique ID for the report.            |
| `student_id`         | `INT`            | `FOREIGN KEY (student_id)`           | Student ID (references `student` table). |
| `report_name`        | `VARCHAR(255)`   | `NOT NULL`                           | Name of the report.                  |
| `report_content`     | `LONGBLOB`       | `NOT NULL`                           | Content of the report.               |
| `created_at`         | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP`          | Timestamp of record creation.        |
| `updated_at`         | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP ON UPDATE`| Timestamp of last update.            |

---

### 10. `studentacademicrecord`
Stores academic records for students.

| Column Name          | Data Type        | Constraints                          | Description                          |
|----------------------|------------------|--------------------------------------|--------------------------------------|
| `student_id`         | `INT`            | `PRIMARY KEY`                        | Student ID (references `student` table). |
| `academic_year`      | `YEAR`           | `PRIMARY KEY`                        | Academic year.                       |
| `year`               | `INT`            | `PRIMARY KEY`                        | Year of study.                       |
| `semester`           | `INT`            | `PRIMARY KEY`, `CHECK (semester IN (1, 2))` | Semester.            |
| `total_credits`      | `INT`            | `NOT NULL`                           | Total credits earned.                |
| `sgpa`               | `DECIMAL(3, 2)`  |                                      | Semester GPA.                        |
| `cgpa`               | `DECIMAL(3, 2)`  |                                      | Cumulative GPA.                      |
| `updated_at`         | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP ON UPDATE`| Timestamp of last update.            |

---

### 11. `studentcourse`
Stores student enrollment in courses.

| Column Name          | Data Type        | Constraints                          | Description                          |
|----------------------|------------------|--------------------------------------|--------------------------------------|
| `student_course_id`  | `INT`            | `PRIMARY KEY`, `AUTO_INCREMENT`      | Unique ID for the student-course record. |
| `student_id`         | `INT`            | `FOREIGN KEY (student_id)`           | Student ID (references `student` table). |
| `offering_id`        | `INT`            | `FOREIGN KEY (offering_id)`          | Offering ID (references `courseoffering` table). |
| `enrollment_date`    | `DATE`           | `NOT NULL`                           | Date of enrollment.                  |
| `created_at`         | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP`          | Timestamp of record creation.        |
| `updated_at`         | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP ON UPDATE`| Timestamp of last update.            |

---

### 12. `teacher`
Stores information about teachers.

| Column Name          | Data Type        | Constraints                          | Description                          |
|----------------------|------------------|--------------------------------------|--------------------------------------|
| `teacher_id`         | `INT`            | `PRIMARY KEY`, `AUTO_INCREMENT`      | Unique ID for the teacher.           |
| `username`           | `VARCHAR(50)`    | `NOT NULL`, `UNIQUE`                 | Teacher username.                    |
| `password_hash`      | `VARCHAR(255)`   | `NOT NULL`                           | Hashed password.                     |
| `first_name`         | `VARCHAR(50)`    | `NOT NULL`                           | Teacher's first name.                |
| `middle_name`        | `VARCHAR(50)`    |                                      | Teacher's middle name.               |
| `last_name`          | `VARCHAR(50)`    | `NOT NULL`                           | Teacher's last name.                 |
| `sex`                | `ENUM('M', 'F')` |                                      | Teacher's gender.                    |
| `date_of_birth`      | `DATE`           |                                      | Teacher's date of birth.             |
| `email`              | `VARCHAR(100)`   | `UNIQUE`                             | Teacher's email address.             |
| `phone_number`       | `VARCHAR(15)`    |                                      | Teacher's phone number.              |
| `address`            | `VARCHAR(255)`   |                                      | Teacher's address.                   |
| `department_id`      | `INT`            | `FOREIGN KEY (department_id)`        | Department ID (references `department` table). |
| `last_login`         | `DATETIME`       |                                      | Timestamp of last login.             |
| `created_at`         | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP`          | Timestamp of record creation.        |
| `updated_at`         | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP ON UPDATE`| Timestamp of last update.            |

---

### 13. `teachercourse`
Stores teacher assignments to courses.

| Column Name          | Data Type        | Constraints                          | Description                          |
|----------------------|------------------|--------------------------------------|--------------------------------------|
| `teacher_course_id`  | `INT`            | `PRIMARY KEY`, `AUTO_INCREMENT`      | Unique ID for the teacher-course record. |
| `teacher_id`         | `INT`            | `FOREIGN KEY (teacher_id)`           | Teacher ID (references `teacher` table). |
| `offering_id`        | `INT`            | `FOREIGN KEY (offering_id)`          | Offering ID (references `courseoffering` table). |
| `assigned_date`      | `DATE`           | `NOT NULL`                           | Date of assignment.                  |
| `created_at`         | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP`          | Timestamp of record creation.        |
| `updated_at`         | `TIMESTAMP`      | `DEFAULT CURRENT_TIMESTAMP ON UPDATE`| Timestamp of last update.            |

---

## Relationships

1. **`admin`** ↔ **`role`**:
   - `admin.role_id` references `role.role_id`.

2. **`course`** ↔ **`department`**:
   - `course.department_id` references `department.department_id`.

3. **`courseoffering`** ↔ **`course`**:
   - `courseoffering.course_id` references `course.course_id`.

4. **`student`** ↔ **`program`**:
   - `student.program_id` references `program.program_id`.

5. **`student`** ↔ **`department`**:
   - `student.department_id` references `department.department_id`.

6. **`studentcourse`** ↔ **`student`**:
   - `studentcourse.student_id` references `student.student_id`.

7. **`studentcourse`** ↔ **`courseoffering`**:
   - `studentcourse.offering_id` references `courseoffering.offering_id`.

8. **`grade`** ↔ **`studentcourse`**:
   - `grade.student_course_id` references `studentcourse.student_course_id`.

9. **`teacher`** ↔ **`department`**:
   - `teacher.department_id` references `department.department_id`.

10. **`teachercourse`** ↔ **`teacher`**:
    - `teachercourse.teacher_id` references `teacher.teacher_id`.

11. **`teachercourse`** ↔ **`courseoffering`**:
    - `teachercourse.offering_id` references `courseoffering.offering_id`.

---

## Notes

- All tables include `created_at` and `updated_at` timestamps for tracking record creation and updates.
- Foreign key constraints ensure data integrity across related tables.
- The schema is designed to support a multi-role system (admin, teacher, student) with role-based access control.

### Default Values and Restrictions
1. **Roles**:
   - The `role` table is pre-populated with two default roles: **Super Admin** and **Admin**.
     ```
     +---------+-------------+
     | role_id | role_name   |
     +---------+-------------+
     |       1 | Super Admin | 
     |       2 | Admin       |
     +---------+-------------+
     ```
   - These roles are integral to the system's core logic and **cannot be modified or added** through the user interface. Any changes to roles must be made directly in the database, but this is not recommended as it may cause system instability.

2. **Programs**:
   - The `program` table is pre-populated with two default programs: **Undergraduate** and **Graduate**.
     ```
     +------------+---------------+
     | program_id | program_name  |
     +------------+---------------+
     |          1 | Undergraduate |
     |          2 | Graduate      |
     +------------+---------------+
     ```
   - Similar to roles, these programs are fixed and **cannot be modified or added** through the user interface. Changes must be made directly in the database, but this is discouraged due to potential system failures.

3. **Departments**:
   - The `department` table is pre-populated with four default departments:
     ```
     +---------------+--------------------------------+
     | department_id | department_name                |
     +---------------+--------------------------------+
     |             1 | Software Engineering           |
     |             2 | Information Technology         | 
     |             3 | Computer Science               |
     |             4 | Information Technology Systems | 
     +---------------+--------------------------------+
     ```
   - Unlike roles and programs, departments can be **added or modified** directly through the database. This flexibility allows institutions to customize the system according to their organizational structure.

---

This document provides a comprehensive overview of the database schema for the STIMS project. For further details, refer to the `schema.sql` file in the `database` directory.