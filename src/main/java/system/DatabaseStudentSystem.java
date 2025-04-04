package system;

import model.*;
import java.sql.*;
import java.util.*;
import java.io.*;
import java.util.Properties;

public class DatabaseStudentSystem implements StudentSystem {
    private Connection connection;

    public DatabaseStudentSystem() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("src/main/resources/db.properties"));
            
            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");
            
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    @Override
    public void addStudent(Student student) {
        try {
            System.out.println("Adding student - ID: " + student.getId() + 
                             ", Name: " + student.getName() + 
                             ", Email: " + student.getEmail());
            
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO students (id, name, email) VALUES (?, ?, ?)"
            );
            stmt.setString(1, student.getId());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getEmail());
            
            int result = stmt.executeUpdate();
            System.out.println("Student insert result: " + result);
        } catch (SQLException e) {
            System.err.println("SQL Error in addStudent: " + e.getMessage());
            throw new RuntimeException("Failed to add student", e);
        }
    }

    @Override
    public void addCourse(Course course) {
        try {
            System.out.println("Adding course - Code: " + course.getCode() + 
                             ", Name: " + course.getName());
            
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO courses (code, name) VALUES (?, ?)"
            );
            stmt.setString(1, course.getCode());
            stmt.setString(2, course.getName());
            
            int result = stmt.executeUpdate();
            System.out.println("Course insert result: " + result);
        } catch (SQLException e) {
            System.err.println("SQL Error in addCourse: " + e.getMessage());
            throw new RuntimeException("Failed to add course", e);
        }
    }

    @Override
    public void addProgramme(Programme programme) {
        try {
            System.out.println("Adding programme - Code: " + programme.getCode() + 
                             ", Name: " + programme.getName());
            
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO programmes (code, name) VALUES (?, ?)"
            );
            stmt.setString(1, programme.getCode());
            stmt.setString(2, programme.getName());
            
            int result = stmt.executeUpdate();
            System.out.println("Programme insert result: " + result);
        } catch (SQLException e) {
            System.err.println("SQL Error in addProgramme: " + e.getMessage());
            throw new RuntimeException("Failed to add programme", e);
        }
    }

    @Override
    public Student getStudent(String id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT s.*, p.code as prog_code, p.name as prog_name " +
                "FROM students s " +
                "LEFT JOIN student_programme sp ON s.id = sp.student_id " +
                "LEFT JOIN programmes p ON sp.programme_code = p.code " +
                "WHERE s.id = ?");
            stmt.setString(1, id);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Student student = new Student(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("email")
                );
                
                String progCode = rs.getString("prog_code");
                if (progCode != null) {
                    student.setProgramme(new Programme(
                        progCode,
                        rs.getString("prog_name")
                    ));
                }
                
                loadStudentCourses(student);
                return student;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve student", e);
        }
    }

    @Override
    public List<Student> getAllStudents() {
        try {
            List<Student> students = new ArrayList<>();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, name, email FROM students");
            
            while (rs.next()) {
                students.add(new Student(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("email")
                ));
            }
            return students;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve students", e);
        }
    }

    private void loadStudentCourses(Student student) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
            "SELECT c.*, sc.score FROM courses c " +
            "JOIN student_course sc ON c.code = sc.course_code " +
            "WHERE sc.student_id = ?");
        stmt.setString(1, student.getId());
        
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Course course = new Course(
                rs.getString("code"),
                rs.getString("name")
            );
            student.getCourses().add(course);
            student.getResults().put(course.getCode(), rs.getDouble("score"));
        }
    }

    @Override
    public void save() {
        // Not needed for database implementation as saves are immediate
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void registerStudentToProgramme(String studentId, String programmeCode) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO student_programme (student_id, programme_code) VALUES (?, ?)");
            stmt.setString(1, studentId);
            stmt.setString(2, programmeCode);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to register student to programme", e);
        }
    }

    @Override
    public void registerStudentToCourse(String studentId, String courseCode) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO student_course (student_id, course_code) VALUES (?, ?)");
            stmt.setString(1, studentId);
            stmt.setString(2, courseCode);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to register student to course", e);
        }
    }

    @Override
    public void assignCourseScore(String studentId, String courseCode, double score) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE student_course SET score = ? WHERE student_id = ? AND course_code = ?");
            stmt.setDouble(1, score);
            stmt.setString(2, studentId);
            stmt.setString(3, courseCode);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to assign course score", e);
        }
    }

    @Override
    public Programme getProgramme(String code) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM programmes WHERE code = ?");
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Programme(
                    rs.getString("code"),
                    rs.getString("name")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve programme", e);
        }
    }

    @Override
    public Course getCourse(String code) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM courses WHERE code = ?");
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Course(
                    rs.getString("code"),
                    rs.getString("name")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve course", e);
        }
    }
}
