package ui;

import system.FileStudentSystem;
import system.DatabaseStudentSystem;
import model.*;
import java.util.Scanner;
import java.util.Map;

public class ConsoleUI {
    private Scanner scanner;
    private FileStudentSystem fileSystem;
    private DatabaseStudentSystem dbSystem;
    private boolean useDatabase;

    public ConsoleUI(boolean useDatabase) {
        this.scanner = new Scanner(System.in);
        this.useDatabase = useDatabase;
        
        if (useDatabase) {
            this.dbSystem = new DatabaseStudentSystem();
        } else {
            this.fileSystem = new FileStudentSystem();
        }
    }

    public void start() {
        System.out.println("Student Management System initialized.");
        while (true) {
            printMenu();
            int choice = getIntInput("Enter your choice: ");
            
            try {
                switch (choice) {
                    case 1:
                        addStudent();
                        break;
                    case 2:
                        addCourse();
                        break;
                    case 3:
                        addProgramme();
                        break;
                    case 4:
                        registerStudent();
                        break;
                    case 0:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== Student Management System ===");
        System.out.println("1. Add Student");
        System.out.println("2. Add Course");
        System.out.println("3. Add Programme");
        System.out.println("4. Register Student");
        System.out.println("0. Exit");
    }

    private int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // Clear the buffer
        return value;
    }

    private String getString(String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim(); // Add trim to clean input
        while (value.isEmpty()) {
            System.out.print("Value cannot be empty. " + prompt);
            value = scanner.nextLine().trim();
        }
        return value;
    }

    private void addStudent() {
        System.out.println("\nAdding new student");
        String id = getString("Enter student ID: ");
        String name = getString("Enter student name: ");
        String email = getString("Enter student email: ");
        
        try {
            Student student = new Student(id, name, email);
            if (useDatabase) {
                dbSystem.addStudent(student);
                System.out.println("Student added successfully to database!");
            } else {
                fileSystem.addStudent(student);
                System.out.println("Student added successfully to file system!");
            }
        } catch (Exception e) {
            System.err.println("Failed to add student: " + e.getMessage());
        }
    }

    private void addCourse() {
        String code = getString("Enter course code: ");
        String name = getString("Enter course name: ");
        
        Course course = new Course(code, name);
        if (useDatabase) {
            dbSystem.addCourse(course);
        } else {
            fileSystem.addCourse(course);
        }
        System.out.println("Course added successfully!");
    }

    private void addProgramme() {
        String code = getString("Enter programme code: ");
        String name = getString("Enter programme name: ");
        
        Programme programme = new Programme(code, name);
        if (useDatabase) {
            dbSystem.addProgramme(programme);
        } else {
            fileSystem.addProgramme(programme);
        }
        System.out.println("Programme added successfully!");
    }

    private void registerStudent() {
        String studentId = getString("Enter student ID: ");
        String programmeCode = getString("Enter programme code: ");
        
        try {
            if (useDatabase) {
                Student student = dbSystem.getStudent(studentId);
                Programme programme = dbSystem.getProgramme(programmeCode);
                
                if (student == null || programme == null) {
                    System.out.println("Student or programme not found!");
                    return;
                }
                
                dbSystem.registerStudentToProgramme(studentId, programmeCode);
                System.out.println("Student registered to programme successfully!");
            } else {
                // File system implementation
                System.out.println("Registration not supported in file system mode.");
            }
        } catch (Exception e) {
            System.err.println("Registration failed: " + e.getMessage());
        }
    }

    private void registerStudentToCourse() {
        String studentId = getString("Enter student ID: ");
        String courseCode = getString("Enter course code: ");
        
        try {
            if (useDatabase) {
                Student student = dbSystem.getStudent(studentId);
                Course course = dbSystem.getCourse(courseCode);
                
                if (student == null || course == null) {
                    System.out.println("Student or course not found!");
                    return;
                }
                
                dbSystem.registerStudentToCourse(studentId, courseCode);
                System.out.println("Student registered to course successfully!");
            } else {
                System.out.println("Registration not supported in file system mode.");
            }
        } catch (Exception e) {
            System.err.println("Course registration failed: " + e.getMessage());
        }
    }
}
