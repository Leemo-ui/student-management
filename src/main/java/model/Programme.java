package model;

import java.util.ArrayList;
import java.util.List;

public class Programme {
    private String code;
    private String name;
    private List<Student> students;
    private List<Course> courses;

    public Programme(String code, String name) {
        this.code = code;
        this.name = name;
        this.students = new ArrayList<>();
        this.courses = new ArrayList<>();
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public List<Student> getStudents() { return students; }
    public List<Course> getCourses() { return courses; }
}
