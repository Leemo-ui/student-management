package ui;

import javax.swing.*;
import java.awt.*;
import model.Course;
import system.StudentSystem;

public class CoursePanel extends JPanel {
    private StudentSystem system;
    private JTextField codeField, nameField;
    private JTable courseTable;
    
    public CoursePanel(StudentSystem system) {
        this.system = system;
        setLayout(new BorderLayout());
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        codeField = new JTextField();
        nameField = new JTextField();
        
        formPanel.add(new JLabel("Course Code:"));
        formPanel.add(codeField);
        formPanel.add(new JLabel("Course Name:"));
        formPanel.add(nameField);
        
        // Buttons
        JButton addButton = new JButton("Add Course");
        addButton.addActionListener(e -> addCourse());
        
        formPanel.add(addButton);
        
        add(formPanel, BorderLayout.NORTH);
    }
    
    private void addCourse() {
        try {
            Course course = new Course(
                codeField.getText(),
                nameField.getText()
            );
            system.addCourse(course);
            JOptionPane.showMessageDialog(this, "Course added successfully!");
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error adding course: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearFields() {
        codeField.setText("");
        nameField.setText("");
    }
}
