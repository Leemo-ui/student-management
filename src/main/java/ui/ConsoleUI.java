package ui;

import java.util.Scanner;
import java.util.Map;
import model.*;
import system.*;

public class ConsoleUI {
    private StudentSystem system;
    private Scanner scanner;
    private boolean running;

    public ConsoleUI(boolean useDatabase) {
        this.system = useDatabase ? new DatabaseStudentSystem() : new FileStudentSystem();
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    public void start() {
        System.out.println("Welcome to Student Management System");
        
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");
            processChoice(choice);
        }
        
        scanner.close();
    }

    private void displayMenu() {
        System.out.println("\n===== MENU =====");
        System.out.println("1. Add Student");
        System.out.println("2. Add Lecturer");
        System.out.println("3. Add Course");
        System.out.println("4. Add Programme");
        System.out.println("5. Register Student to Programme");
        System.out.println("6. Register Student to Course");
        System.out.println("7. Assign Lecturer to Course");
        System.out.println("8. Record Student Score");
        System.out.println("9. View Student Details");
        System.out.println("10. View Course Details");
        System.out.println("11. View Programme Details");
        System.out.println("12. View Lecturer Details");
        System.out.println("13. Save Data");
        System.out.println("14. Load Data");
        System.out.println("0. Exit");
        System.out.println("================");
    }

    private void processChoice(int choice) {
        switch (choice) {
            case 1:
                addStudent();
                break;
            case 2:
                addLecturer();
                break;
            case 3:
                addCourse();
                break;
            case 4:
                addProgramme();
                break;
            case 5:
                registerStudentToProgramme();
                break;
            case 6:
                registerStudentToCourse();
                break;
            case 7:
                assignLecturerToCourse();
                break;
            case 8:
                recordStudentScore();
                break;
            case 9:
                viewStudentDetails();
                break;
            case 10:
                viewCourseDetails();
                break;
            case 11:
                viewProgrammeDetails();
                break;
            case 12:
                viewLecturerDetails();
                break;
            case 13:
                system.saveData();
                System.out.println("Data saved successfully!");
                break;
            case 14:
                system.loadData();
                System.out.println("Data loaded successfully!");
                break;
            case 0:
                running = false;
                System.out.println("Exiting... Thank you for using Student Management System!");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    // Implementation of menu options
    private void addStudent() {
        String id = getStringInput("Enter student ID: ");
        String firstName = getStringInput("Enter first name: ");
        String lastName = getStringInput("Enter last name: ");
        String email = getStringInput("Enter email: ");
        
        Student student = new Student(id, firstName, lastName, email);
        boolean success = system.addStudent(student);
        
        if (success) {
            System.out.println("Student added successfully!");
        } else {
            System.out.println("Failed to add student. ID may already exist.");
        }
    }

    private void addLecturer() {
        String id = getStringInput("Enter lecturer ID: ");
        String firstName = getStringInput("Enter first name: ");
        String lastName = getStringInput("Enter last name: ");
        String email = getStringInput("Enter email: ");
        
        Lecturer lecturer = new Lecturer(id, firstName, lastName, email);
        boolean success = system.addLecturer(lecturer);
        
        if (success) {
            System.out.println("Lecturer added successfully!");
        } else {
            System.out.println("Failed to add lecturer. ID may already exist.");
        }
    }

    // Helper methods
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
    
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    // Additional methods for other menu options would be implemented here
    private void addCourse() {
        // Implementation
    }
    
    private void addProgramme() {
        // Implementation
    }
    
    private void registerStudentToProgramme() {
        // Implementation
    }
    
    private void registerStudentToCourse() {
        // Implementation
    }
    
    private void assignLecturerToCourse() {
        // Implementation
    }
    
    private void recordStudentScore() {
        // Implementation
    }
    
    private void viewStudentDetails() {
        // Implementation
    }
    
    private void viewCourseDetails() {
        // Implementation
    }
    
    private void viewProgrammeDetails() {
        // Implementation
    }
    
    private void viewLecturerDetails() {
        // Implementation
    }
}

