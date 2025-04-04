package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import model.*;
import system.StudentSystem;
import java.util.Map;

public class ResultPanel extends JPanel {
    private StudentSystem system;
    private JTextField studentIdField, courseIdField, scoreField;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    
    public ResultPanel(StudentSystem system) {
        this.system = system;
        setLayout(new BorderLayout(10, 10));
        
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        studentIdField = new JTextField();
        courseIdField = new JTextField();
        scoreField = new JTextField();
        
        inputPanel.add(new JLabel("Student ID:"));
        inputPanel.add(studentIdField);
        inputPanel.add(new JLabel("Course ID:"));
        inputPanel.add(courseIdField);
        inputPanel.add(new JLabel("Score:"));
        inputPanel.add(scoreField);
        
        JButton addButton = new JButton("Record Score");
        addButton.addActionListener(e -> recordScore());
        JButton viewButton = new JButton("View Student Results");
        viewButton.addActionListener(e -> viewResults());
        
        inputPanel.add(addButton);
        inputPanel.add(viewButton);
        
        // Results Table
        String[] columns = {"Course", "Score"};
        tableModel = new DefaultTableModel(columns, 0);
        resultsTable = new JTable(tableModel);
        
        // Add components to main panel
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultsTable), BorderLayout.CENTER);
        
        // Add some padding
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    private void recordScore() {
        try {
            String studentId = studentIdField.getText().trim();
            String courseId = courseIdField.getText().trim();
            String scoreText = scoreField.getText().trim();
            
            if (studentId.isEmpty() || courseId.isEmpty() || scoreText.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please fill in all fields", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double score;
            try {
                score = Double.parseDouble(scoreText);
                if (score < 0 || score > 100) {
                    JOptionPane.showMessageDialog(this, 
                        "Score must be between 0 and 100", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Score must be a valid number", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean success = system.updateStudentScore(studentId, courseId, score);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Score recorded successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                viewResults(); // Refresh the results table
            } else {
                // Check specific reason for failure
                Student student = system.getStudent(studentId);
                Course course = system.getCourse(courseId);
                
                if (student == null) {
                    JOptionPane.showMessageDialog(this, 
                        "Student not found!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                } else if (course == null) {
                    JOptionPane.showMessageDialog(this, 
                        "Course not found!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Student is not registered for this course!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewResults() {
        // Clear the table
        tableModel.setRowCount(0);
        
        String studentId = studentIdField.getText().trim();
        if (studentId != null && !studentId.isEmpty()) {
            Student student = system.getStudent(studentId);
            
            if (student != null) {
                StringBuilder result = new StringBuilder();
                result.append("Results for ").append(student.getName()).append(":\n\n");
                
                Map<String, Double> scores = system.getStudentResults(studentId);
                if (scores.isEmpty()) {
                    result.append("No results recorded yet.");
                } else {
                    for (Map.Entry<String, Double> entry : scores.entrySet()) {
                        Course course = system.getCourse(entry.getKey());
                        String courseName = course != null ? course.getCourseName() : entry.getKey();
                        
                        // Add to table
                        tableModel.addRow(new Object[]{courseName, entry.getValue()});
                    }
                    
                    // Calculate average score
                    double average = scores.values().stream().mapToDouble(Double::doubleValue).average().orElse(0);
                    tableModel.addRow(new Object[]{"Average", String.format("%.2f", average)});
                }
            } else {
                JOptionPane.showMessageDialog(this, "Student not found!");
            }
        }
    }
    
    private void clearFields() {
        studentIdField.setText("");
        courseIdField.setText("");
        scoreField.setText("");
    }
}
