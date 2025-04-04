package model;

import java.util.HashSet;
import java.util.Set;

public class Lecturer extends Person {
    private Set<Course> assignedCourses;

    public Lecturer(String id, String firstName, String lastName, String email) {
        super(id, firstName, lastName, email);
        this.assignedCourses = new HashSet<>();
    }

    public boolean assignCourse(Course course) {
        if (assignedCourses.size() >= 2) {
            return false;
        }
        return assignedCourses.add(course);
    }

    public boolean removeCourse(Course course) {
        return assignedCourses.remove(course);
    }

    public Set<Course> getAssignedCourses() {
        return new HashSet<>(assignedCourses);
    }

    public String getName() {
        return getFirstName() + " " + getLastName();
    }

    public String getDepartment() {
        return "Faculty";
    }
}
