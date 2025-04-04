package model;

import java.util.HashSet;
import java.util.Set;

public class Programme {
    private String programmeId;
    private String programmeName;
    private Set<Course> courses;
    private Set<Student> enrolledStudents;

    public Programme(String programmeId, String programmeName) {
        this.programmeId = programmeId;
        this.programmeName = programmeName;
        this.courses = new HashSet<>();
        this.enrolledStudents = new HashSet<>();
    }

    public String getProgrammeId() { return programmeId; }
    public void setProgrammeId(String programmeId) { this.programmeId = programmeId; }

    public String getProgrammeName() { return programmeName; }
    public void setProgrammeName(String programmeName) { this.programmeName = programmeName; }

    public boolean addCourse(Course course) {
        return courses.add(course);
    }

    public boolean removeCourse(Course course) {
        return courses.remove(course);
    }

    public Set<Course> getCourses() {
        return new HashSet<>(courses);
    }

    public Set<Student> getStudents() {
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
        return String.format("%s,%s", programmeId, programmeName);
    }

    public String getName() {
        return getProgrammeName();
    }

    public String getCode() {
        return getProgrammeId();
    }
}
