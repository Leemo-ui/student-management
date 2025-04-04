package system;

import model.*;
import java.util.List;

public interface StudentSystem {
    void addStudent(Student student);
    void addCourse(Course course);
    void addProgramme(Programme programme);
    Student getStudent(String id);
    List<Student> getAllStudents();
    void save();
    void registerStudentToProgramme(String studentId, String programmeCode);
    void registerStudentToCourse(String studentId, String courseCode);
    void assignCourseScore(String studentId, String courseCode, double score);
    Programme getProgramme(String code);
    Course getCourse(String code);
}
