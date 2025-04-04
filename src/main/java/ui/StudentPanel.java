package ui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import model.Student;
import model.Course;
import system.StudentSystem;

public class StudentPanel extends JPanel {
    private StudentSystem system;
    private JTextField idField, firstNameField, lastNameField, emailField;
    private JTable studentTable;
    private JButton addButton, viewButton, updateButton;
    
    public StudentPanel(StudentSystem system) {
        this.system = system;
        setLayout(new BorderLayout());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        
        idField = new JTextField();
        firstNameField = new JTextField();
        lastNameField = new JTextField();
        emailField = new JTextField();
        
        formPanel.add(new JLabel("Student ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        
        JButton addButton = new JButton("Add Student");
        addButton.addActionListener(e -> addStudent());
        
        formPanel.add(new JLabel(""));
        formPanel.add(addButton);
        
        // Add to main panel
        add(formPanel, BorderLayout.NORTH);
        
        // Add some padding
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table
        refreshStudentTable();
    }
    
    private void addStudent() {
        String id = idField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        
        if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all fields", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Student student = new Student(id, firstName, lastName, email);
        boolean success = system.addStudent(student);
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Student added successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            refreshStudentTable();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to add student. ID may already exist.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearFields() {
        idField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
    }
    
    private void refreshStudentTable() {
        // Implement table refresh logic
    }
    
    private void showStudentDetails(String studentId) {
        Student student = system.getStudent(studentId);
        if (student != null) {
            StringBuilder details = new StringBuilder();
            details.append("ID: ").append(student.getId()).append("\n");
            details.append("Name: ").append(student.getName()).append("\n");
            details.append("Email: ").append(student.getEmail()).append("\n");
            details.append("Programme: ")
                  .append(student.getProgramme() != null ? student.getProgramme().getName() : "None")
                  .append("\n\n");
            
            details.append("Courses and Results:\n");
            Map<String, Double> results = system.getStudentResults(studentId);
            for (Map.Entry<String, Double> entry : results.entrySet()) {
                Course course = system.getCourse(entry.getKey());
                String courseName = course != null ? course.getName() : entry.getKey();
                details.append(courseName).append(": ").append(entry.getValue()).append("\n");
            }
            
            JTextArea textArea = new JTextArea(details.toString());
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), 
                "Student Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Student not found!");
        }
    }
}
