package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student extends Person {
    private Programme programme;
    private List<Course> courses;
    private Map<String, Double> results;

    public Student(String id, String name, String email) {
        super(id, name, email);
        this.courses = new ArrayList<>();
        this.results = new HashMap<>();
    }

    public Programme getProgramme() { return programme; }
    public void setProgramme(Programme programme) { this.programme = programme; }
    public List<Course> getCourses() { return courses; }
    public Map<String, Double> getResults() { return results; }
}
