package system;

import model.*;
import java.sql.*;
import java.util.*;
import java.io.*;
import javax.swing.JOptionPane;

public class DatabaseStudentSystem implements StudentSystem {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/student_system";
    private static final String USER = "root";
    private static final String PASS = "833300";

    private Connection connection;

    public DatabaseStudentSystem() {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Open a connection
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected successfully!");
            
            // Create tables if they don't exist
            createTables();
            
            // Check and fix schema issues
            checkAndFixSchema();
            
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Add the MySQL JDBC driver to your classpath.");
            e.printStackTrace();
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to database: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database: " + e.getMessage(), e);
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // First, check if the old schema exists and drop constraints if needed
            try {
                stmt.execute("ALTER TABLE courses DROP FOREIGN KEY courses_ibfk_1");
            } catch (SQLException e) {
                // Constraint might not exist, that's okay
            }
            
            // Create students table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS students (" +
                "id VARCHAR(50) PRIMARY KEY," +
                "first_name VARCHAR(100)," +
                "last_name VARCHAR(100)," +
                "email VARCHAR(100)" +
                ")"
            );

            // Create lecturers table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS lecturers (" +
                "id VARCHAR(50) PRIMARY KEY," +
                "first_name VARCHAR(100)," +
                "last_name VARCHAR(100)," +
                "email VARCHAR(100)" +
                ")"
            );

            // Create programmes table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS programmes (" +
                "programme_id VARCHAR(50) PRIMARY KEY," +
                "programme_name VARCHAR(100)" +
                ")"
            );

            // Drop courses table if it exists with old schema
            try {
                stmt.execute("DROP TABLE IF EXISTS courses");
            } catch (SQLException e) {
                // Table might not exist, that's okay
            }

            // Create courses table with correct foreign key
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS courses (" +
                "course_id VARCHAR(50) PRIMARY KEY," +
                "course_name VARCHAR(100)," +
                "lecturer_id VARCHAR(50)," +
                "FOREIGN KEY (lecturer_id) REFERENCES lecturers(id)" +
                ")"
            );

            // Create student_programme table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS student_programme (" +
                "student_id VARCHAR(50)," +
                "programme_id VARCHAR(50)," +
                "FOREIGN KEY (student_id) REFERENCES students(id)," +
                "FOREIGN KEY (programme_id) REFERENCES programmes(programme_id)," +
                "PRIMARY KEY (student_id)" +
                ")"
            );

            // Create student_course table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS student_course (" +
                "student_id VARCHAR(50)," +
                "course_id VARCHAR(50)," +
                "score DOUBLE," +
                "FOREIGN KEY (student_id) REFERENCES students(id)," +
                "FOREIGN KEY (course_id) REFERENCES courses(course_id)," +
                "PRIMARY KEY (student_id, course_id)" +
                ")"
            );

            // Create programme_course table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS programme_course (" +
                "programme_id VARCHAR(50)," +
                "course_id VARCHAR(50)," +
                "FOREIGN KEY (programme_id) REFERENCES programmes(programme_id)," +
                "FOREIGN KEY (course_id) REFERENCES courses(course_id)," +
                "PRIMARY KEY (programme_id, course_id)" +
                ")"
            );
        }
    }

    private void checkAndFixSchema() {
        try {
            // Check if student_programme table exists
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "student_programme", null);
            
            if (!tables.next()) {
                System.out.println("student_programme table doesn't exist. Creating it...");
                Statement stmt = connection.createStatement();
                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS student_programme (" +
                    "student_id VARCHAR(50)," +
                    "programme_id VARCHAR(50)," +
                    "FOREIGN KEY (student_id) REFERENCES students(id)," +
                    "FOREIGN KEY (programme_id) REFERENCES programmes(programme_id)," +
                    "PRIMARY KEY (student_id)" +
                    ")"
                );
                System.out.println("student_programme table created successfully");
            }
        } catch (SQLException e) {
            System.err.println("Error checking/fixing schema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean addStudent(Student student) {
        try {
            // Check if student already exists
            PreparedStatement checkStmt = connection.prepareStatement(
                "SELECT id FROM students WHERE id = ?"
            );
            checkStmt.setString(1, student.getId());
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                return false; // Student already exists
            }
            
            // Add student
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO students (id, first_name, last_name, email) VALUES (?, ?, ?, ?)"
            );
            stmt.setString(1, student.getId());
            stmt.setString(2, student.getFirstName());
            stmt.setString(3, student.getLastName());
            stmt.setString(4, student.getEmail());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean addCourse(Course course) {
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO courses (course_id, course_name) VALUES (?, ?)"
            )) {
                stmt.setString(1, course.getCourseId());
                stmt.setString(2, course.getCourseName());
                stmt.executeUpdate();
                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                return false;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    @Override
    public boolean addProgramme(Programme programme) {
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO programmes (programme_id, programme_name) VALUES (?, ?)"
            )) {
                stmt.setString(1, programme.getProgrammeId());
                stmt.setString(2, programme.getProgrammeName());
                stmt.executeUpdate();
                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                return false;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    @Override
    public boolean addLecturer(Lecturer lecturer) {
        try {
            // Check if lecturer already exists
            PreparedStatement checkStmt = connection.prepareStatement(
                "SELECT id FROM lecturers WHERE id = ?"
            );
            checkStmt.setString(1, lecturer.getId());
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                return false; // Lecturer already exists
            }
            
            // Add lecturer
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO lecturers (id, first_name, last_name, email) VALUES (?, ?, ?, ?)"
            );
            stmt.setString(1, lecturer.getId());
            stmt.setString(2, lecturer.getFirstName());
            stmt.setString(3, lecturer.getLastName());
            stmt.setString(4, lecturer.getEmail());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Student getStudent(String id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT s.*, sp.programme_id FROM students s " +
                "LEFT JOIN student_programme sp ON s.id = sp.student_id " +
                "WHERE s.id = ?");
            stmt.setString(1, id);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Student student = new Student(
                    rs.getString("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email")
                );
                
                String progId = rs.getString("programme_id");
                if (progId != null) {
                    Programme programme = getProgramme(progId);
                    if (programme != null) {
                        student.setProgramme(programme);
                    }
                }
                
                loadStudentCourses(student);
                return student;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadStudentCourses(Student student) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
            "SELECT c.*, sc.score FROM courses c " +
            "JOIN student_course sc ON c.course_id = sc.course_id " +
            "WHERE sc.student_id = ?");
        stmt.setString(1, student.getId());
        
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Course course = new Course(
                rs.getString("course_id"),
                rs.getString("course_name")
            );
            student.getCourses().add(course);
            student.getResults().put(course.getCourseId(), rs.getDouble("score"));
        }
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public boolean enrollStudentInProgramme(String studentId, String programmeId) {
        try {
            System.out.println("Attempting to enroll student " + studentId + " in programme " + programmeId);
            
            // Debug: Print all students in the database
            System.out.println("All students in database:");
            Set<Student> allStudents = getAllStudents();
            for (Student s : allStudents) {
                System.out.println("ID: " + s.getId() + ", Name: " + s.getFirstName() + " " + s.getLastName());
            }
            
            // First, check if the student exists in the database
            PreparedStatement studentCheckStmt = connection.prepareStatement(
                "SELECT * FROM students WHERE id = ?"
            );
            studentCheckStmt.setString(1, studentId);
            ResultSet studentRs = studentCheckStmt.executeQuery();
            
            boolean studentExists = studentRs.next();
            System.out.println("Student exists in database: " + studentExists);
            
            if (!studentExists) {
                // Try to find the student by case-insensitive search
                PreparedStatement fuzzySearchStmt = connection.prepareStatement(
                    "SELECT * FROM students WHERE LOWER(id) = LOWER(?)"
                );
                fuzzySearchStmt.setString(1, studentId);
                ResultSet fuzzyRs = fuzzySearchStmt.executeQuery();
                
                if (fuzzyRs.next()) {
                    // Found student with different case
                    studentId = fuzzyRs.getString("id");
                    System.out.println("Found student with ID: " + studentId + " (case-insensitive match)");
                    studentExists = true;
                } else {
                    System.out.println("Student not found even with case-insensitive search");
                    
                    // Prompt for student information and add to database
                    int addNew = JOptionPane.showConfirmDialog(
                        null,
                        "Student with ID " + studentId + " not found. Would you like to add this student?",
                        "Student Not Found",
                        JOptionPane.YES_NO_OPTION
                    );
                    
                    if (addNew == JOptionPane.YES_OPTION) {
                        String firstName = JOptionPane.showInputDialog("Enter first name:");
                        String lastName = JOptionPane.showInputDialog("Enter last name:");
                        String email = JOptionPane.showInputDialog("Enter email:");
                        
                        if (firstName != null && lastName != null && email != null) {
                            Student newStudent = new Student(studentId, firstName, lastName, email);
                            boolean added = addStudent(newStudent);
                            System.out.println("Added new student: " + added);
                            
                            if (!added) {
                                JOptionPane.showMessageDialog(null, "Failed to add student to database.");
                                return false;
                            }
                            studentExists = true;
                        } else {
                            JOptionPane.showMessageDialog(null, "Student information incomplete. Cannot add student.");
                            return false;
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Cannot enroll non-existent student in programme.");
                        return false;
                    }
                }
            }
            
            // Check if the programme exists
            PreparedStatement programmeCheckStmt = connection.prepareStatement(
                "SELECT * FROM programmes WHERE programme_id = ?"
            );
            programmeCheckStmt.setString(1, programmeId);
            ResultSet programmeRs = programmeCheckStmt.executeQuery();
            
            if (!programmeRs.next()) {
                System.out.println("Programme not found: " + programmeId);
                
                // Debug: Print all programmes
                System.out.println("All programmes in database:");
                Set<Programme> allProgrammes = getAllProgrammes();
                for (Programme p : allProgrammes) {
                    System.out.println("ID: " + p.getProgrammeId() + ", Name: " + p.getProgrammeName());
                }
                
                // Try case-insensitive search
                PreparedStatement fuzzyProgrammeStmt = connection.prepareStatement(
                    "SELECT * FROM programmes WHERE LOWER(programme_id) = LOWER(?)"
                );
                fuzzyProgrammeStmt.setString(1, programmeId);
                ResultSet fuzzyProgrammeRs = fuzzyProgrammeStmt.executeQuery();
                
                if (fuzzyProgrammeRs.next()) {
                    // Found programme with different case
                    programmeId = fuzzyProgrammeRs.getString("programme_id");
                    System.out.println("Found programme with ID: " + programmeId + " (case-insensitive match)");
                } else {
                    // Prompt for programme information and add to database
                    int addNew = JOptionPane.showConfirmDialog(
                        null,
                        "Programme with ID " + programmeId + " not found. Would you like to add this programme?",
                        "Programme Not Found",
                        JOptionPane.YES_NO_OPTION
                    );
                    
                    if (addNew == JOptionPane.YES_OPTION) {
                        String programmeName = JOptionPane.showInputDialog("Enter programme name:");
                        
                        if (programmeName != null) {
                            Programme newProgramme = new Programme(programmeId, programmeName);
                            boolean added = addProgramme(newProgramme);
                            System.out.println("Added new programme: " + added);
                            
                            if (!added) {
                                JOptionPane.showMessageDialog(null, "Failed to add programme to database.");
                                return false;
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Programme information incomplete. Cannot add programme.");
                            return false;
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Cannot enroll student in non-existent programme.");
                        return false;
                    }
                }
            }
            
            // Check if student is already enrolled in a programme
            PreparedStatement checkEnrollmentStmt = connection.prepareStatement(
                "SELECT * FROM student_programme WHERE student_id = ?"
            );
            checkEnrollmentStmt.setString(1, studentId);
            ResultSet enrollmentRs = checkEnrollmentStmt.executeQuery();
            
            if (enrollmentRs.next()) {
                String currentProgramme = enrollmentRs.getString("programme_id");
                System.out.println("Student already enrolled in programme: " + currentProgramme);
                
                // Ask if user wants to change the programme
                int response = JOptionPane.showConfirmDialog(
                    null, 
                    "Student is already enrolled in programme " + currentProgramme + 
                    ". Do you want to change to " + programmeId + "?",
                    "Confirm Change",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (response == JOptionPane.YES_OPTION) {
                    // Update the existing enrollment
                    PreparedStatement updateStmt = connection.prepareStatement(
                        "UPDATE student_programme SET programme_id = ? WHERE student_id = ?"
                    );
                    updateStmt.setString(1, programmeId);
                    updateStmt.setString(2, studentId);
                    int rowsAffected = updateStmt.executeUpdate();
                    System.out.println("Updated enrollment: " + (rowsAffected > 0));
                    
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Student's programme updated successfully!");
                        return true;
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to update student's programme.");
                        return false;
                    }
                } else {
                    return false;
                }
            }
            
            // Enroll student in programme
            PreparedStatement insertStmt = connection.prepareStatement(
                "INSERT INTO student_programme (student_id, programme_id) VALUES (?, ?)"
            );
            insertStmt.setString(1, studentId);
            insertStmt.setString(2, programmeId);
            
            System.out.println("Executing SQL: INSERT INTO student_programme (student_id, programme_id) VALUES ('" + 
                              studentId + "', '" + programmeId + "')");
            
            int rowsAffected = insertStmt.executeUpdate();
            System.out.println("Enrollment result: " + (rowsAffected > 0));
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Student successfully enrolled in programme!");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Failed to enroll student in programme.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error enrolling student in programme: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean registerStudentForCourse(String studentId, String courseId) {
        try {
            System.out.println("Attempting to register student " + studentId + " for course " + courseId);
            
            // First, check if the persons table exists and if the student exists in it
            try {
                PreparedStatement personCheckStmt = connection.prepareStatement(
                    "INSERT INTO persons (id, first_name, last_name, email, type) " +
                    "SELECT id, first_name, last_name, email, 'STUDENT' FROM students " +
                    "WHERE id = ? AND NOT EXISTS (SELECT 1 FROM persons WHERE id = ?)"
                );
                personCheckStmt.setString(1, studentId);
                personCheckStmt.setString(2, studentId);
                personCheckStmt.executeUpdate();
                System.out.println("Ensured student exists in persons table");
            } catch (SQLException e) {
                System.out.println("Note: Could not ensure student in persons table: " + e.getMessage());
                // This might happen if the persons table doesn't exist or has a different structure
                // We'll continue and let the main operation determine if it succeeds
            }
            
            // Check if student already has 3 courses
            PreparedStatement checkStmt = connection.prepareStatement(
                "SELECT COUNT(*) FROM student_course WHERE student_id = ?"
            );
            checkStmt.setString(1, studentId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) >= 3) {
                System.out.println("Student already has maximum courses");
                return false; // Student already has maximum courses
            }
            
            // Register student for course
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO student_course (student_id, course_id) VALUES (?, ?)"
            );
            stmt.setString(1, studentId);
            stmt.setString(2, courseId);
            
            System.out.println("Executing SQL: INSERT INTO student_course (student_id, course_id) VALUES ('" + 
                              studentId + "', '" + courseId + "')");
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Registration result: " + (rowsAffected > 0));
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error registering student for course: " + e.getMessage());
            e.printStackTrace();
            
            // If the error is about the foreign key constraint, try to fix the schema
            if (e.getMessage().contains("foreign key constraint fails") && 
                e.getMessage().contains("persons")) {
                
                try {
                    System.out.println("Attempting to fix schema issue...");
                    
                    // Create persons table if it doesn't exist
                    Statement fixStmt = connection.createStatement();
                    fixStmt.execute(
                        "CREATE TABLE IF NOT EXISTS persons (" +
                        "id VARCHAR(50) PRIMARY KEY," +
                        "first_name VARCHAR(100)," +
                        "last_name VARCHAR(100)," +
                        "email VARCHAR(100)," +
                        "type VARCHAR(20)" +
                        ")"
                    );
                    
                    // Copy student data to persons table
                    fixStmt.execute(
                        "INSERT IGNORE INTO persons (id, first_name, last_name, email, type) " +
                        "SELECT id, first_name, last_name, email, 'STUDENT' FROM students"
                    );
                    
                    System.out.println("Schema fix attempted, trying registration again...");
                    
                    // Try registration again
                    return registerStudentForCourse(studentId, courseId);
                } catch (SQLException fixError) {
                    System.err.println("Failed to fix schema: " + fixError.getMessage());
                    fixError.printStackTrace();
                }
            }
            
            return false;
        }
    }

    @Override
    public boolean updateStudentScore(String studentId, String courseId, double score) {
        try {
            // Check if student is registered for the course
            PreparedStatement checkStmt = connection.prepareStatement(
                "SELECT * FROM student_course WHERE student_id = ? AND course_id = ?"
            );
            checkStmt.setString(1, studentId);
            checkStmt.setString(2, courseId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                return false; // Student not registered for this course
            }
            
            // Update the score
            PreparedStatement updateStmt = connection.prepareStatement(
                "UPDATE student_course SET score = ? WHERE student_id = ? AND course_id = ?"
            );
            updateStmt.setDouble(1, score);
            updateStmt.setString(2, studentId);
            updateStmt.setString(3, courseId);
            
            int rowsAffected = updateStmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Programme getProgramme(String programmeId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM programmes WHERE programme_id = ?"
            );
            stmt.setString(1, programmeId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Programme programme = new Programme(
                    rs.getString("programme_id"),
                    rs.getString("programme_name")
                );
                
                // Load courses for this programme
                loadProgrammeCourses(programme);
                
                // Load students enrolled in this programme
                loadProgrammeStudents(programme);
                
                return programme;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadProgrammeCourses(Programme programme) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
            "SELECT c.* FROM courses c " +
            "JOIN programme_course pc ON c.course_id = pc.course_id " +
            "WHERE pc.programme_id = ?"
        );
        stmt.setString(1, programme.getProgrammeId());
        
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Course course = new Course(
                rs.getString("course_id"),
                rs.getString("course_name")
            );
            programme.addCourse(course);
        }
    }

    private void loadProgrammeStudents(Programme programme) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
            "SELECT s.* FROM students s " +
            "JOIN student_programme sp ON s.id = sp.student_id " +
            "WHERE sp.programme_id = ?"
        );
        stmt.setString(1, programme.getProgrammeId());
        
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Student student = new Student(
                rs.getString("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email")
            );
            programme.addStudent(student);
        }
    }

    @Override
    public Course getCourse(String code) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM courses WHERE course_id = ?");
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Course(
                    rs.getString("course_id"),
                    rs.getString("course_name")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve course", e);
        }
    }

    @Override
    public Set<Student> getStudentsInCourse(String courseId) {
        try {
            Set<Student> students = new HashSet<>();
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT s.* FROM students s " +
                "JOIN student_course sc ON s.id = sc.student_id " +
                "WHERE sc.course_id = ? AND s.type = 'STUDENT'"
            );
            stmt.setString(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                students.add(new Student(
                    rs.getString("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email")
                ));
            }
            return students;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve course students", e);
        }
    }

    @Override
    public boolean assignCourseToLecturer(String lecturerId, String courseId) {
        try {
            // Check lecturer course count
            PreparedStatement checkStmt = connection.prepareStatement(
                "SELECT COUNT(*) FROM courses WHERE lecturer_id = ?"
            );
            checkStmt.setString(1, lecturerId);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) >= 2) {
                return false;
            }

            // Check if course already has a lecturer
            checkStmt = connection.prepareStatement(
                "SELECT lecturer_id FROM courses WHERE course_id = ?"
            );
            checkStmt.setString(1, courseId);
            rs = checkStmt.executeQuery();
            if (rs.next() && rs.getString("lecturer_id") != null) {
                return false;
            }

            // Assign lecturer to course
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE courses SET lecturer_id = ? WHERE course_id = ?"
            );
            stmt.setString(1, lecturerId);
            stmt.setString(2, courseId);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public Set<Student> getStudentsInProgramme(String programmeId) {
        try {
            Set<Student> students = new HashSet<>();
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT s.* FROM students s " +
                "JOIN student_programme sp ON s.id = sp.student_id " +
                "WHERE sp.programme_id = ? AND s.type = 'STUDENT'"
            );
            stmt.setString(1, programmeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                students.add(new Student(
                    rs.getString("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email")
                ));
            }
            return students;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve programme students", e);
        }
    }

    @Override
    public boolean addCourseToProgramme(String programmeId, String courseId) {
        try {
            // First check if both programme and course exist
            PreparedStatement checkProgrammeStmt = connection.prepareStatement(
                "SELECT * FROM programmes WHERE programme_id = ?"
            );
            checkProgrammeStmt.setString(1, programmeId);
            ResultSet programmeRs = checkProgrammeStmt.executeQuery();
            
            if (!programmeRs.next()) {
                return false; // Programme doesn't exist
            }
            
            PreparedStatement checkCourseStmt = connection.prepareStatement(
                "SELECT * FROM courses WHERE course_id = ?"
            );
            checkCourseStmt.setString(1, courseId);
            ResultSet courseRs = checkCourseStmt.executeQuery();
            
            if (!courseRs.next()) {
                return false; // Course doesn't exist
            }
            
            // Check if the association already exists
            PreparedStatement checkAssocStmt = connection.prepareStatement(
                "SELECT * FROM programme_course WHERE programme_id = ? AND course_id = ?"
            );
            checkAssocStmt.setString(1, programmeId);
            checkAssocStmt.setString(2, courseId);
            ResultSet assocRs = checkAssocStmt.executeQuery();
            
            if (assocRs.next()) {
                return true; // Association already exists, consider it a success
            }
            
            // Create the association
            PreparedStatement insertStmt = connection.prepareStatement(
                "INSERT INTO programme_course (programme_id, course_id) VALUES (?, ?)"
            );
            insertStmt.setString(1, programmeId);
            insertStmt.setString(2, courseId);
            
            int rowsAffected = insertStmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<String, Double> getStudentResults(String studentId) {
        try {
            Map<String, Double> results = new HashMap<>();
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT course_id, score FROM student_course WHERE student_id = ?"
            );
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                results.put(rs.getString("course_id"), rs.getDouble("score"));
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve student results", e);
        }
    }

    @Override
    public void loadData() {
        // Database is already loaded, no need to implement
    }

    @Override
    public void saveData() {
        // Database saves automatically, no need to implement
    }

    @Override
    public Set<Course> getCoursesByLecturer(String lecturerId) {
        try {
            Set<Course> courses = new HashSet<>();
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM courses WHERE lecturer_id = ?"
            );
            stmt.setString(1, lecturerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                courses.add(new Course(
                    rs.getString("course_id"),
                    rs.getString("course_name")
                ));
            }
            return courses;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve lecturer courses", e);
        }
    }

    @Override
    public Lecturer getLecturer(String lecturerId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM lecturers WHERE id = ?"
            );
            stmt.setString(1, lecturerId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Lecturer lecturer = new Lecturer(
                    rs.getString("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email")
                );
                
                // Load assigned courses
                loadLecturerCourses(lecturer);
                
                return lecturer;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean assignLecturerToCourse(String lecturerId, String courseId) {
        try {
            // Check if course already has a lecturer (no sharing)
            PreparedStatement courseCheckStmt = connection.prepareStatement(
                "SELECT lecturer_id FROM courses WHERE course_id = ?"
            );
            courseCheckStmt.setString(1, courseId);
            ResultSet courseRs = courseCheckStmt.executeQuery();
            
            if (courseRs.next() && courseRs.getString("lecturer_id") != null) {
                return false; // Course already has a lecturer
            }
            
            // Check lecturer course count (max 2)
            PreparedStatement checkStmt = connection.prepareStatement(
                "SELECT COUNT(*) FROM courses WHERE lecturer_id = ?"
            );
            checkStmt.setString(1, lecturerId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) >= 2) {
                return false; // Lecturer already has maximum courses
            }
            
            // Assign lecturer to course
            PreparedStatement updateStmt = connection.prepareStatement(
                "UPDATE courses SET lecturer_id = ? WHERE course_id = ?"
            );
            updateStmt.setString(1, lecturerId);
            updateStmt.setString(2, courseId);
            
            int rowsAffected = updateStmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void loadLecturerCourses(Lecturer lecturer) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
            "SELECT * FROM courses WHERE lecturer_id = ?"
        );
        stmt.setString(1, lecturer.getId());
        
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Course course = new Course(
                rs.getString("course_id"),
                rs.getString("course_name")
            );
            lecturer.assignCourse(course);
            course.setLecturer(lecturer);
        }
    }

    @Override
    public Set<Student> getAllStudents() {
        try {
            Set<Student> students = new HashSet<>();
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM students");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Student student = new Student(
                    rs.getString("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email")
                );
                students.add(student);
            }
            return students;
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    @Override
    public Set<Course> getAllCourses() {
        try {
            Set<Course> courses = new HashSet<>();
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM courses");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Course course = new Course(
                    rs.getString("course_id"),
                    rs.getString("course_name")
                );
                courses.add(course);
            }
            return courses;
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    @Override
    public Set<Programme> getAllProgrammes() {
        try {
            Set<Programme> programmes = new HashSet<>();
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM programmes");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Programme programme = new Programme(
                    rs.getString("programme_id"),
                    rs.getString("programme_name")
                );
                programmes.add(programme);
            }
            return programmes;
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    @Override
    public Set<Lecturer> getAllLecturers() {
        try {
            Set<Lecturer> lecturers = new HashSet<>();
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM lecturers");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Lecturer lecturer = new Lecturer(
                    rs.getString("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email")
                );
                lecturers.add(lecturer);
            }
            return lecturers;
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    private void printTableInfo(String tableName) throws SQLException {
        System.out.println("Table: " + tableName);
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + tableName);
        ResultSet rs = stmt.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        System.out.println("Columns:");
        for (int i = 1; i <= columnCount; i++) {
            System.out.println("Column " + i + ": " + metaData.getColumnName(i));
        }
        System.out.println();
    }

    // Add this method to help with debugging database issues
    public void debugDatabaseState() {
        try {
            System.out.println("\n--- DATABASE STATE ---");
            
            // Print all tables
            printTableInfo("students");
            printTableInfo("programmes");
            printTableInfo("courses");
            printTableInfo("student_programme");
            printTableInfo("student_course");
            printTableInfo("programme_course");
            
            // Print all students
            System.out.println("\nAll students:");
            Set<Student> students = getAllStudents();
            for (Student s : students) {
                System.out.println("ID: " + s.getId() + ", Name: " + s.getFirstName() + " " + s.getLastName());
            }
            
            // Print all programmes
            System.out.println("\nAll programmes:");
            Set<Programme> programmes = getAllProgrammes();
            for (Programme p : programmes) {
                System.out.println("ID: " + p.getProgrammeId() + ", Name: " + p.getProgrammeName());
            }
            
            // Print student-programme enrollments
            System.out.println("\nStudent-Programme enrollments:");
            try {
                PreparedStatement stmt = connection.prepareStatement(
                    "SELECT s.id, s.first_name, s.last_name, p.programme_id, p.programme_name " +
                    "FROM students s " +
                    "JOIN student_programme sp ON s.id = sp.student_id " +
                    "JOIN programmes p ON sp.programme_id = p.programme_id"
                );
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    System.out.println("Student: " + rs.getString("id") + " (" + 
                                      rs.getString("first_name") + " " + rs.getString("last_name") + 
                                      ") enrolled in Programme: " + rs.getString("programme_id") + 
                                      " (" + rs.getString("programme_name") + ")");
                }
            } catch (SQLException e) {
                System.out.println("Error retrieving student-programme enrollments: " + e.getMessage());
            }
            
            System.out.println("--- END DATABASE STATE ---\n");
        } catch (SQLException e) {
            System.err.println("Error debugging database state: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
