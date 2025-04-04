package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Student extends Person {
    private Programme enrolledProgramme;
    private Set<Course> registeredCourses;
    private Map<Course, Double> courseScores;

    public Student(String id, String firstName, String lastName, String email) {
        super(id, firstName, lastName, email);
        this.registeredCourses = new HashSet<>();
        this.courseScores = new HashMap<>();
    }

    public Programme getEnrolledProgramme() { return enrolledProgramme; }
    
    public void setEnrolledProgramme(Programme programme) {
        this.enrolledProgramme = programme;
    }

    public boolean registerCourse(Course course) {
        if (registeredCourses.size() >= 3) {
            return false;
        }
        return registeredCourses.add(course);
    }

    public boolean unregisterCourse(Course course) {
        return registeredCourses.remove(course);
    }

    public void setScore(Course course, double score) {
        if (registeredCourses.contains(course)) {
            courseScores.put(course, score);
        }
    }

    public Double getScore(Course course) {
        return courseScores.get(course);
    }

    public Set<Course> getRegisteredCourses() {
        return new HashSet<>(registeredCourses);
    }

    public Map<Course, Double> getCourseScores() {
        return new HashMap<>(courseScores);
    }

    public String getName() {
        return getFirstName() + " " + getLastName();
    }

    public Programme getProgramme() {
        return enrolledProgramme;
    }

    public void setProgramme(Programme programme) {
        this.enrolledProgramme = programme;
    }

    public Set<Course> getCourses() {
        return getRegisteredCourses();
    }

    public Map<String, Double> getResults() {
        Map<String, Double> results = new HashMap<>();
        for (Map.Entry<Course, Double> entry : courseScores.entrySet()) {
            results.put(entry.getKey().getCourseId(), entry.getValue());
        }
        return results;
    }
}
