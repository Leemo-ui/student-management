package ui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import system.StudentSystem;
import model.Lecturer;
import model.Course;

public class AssignLecturerPanel extends JPanel {
    private StudentSystem system;
    private JComboBox<String> lecturerCombo, courseCombo;
    private Map<String, String> lecturerMap; // Maps display name to ID
    private Map<String, String> courseMap;   // Maps display name to ID
    
    public AssignLecturerPanel(StudentSystem system) {
        this.system = system;
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        
        // Create lecturer dropdown
        Vector<String> lecturerItems = new Vector<>();
        lecturerMap = new HashMap<>();
        
        // Populate lecturer dropdown
        for (Lecturer lecturer : getAllLecturers()) {
            String displayName = lecturer.getId() + " - " + lecturer.getName();
            lecturerItems.add(displayName);
            lecturerMap.put(displayName, lecturer.getId());
        }
        lecturerCombo = new JComboBox<>(lecturerItems);
        
        // Create course dropdown
        Vector<String> courseItems = new Vector<>();
        courseMap = new HashMap<>();
        
        // Populate course dropdown
        for (Course course : getUnassignedCourses()) {
            String displayName = course.getCourseId() + " - " + course.getCourseName();
            courseItems.add(displayName);
            courseMap.put(displayName, course.getCourseId());
        }
        courseCombo = new JComboBox<>(courseItems);
        
        // Add refresh button
        JButton refreshButton = new JButton("Refresh Lists");
        refreshButton.addActionListener(e -> refreshDropdowns());
        
        formPanel.add(new JLabel("Lecturer:"));
        formPanel.add(lecturerCombo);
        formPanel.add(new JLabel("Course:"));
        formPanel.add(courseCombo);
        
        JButton assignButton = new JButton("Assign Lecturer");
        assignButton.addActionListener(e -> assignLecturer());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        buttonPanel.add(assignButton);
        
        formPanel.add(new JLabel(""));
        formPanel.add(buttonPanel);
        
        add(formPanel, BorderLayout.NORTH);
    }
    
    private void refreshDropdowns() {
        // Refresh lecturer dropdown
        lecturerCombo.removeAllItems();
        lecturerMap.clear();
        for (Lecturer lecturer : getAllLecturers()) {
            String displayName = lecturer.getId() + " - " + lecturer.getName();
            lecturerCombo.addItem(displayName);
            lecturerMap.put(displayName, lecturer.getId());
        }
        
        // Refresh course dropdown
        courseCombo.removeAllItems();
        courseMap.clear();
        for (Course course : getUnassignedCourses()) {
            String displayName = course.getCourseId() + " - " + course.getCourseName();
            courseCombo.addItem(displayName);
            courseMap.put(displayName, course.getCourseId());
        }
    }
    
    private void assignLecturer() {
        if (lecturerCombo.getSelectedItem() == null || courseCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select both a lecturer and a course");
            return;
        }
        
        String lecturerId = lecturerMap.get(lecturerCombo.getSelectedItem().toString());
        String courseId = courseMap.get(courseCombo.getSelectedItem().toString());
        
        boolean success = system.assignLecturerToCourse(lecturerId, courseId);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Lecturer assigned to course successfully!");
            refreshDropdowns(); // Refresh to update the unassigned courses list
        } else {
            // Check specific reason for failure
            Lecturer lecturer = system.getLecturer(lecturerId);
            Course course = system.getCourse(courseId);
            
            if (lecturer == null) {
                JOptionPane.showMessageDialog(this, "Lecturer not found!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (course == null) {
                JOptionPane.showMessageDialog(this, "Course not found!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (course.getLecturer() != null) {
                JOptionPane.showMessageDialog(this, 
                    "Course already has a lecturer assigned!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } else if (lecturer.getAssignedCourses().size() >= 2) {
                JOptionPane.showMessageDialog(this, 
                    "Lecturer already assigned to maximum number of courses (2)!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Assignment failed for unknown reason.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Helper methods
    private Set<Lecturer> getAllLecturers() {
        // This should be implemented in the StudentSystem interface
        return system.getAllLecturers();
    }
    
    private Set<Course> getUnassignedCourses() {
        Set<Course> allCourses = system.getAllCourses();
        Set<Course> unassignedCourses = new HashSet<>();
        
        for (Course course : allCourses) {
            if (course.getLecturer() == null) {
                unassignedCourses.add(course);
            }
        }
        
        return unassignedCourses;
    }
}