package system;

import model.*;
import java.util.*;
import java.io.*;
import java.util.stream.Collectors;

public class FileStudentSystem implements StudentSystem {
    private static final String DATA_DIR = "data";
    private static final String STUDENTS_FILE = DATA_DIR + "/students.csv";
    private static final String LECTURERS_FILE = DATA_DIR + "/lecturers.csv";
    private static final String COURSES_FILE = DATA_DIR + "/courses.csv";
    private static final String PROGRAMMES_FILE = DATA_DIR + "/programmes.csv";
    private static final String ENROLLMENTS_FILE = DATA_DIR + "/enrollments.csv";
    private static final String SCORES_FILE = DATA_DIR + "/scores.csv";

    private Map<String, Student> students;
    private Map<String, Lecturer> lecturers;
    private Map<String, Course> courses;
    private Map<String, Programme> programmes;

    public FileStudentSystem() {
        students = new HashMap<>();
        lecturers = new HashMap<>();
        courses = new HashMap<>();
        programmes = new HashMap<>();
        createDataDirectory();
        loadData();
    }

    private void createDataDirectory() {
        File directory = new File(DATA_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    @Override
    public void loadData() {
        try {
            loadStudents();
            loadLecturers();
            loadCourses();
            loadProgrammes();
            loadEnrollments();
            loadScores();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load data", e);
        }
    }

    private void loadStudents() throws IOException {
        File file = new File(STUDENTS_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    students.put(parts[0], new Student(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        }
    }

    private void loadLecturers() throws IOException {
        File file = new File(LECTURERS_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    lecturers.put(parts[0], new Lecturer(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        }
    }

    private void loadCourses() throws IOException {
        File file = new File(COURSES_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    Course course = new Course(parts[0], parts[1]);
                    if (parts.length > 2 && !parts[2].isEmpty()) {
                        Lecturer lecturer = lecturers.get(parts[2]);
                        course.setLecturer(lecturer);
                        if (lecturer != null) {
                            lecturer.assignCourse(course);
                        }
                    }
                    courses.put(parts[0], course);
                }
            }
        }
    }

    private void loadProgrammes() throws IOException {
        File file = new File(PROGRAMMES_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    programmes.put(parts[0], new Programme(parts[0], parts[1]));
                }
            }
        }
    }

    private void loadEnrollments() throws IOException {
        File file = new File(ENROLLMENTS_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    Student student = students.get(parts[0]);
                    if (parts[1].startsWith("P")) {
                        Programme programme = programmes.get(parts[1]);
                        if (student != null && programme != null) {
                            student.setEnrolledProgramme(programme);
                            programme.addStudent(student);
                        }
                    } else {
                        Course course = courses.get(parts[1]);
                        if (student != null && course != null) {
                            student.registerCourse(course);
                            course.addStudent(student);
                        }
                    }
                }
            }
        }
    }

