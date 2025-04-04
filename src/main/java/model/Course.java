package model;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String code;
    private String name;
    private List<Student> students;
    private Lecturer lecturer;

    public Course(String code, String name) {
        this.code = code;
        this.name = name;
        this.students = new ArrayList<>();
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public List<Student> getStudents() { return students; }
    public Lecturer getLecturer() { return lecturer; }
    public void setLecturer(Lecturer lecturer) { this.lecturer = lecturer; }
}
