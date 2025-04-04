package ui;

import javax.swing.*;
import java.awt.*;
import model.*;
import system.StudentSystem;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

public class SearchPanel extends JPanel {
    private StudentSystem system;
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    private JTextArea resultArea;
    private JComboBox<String> courseComboBox;
    
    public SearchPanel(StudentSystem system) {
        this.system = system;
        setLayout(new BorderLayout(10, 10));
        
        // Search controls
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchField = new JTextField();
        searchTypeCombo = new JComboBox<>(new String[] {
            "Student by ID", 
            "Student by Programme", 
            "Course", 
            "Programme",
            "Lecturer",
            "Course Lecturer"
        });
        
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        
        searchPanel.add(searchTypeCombo, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        // Results area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        
        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);
    }
    
    private void performSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter search text");
            return;
        }
        
        String searchType = (String) searchTypeCombo.getSelectedItem();
        resultArea.setText("");
        
        switch (searchType) {
            case "Student by ID":
                searchStudentById(searchText);
                break;
            case "Student by Programme":
                searchStudentsByProgramme(searchText);
                break;
            case "Course":
                searchCourse(searchText);
                break;
            case "Programme":
                searchProgramme(searchText);
                break;
            case "Lecturer":
                searchLecturer(searchText);
                break;
            case "Course Lecturer":
                searchCourseLecturer(searchText);
                break;
        }
    }
    
    private void searchCourseLecturer(String courseCode) {
        Lecturer lecturer = system.getLecturer(courseCode);
        if (lecturer != null) {
            resultArea.setText("Lecturer for course " + courseCode + ":\n" +
                             "ID: " + lecturer.getId() + "\n" +
                             "Name: " + lecturer.getName() + "\n" +
                             "Email: " + lecturer.getEmail());
        } else {
            resultArea.setText("No lecturer found for course " + courseCode);
        }
    }
    
    private void searchStudentById(String id) {
        Student student = system.getStudent(id);
        if (student != null) {
            StringBuilder result = new StringBuilder();
            result.append("Student Details:\n");
            result.append("ID: ").append(student.getId()).append("\n");
            result.append("Name: ").append(student.getName()).append("\n");
            result.append("Email: ").append(student.getEmail()).append("\n");
            result.append("Programme: ").append(student.getProgramme() != null ? 
                student.getProgramme().getName() : "None").append("\n\n");
            
            result.append("Courses and Results:\n");
            Map<String, Double> results = student.getResults();
            if (results.isEmpty()) {
                result.append("No results recorded yet.");
            } else {
                for (Map.Entry<String, Double> entry : results.entrySet()) {
                    Course course = system.getCourse(entry.getKey());
                    String courseName = course != null ? course.getCourseName() : entry.getKey();
                    result.append(courseName)
                          .append(": ")
                          .append(entry.getValue())
                          .append("\n");
                }
                
                // Calculate average score
                double average = results.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
                result.append("\nAverage Score: ").append(String.format("%.2f", average));
            }
            
            // Add button to update results
            JButton updateButton = new JButton("Update Results");
            updateButton.addActionListener(e -> openUpdateResultsDialog(student));
            
            JTextArea textArea = new JTextArea(result.toString());
            textArea.setEditable(false);
            
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
            panel.add(updateButton, BorderLayout.SOUTH);
            
            resultArea.setText(result.toString());
            
            // Show option to update results
            int option = JOptionPane.showConfirmDialog(
                this,
                "Would you like to update this student's results?",
                "Update Results",
                JOptionPane.YES_NO_OPTION
            );
            
            if (option == JOptionPane.YES_OPTION) {
                openUpdateResultsDialog(student);
            }
        } else {
            resultArea.setText("Student not found");
        }
    }
    
    private void openUpdateResultsDialog(Student student) {
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Student not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Update Results", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        // Course input field
        panel.add(new JLabel("Enter Course ID:"));
        JTextField courseIdField = new JTextField();
        panel.add(courseIdField);
        
        // Score input field
        panel.add(new JLabel("Enter Score:"));
        JTextField scoreField = new JTextField();
        panel.add(scoreField);
        
        // Display registered courses for reference
        JTextArea coursesArea = new JTextArea(5, 20);
        coursesArea.setEditable(false);
        
        StringBuilder coursesText = new StringBuilder("Registered Courses:\n");
        for (Course course : student.getRegisteredCourses()) {
            coursesText.append(course.getCourseId())
                      .append(" - ")
                      .append(course.getCourseName())
                      .append("\n");
        }
        coursesArea.setText(coursesText.toString());
        
        JScrollPane scrollPane = new JScrollPane(coursesArea);
        
        // Update button
        JButton updateButton = new JButton("Update Score");
        updateButton.addActionListener(e -> {
            String courseId = courseIdField.getText().trim();
            if (courseId.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a course ID", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                double score = Double.parseDouble(scoreField.getText().trim());
                boolean success = system.updateStudentScore(student.getId(), courseId, score);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Score updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    // Refresh the search results
                    searchStudentById(student.getId());
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                        "Failed to update score. Please check if the student is registered for this course.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid score (numeric value)", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(updateButton);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void searchCourseStudents(String courseCode) {
        Set<Student> studentSet = system.getStudentsInCourse(courseCode);
        List<Student> students = new ArrayList<>(studentSet);
        StringBuilder result = new StringBuilder();
        result.append("Students enrolled in course ").append(courseCode).append(":\n\n");
        
        for (Student student : students) {
            result.append(student.getId())
                  .append(" - ")
                  .append(student.getName())
                  .append("\n");
        }
        
        resultArea.setText(result.toString());
    }

    private void searchStudentsByProgramme(String programmeCode) {
        Set<Student> studentSet = system.getStudentsInProgramme(programmeCode);
        List<Student> students = new ArrayList<>(studentSet);
        StringBuilder result = new StringBuilder();
        result.append("Students enrolled in programme ").append(programmeCode).append(":\n\n");
        
        if (students.isEmpty()) {
            result.append("No students found in this programme.");
        } else {
            for (Student student : students) {
                result.append(student.getId())
                      .append(" - ")
                      .append(student.getName())
                      .append("\n");
            }
        }
        
        resultArea.setText(result.toString());
    }

    private void searchProgramme(String programmeCode) {
        // Implementation needed
        resultArea.setText("Programme search not implemented");
    }

    private void searchLecturer(String lecturerId) {
        // Implementation needed
        resultArea.setText("Lecturer search not implemented");
    }

    private void searchCourse(String courseCode) {
        // Implementation needed
        resultArea.setText("Course search not implemented");
    }
}
