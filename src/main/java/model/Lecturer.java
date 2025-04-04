package model;

import java.util.ArrayList;
import java.util.List;

public class Lecturer extends Person {
    private List<Course> courses;
    private String department;

    public Lecturer(String id, String name, String email, String department) {
        super(id, name, email);
        this.department = department;
        this.courses = new ArrayList<>();
    }

    public List<Course> getCourses() { return courses; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
