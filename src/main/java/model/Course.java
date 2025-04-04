package model;

import java.util.HashSet;
import java.util.Set;

public class Course {
    private String courseId;
    private String courseName;
    private Lecturer lecturer;
    private Set<Student> enrolledStudents;

    public Course(String courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.enrolledStudents = new HashSet<>();
    }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Lecturer getLecturer() { return lecturer; }
    public void setLecturer(Lecturer lecturer) { this.lecturer = lecturer; }

    public Set<Student> getEnrolledStudents() {
        return new HashSet<>(enrolledStudents);
    }

    public boolean addStudent(Student student) {
        return enrolledStudents.add(student);
    }

    public boolean removeStudent(Student student) {
        return enrolledStudents.remove(student);
    }

    @Override
    public String toString() {
        return String.format("%s,%s", courseId, courseName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;
        return courseId.equals(course.courseId);
    }

    @Override
    public int hashCode() {
        return courseId.hashCode();
    }

    public String getName() {
        return getCourseName();
    }

    public String getCode() {
        return getCourseId();
    }
}