    private void loadScores() throws IOException {
        File file = new File(SCORES_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    Student student = students.get(parts[0]);
                    Course course = courses.get(parts[1]);
                    if (student != null && course != null) {
                        student.setScore(course, Double.parseDouble(parts[2]));
                    }
                }
            }
        }
    }

    @Override
    public void saveData() {
        try {
            saveStudents();
            saveLecturers();
            saveCourses();
            saveProgrammes();
            saveStudentScores();
            saveEnrollments();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save data", e);
        }
    }

    private void saveStudents() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STUDENTS_FILE))) {
            for (Student student : students.values()) {
                writer.write(String.format("%s,%s,%s,%s\n",
                    student.getId(), student.getFirstName(), student.getLastName(), student.getEmail()));
            }
        }
    }

    private void saveCourses() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(COURSES_FILE))) {
            for (Course course : courses.values()) {
                writer.write(String.format("%s,%s,%s\n",
                    course.getCourseId(),
                    course.getCourseName(),
                    course.getLecturer() != null ? course.getLecturer().getId() : ""));
            }
        }
    }

    private void saveProgrammes() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROGRAMMES_FILE))) {
            for (Programme programme : programmes.values()) {
                writer.write(String.format("%s,%s\n",
                    programme.getProgrammeId(),
                    programme.getProgrammeName()));
            }
        }
    }

    private void saveStudentScores() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORES_FILE))) {
            for (Student student : students.values()) {
                Map<String, Double> results = student.getResults();
                for (Map.Entry<String, Double> entry : results.entrySet()) {
                    writer.write(String.format("%s,%s,%.2f\n",
                        student.getId(),
                        entry.getKey(),
                        entry.getValue()));
                }
            }
        }
    }

    private void saveEnrollments() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ENROLLMENTS_FILE))) {
            for (Student student : students.values()) {
                if (student.getEnrolledProgramme() != null) {
                    writer.write(String.format("%s,%s\n",
                        student.getId(),
                        student.getEnrolledProgramme().getProgrammeId()));
                }
                for (Course course : student.getRegisteredCourses()) {
                    writer.write(String.format("%s,%s\n",
                        student.getId(),
                        course.getCourseId()));
                }
            }
        }
    }

    private void saveLecturers() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LECTURERS_FILE))) {
            for (Lecturer lecturer : lecturers.values()) {
                writer.write(String.format("%s,%s,%s,%s\n",
                    lecturer.getId(),
                    lecturer.getFirstName(),
                    lecturer.getLastName(),
                    lecturer.getEmail()));
            }
        }
    }

    @Override
    public boolean addStudent(Student student) {
        if (students.containsKey(student.getId())) {
            return false;
        }
        students.put(student.getId(), student);
        saveData();
        return true;
    }

    @Override
    public boolean addLecturer(Lecturer lecturer) {
        if (lecturers.containsKey(lecturer.getId())) {
            return false;
        }
        lecturers.put(lecturer.getId(), lecturer);
        saveData();
        return true;
    }

    @Override
    public boolean addCourse(Course course) {
        if (courses.containsKey(course.getCourseId())) {
            return false;
        }
        courses.put(course.getCourseId(), course);
        saveData();
        return true;
    }

    @Override
    public boolean addProgramme(Programme programme) {
        if (programmes.containsKey(programme.getProgrammeId())) {
            return false;
        }
        programmes.put(programme.getProgrammeId(), programme);
        saveData();
        return true;
    }

    @Override
    public boolean enrollStudentInProgramme(String studentId, String programmeId) {
        Student student = students.get(studentId);
        Programme programme = programmes.get(programmeId);
        
        if (student == null || programme == null) {
            return false;
        }
        
        // Check if student is already enrolled in a programme
        if (student.getEnrolledProgramme() != null) {
            return false;
        }
        
        student.setEnrolledProgramme(programme);
        programme.addStudent(student);
        saveData();
        return true;
    }

    @Override
    public boolean registerStudentForCourse(String studentId, String courseId) {
        Student student = students.get(studentId);
        Course course = courses.get(courseId);
        
        if (student == null || course == null) {
            return false;
        }
        
        // Check if student already has maximum courses (3)
        if (student.getRegisteredCourses().size() >= 3) {
            return false;
        }
        
        if (student.registerCourse(course)) {
            course.addStudent(student);
            saveData();
            return true;
        }
        return false;
    }

    @Override
    public boolean updateStudentScore(String studentId, String courseId, double score) {
        Student student = students.get(studentId);
        Course course = courses.get(courseId);
        
        if (student == null || course == null) {
            return false;
        }
        
        // Check if student is registered for the course
        if (!student.getCourses().contains(course)) {
            return false;
        }
        
        // Update the score
        student.updateResult(courseId, score);
        
        // Save to file
        saveData();
        return true;
    }

    @Override
    public Student getStudent(String studentId) {
        return students.get(studentId);
    }

    @Override
    public Set<Student> getStudentsInCourse(String courseId) {
        Course course = courses.get(courseId);
        return course != null ? course.getEnrolledStudents() : new HashSet<>();
    }

    @Override
    public Set<Student> getStudentsInProgramme(String programmeId) {
        Programme programme = programmes.get(programmeId);
        return programme != null ? programme.getStudents() : new HashSet<>();
    }

    @Override
    public Programme getProgramme(String programmeId) {
        return programmes.get(programmeId);
    }

    @Override
    public Lecturer getLecturerByCourse(String courseId) {
        Course course = courses.get(courseId);
        return course != null ? course.getLecturer() : null;
    }

    @Override
    public boolean assignCourseToLecturer(String lecturerId, String courseId) {
        Lecturer lecturer = lecturers.get(lecturerId);
        Course course = courses.get(courseId);
        
        if (lecturer == null || course == null) {
            return false;
        }
        
        if (course.getLecturer() != null) {
            return false;
        }
        
        if (lecturer.getAssignedCourses().size() >= 2) {
            return false;
        }
        
        course.setLecturer(lecturer);
        lecturer.assignCourse(course);
        saveData();
        return true;
    }

    @Override
    public Set<Course> getCoursesByLecturer(String lecturerId) {
        Lecturer lecturer = lecturers.get(lecturerId);
        return lecturer != null ? lecturer.getAssignedCourses() : new HashSet<>();
    }

    @Override
    public boolean addCourseToProgramme(String courseId, String programmeId) {
        Course course = courses.get(courseId);
        Programme programme = programmes.get(programmeId);
        
        if (course == null || programme == null) {
            return false;
        }
        
        boolean success = programme.addCourse(course);
        if (success) {
            saveData();
        }
        return success;
    }

    @Override
    public Map<String, Double> getStudentResults(String studentId) {
        Student student = getStudent(studentId);
        return student != null ? student.getResults() : new HashMap<>();
    }

    @Override
    public Course getCourse(String courseId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(COURSES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(courseId)) {
                    return new Course(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read courses file", e);
        }
        return null;
    }

    @Override
    public Lecturer getLecturer(String lecturerId) {
        return lecturers.get(lecturerId);
    }

    @Override
    public boolean assignLecturerToCourse(String lecturerId, String courseId) {
        Lecturer lecturer = lecturers.get(lecturerId);
        Course course = courses.get(courseId);
        
        if (lecturer == null || course == null) {
            return false;
        }
        
        // Check if course already has a lecturer (no sharing)
        if (course.getLecturer() != null) {
            return false;
        }
        
        // Check if lecturer already has maximum courses (2)
        if (lecturer.getAssignedCourses().size() >= 2) {
            return false;
        }
        
        lecturer.assignCourse(course);
        course.setLecturer(lecturer);
        
        saveData();
        return true;
    }

    @Override
    public Set<Student> getAllStudents() {
        return new HashSet<>(students.values());
    }

    @Override
    public Set<Course> getAllCourses() {
        return new HashSet<>(courses.values());
    }

    @Override
    public Set<Programme> getAllProgrammes() {
        return new HashSet<>(programmes.values());
    }

    @Override
    public Set<Lecturer> getAllLecturers() {
        return new HashSet<>(lecturers.values());
    }
}
