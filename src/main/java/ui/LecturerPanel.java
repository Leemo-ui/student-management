package ui;

import javax.swing.*;
import java.awt.*;
import model.Lecturer;
import system.StudentSystem;

public class LecturerPanel extends JPanel {
    private StudentSystem system;
    private JTextField idField, firstNameField, lastNameField, emailField;
    
    public LecturerPanel(StudentSystem system) {
        this.system = system;
        setLayout(new BorderLayout());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        
        idField = new JTextField();
        firstNameField = new JTextField();
        lastNameField = new JTextField();
        emailField = new JTextField();
        
        formPanel.add(new JLabel("Lecturer ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        
        JButton addButton = new JButton("Add Lecturer");
        addButton.addActionListener(e -> addLecturer());
        
        formPanel.add(new JLabel(""));
        formPanel.add(addButton);
        
        // Add to main panel
        add(formPanel, BorderLayout.NORTH);
        
        // Add some padding
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    private void addLecturer() {
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
        
        Lecturer lecturer = new Lecturer(id, firstName, lastName, email);
        boolean success = system.addLecturer(lecturer);
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Lecturer added successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to add lecturer. ID may already exist.", 
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
}
