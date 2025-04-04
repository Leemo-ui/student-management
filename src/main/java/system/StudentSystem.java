package system;

import model.*;
import java.util.*;

public interface StudentSystem {
    // Student operations
    boolean addStudent(Student student);
    Student getStudent(String studentId);
    boolean enrollStudentInProgramme(String studentId, String programmeId);
    boolean registerStudentForCourse(String studentId, String courseId);
    boolean updateStudentScore(String studentId, String courseId, double score);
    Set<Student> getStudentsInCourse(String courseId);
    Set<Student> getStudentsInProgramme(String programmeId);
    Map<String, Double> getStudentResults(String studentId);

    // Lecturer operations
    boolean addLecturer(Lecturer lecturer);
    Lecturer getLecturer(String lecturerId);
    boolean assignLecturerToCourse(String lecturerId, String courseId);
    Set<Course> getCoursesByLecturer(String lecturerId);

    // Course operations
    boolean addCourse(Course course);
    Course getCourse(String courseId);
    boolean addCourseToProgramme(String programmeId, String courseId);
    
    // Programme operations
    boolean addProgramme(Programme programme);
    Programme getProgramme(String programmeId);

    // Data operations
    void saveData();
    void loadData();
    
    // Collection operations
    Set<Student> getAllStudents();
    Set<Course> getAllCourses();
    Set<Programme> getAllProgrammes();
    Set<Lecturer> getAllLecturers();
}
