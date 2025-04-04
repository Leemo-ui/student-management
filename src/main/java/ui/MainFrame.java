package ui;

import javax.swing.*;
import java.awt.*;
import system.StudentSystem;
import system.FileStudentSystem;
import system.DatabaseStudentSystem;

public class MainFrame extends JFrame {
    private StudentSystem system;
    
    public MainFrame(boolean useDatabase) {
        super("Student Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        
        // Initialize the system
        system = useDatabase ? new DatabaseStudentSystem() : new FileStudentSystem();
        
        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Students", new StudentPanel(system));
        tabbedPane.addTab("Lecturers", new LecturerPanel(system));
        tabbedPane.addTab("Courses", new CoursePanel(system));
        tabbedPane.addTab("Programmes", new ProgrammePanel(system));
        tabbedPane.addTab("Registration", new RegistrationPanel(system));
        tabbedPane.addTab("Assign Lecturer", new AssignLecturerPanel(system));
        tabbedPane.addTab("Search", new SearchPanel(system));
        
        add(tabbedPane, BorderLayout.CENTER);
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }
}
