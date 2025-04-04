package ui;

import javax.swing.*;
import java.awt.*;
import system.StudentSystem;
import model.Student;
import model.Course;

public class RegistrationPanel extends JPanel {
    private StudentSystem system;
    private JTextField studentIdField, courseCodeField, programmeCodeField;
    
    public RegistrationPanel(StudentSystem system) {
        this.system = system;
        setLayout(new BorderLayout());
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Course Registration", createCourseRegistrationPanel());
        tabbedPane.addTab("Programme Enrollment", createProgrammeEnrollmentPanel());
        
        // Add debug button
        JButton debugButton = new JButton("Debug Database");
        debugButton.addActionListener(e -> {
            if (system instanceof system.DatabaseStudentSystem) {
                ((system.DatabaseStudentSystem) system).debugDatabaseState();
            }
        });
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(debugButton);
        
        add(tabbedPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createCourseRegistrationPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        studentIdField = new JTextField();
        courseCodeField = new JTextField();
        
        panel.add(new JLabel("Student ID:"));
        panel.add(studentIdField);
        panel.add(new JLabel("Course Code:"));
        panel.add(courseCodeField);
        
        JButton registerButton = new JButton("Register for Course");
        registerButton.addActionListener(e -> registerStudentForCourse());
        panel.add(registerButton);
        
        return panel;
    }
    
    private JPanel createProgrammeEnrollmentPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        programmeCodeField = new JTextField();
        
        panel.add(new JLabel("Student ID:"));
        panel.add(studentIdField);
        panel.add(new JLabel("Programme Code:"));
        panel.add(programmeCodeField);
        
        JButton enrollButton = new JButton("Enroll in Programme");
        enrollButton.addActionListener(e -> enrollProgramme());
        panel.add(enrollButton);
        
        return panel;
    }
    
    private void registerStudentForCourse() {
        String studentId = studentIdField.getText().trim();
        String courseId = courseCodeField.getText().trim();
        
        if (studentId.isEmpty() || courseId.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both student ID and course ID", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean success = system.registerStudentForCourse(studentId, courseId);
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Student registered for course successfully", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            studentIdField.setText("");
            courseCodeField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to register student for course", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void enrollProgramme() {
        String studentId = studentIdField.getText().trim();
        String programmeId = programmeCodeField.getText().trim();
        
        if (studentId.isEmpty() || programmeId.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both student ID and programme ID", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean success = system.enrollStudentInProgramme(studentId, programmeId);
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Student enrolled in programme successfully", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            studentIdField.setText("");
            programmeCodeField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to enroll student in programme", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearFields() {
        studentIdField.setText("");
        courseCodeField.setText("");
        programmeCodeField.setText("");
    }
}
