package ui;

import javax.swing.*;
import java.awt.*;
import model.Programme;
import system.StudentSystem;

public class ProgrammePanel extends JPanel {
    private StudentSystem system;
    private JTextField codeField, nameField;
    private JTable courseTable;
    
    public ProgrammePanel(StudentSystem system) {
        this.system = system;
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        codeField = new JTextField();
        nameField = new JTextField();
        
        formPanel.add(new JLabel("Programme Code:"));
        formPanel.add(codeField);
        formPanel.add(new JLabel("Programme Name:"));
        formPanel.add(nameField);
        
        JButton addButton = new JButton("Add Programme");
        addButton.addActionListener(e -> addProgramme());
        formPanel.add(addButton);
        
        add(formPanel, BorderLayout.NORTH);
        
        initializeCoursePanel();
    }
    
    private void addProgramme() {
        try {
            Programme programme = new Programme(
                codeField.getText(),
                nameField.getText()
            );
            system.addProgramme(programme);
            JOptionPane.showMessageDialog(this, "Programme added successfully!");
            clearFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error adding programme: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearFields() {
        codeField.setText("");
        nameField.setText("");
    }
    
    private void initializeCoursePanel() {
        JPanel coursePanel = new JPanel(new BorderLayout());
        courseTable = new JTable();
        JButton addCourseButton = new JButton("Add Course to Programme");
        addCourseButton.addActionListener(e -> addCourseToProgramme());
        
        coursePanel.add(new JScrollPane(courseTable), BorderLayout.CENTER);
        coursePanel.add(addCourseButton, BorderLayout.SOUTH);
        
        add(coursePanel, BorderLayout.CENTER);
    }
    
    private void addCourseToProgramme() {
        String progCode = JOptionPane.showInputDialog("Enter Programme Code:");
        String courseCode = JOptionPane.showInputDialog("Enter Course Code:");
        
        try {
            system.addCourseToProgramme(progCode, courseCode);
            JOptionPane.showMessageDialog(this, "Course added to programme successfully!");
            refreshCourseTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void refreshCourseTable() {
        // Implement refresh logic
    }
}
