package system;

import model.*;
import java.util.*;
import java.io.*;

public class FileStudentSystem implements StudentSystem {
    private List<Student> students;
    private List<Course> courses;
    private List<Programme> programmes;
    private static final String DATA_DIR = "data";

    public FileStudentSystem() {
        students = new ArrayList<>();
        courses = new ArrayList<>();
        programmes = new ArrayList<>();
        createDataDirectory();
        loadData();
    }

    private void createDataDirectory() {
        File directory = new File(DATA_DIR);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    private void loadData() {
        try {
            File studentFile = new File(DATA_DIR + "/students.txt");
            if (studentFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(studentFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    students.add(new Student(parts[0], parts[1], parts[2]));
                }
                reader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load data", e);
        }
    }

    @Override
    public void addStudent(Student student) {
        students.add(student);
        save();
    }

    @Override
    public void addCourse(Course course) {
        courses.add(course);
        save();
    }

    @Override
    public void addProgramme(Programme programme) {
        programmes.add(programme);
        save();
    }

    @Override
    public Student getStudent(String id) {
        return students.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    @Override
    public void save() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DIR + "/students.txt"));
            for (Student student : students) {
                writer.write(String.format("%s,%s,%s\n", 
                    student.getId(), student.getName(), student.getEmail()));
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save data", e);
        }
    }
}
