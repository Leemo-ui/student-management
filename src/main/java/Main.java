import ui.ConsoleUI;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Welcome to Student Management System");
            System.out.println("==================================");
            System.out.println("\nChoose storage system:");
            System.out.println("1. File-Based System");
            System.out.println("2. Database System");
            
            Scanner scanner = new Scanner(System.in);
            int choice;
            do {
                System.out.print("\nEnter choice (1 or 2): ");
                while (!scanner.hasNextInt()) {
                    System.out.println("Please enter a valid number.");
                    scanner.next();
                }
                choice = scanner.nextInt();
            } while (choice != 1 && choice != 2);
            
            boolean useDatabase = (choice == 2);
            ConsoleUI ui = new ConsoleUI(useDatabase);
            ui.start();
            
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
