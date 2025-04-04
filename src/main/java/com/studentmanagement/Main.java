package com.studentmanagement;

import ui.ConsoleUI;
import ui.MainFrame;
import javax.swing.*;
import java.sql.SQLException;

public class Main {
    private static final String USAGE = 
        "Usage: java -jar student-system.jar [options]\n" +
        "Options:\n" +
        "  --console    Start in console mode\n" +
        "  --gui        Start with graphical interface (default)\n" +
        "  --db         Use database storage\n" +
        "  --file       Use file storage (default)";

    public static void main(String[] args) {
        boolean useConsole = false;
        final boolean[] useDatabase = {false};

        // Check if command line args were provided
        if (args.length > 0) {
            // Parse command line arguments
            for (String arg : args) {
                switch (arg) {
                    case "--console":
                        useConsole = true;
                        break;
                    case "--gui":
                        useConsole = false;
                        break;
                    case "--db":
                        useDatabase[0] = true;
                        break;
                    case "--file":
                        useDatabase[0] = false;
                        break;
                    case "--help":
                        System.out.println(USAGE);
                        return;
                    default:
                        System.err.println("Unknown option: " + arg);
                        System.out.println(USAGE);
                        return;
                }
            }
        } else {
            // No args provided, show UI for selection
            String[] options = {"Console UI", "Graphical UI"};
            int uiChoice = JOptionPane.showOptionDialog(
                null,
                "Choose UI mode:",
                "Student Management System",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]
            );
            
            useConsole = (uiChoice == 0);
            
            String[] storageOptions = {"File System", "Database"};
            int storageChoice = JOptionPane.showOptionDialog(
                null,
                "Choose storage mode:",
                "Student Management System",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                storageOptions,
                storageOptions[0]
            );
            
            useDatabase[0] = (storageChoice == 1);
        }

        // Start the appropriate UI
        try {
            if (useConsole) {
                ConsoleUI ui = new ConsoleUI(useDatabase[0]);
                ui.start();
            } else {
                // Start the GUI
                javax.swing.SwingUtilities.invokeLater(() -> {
                    try {
                        new MainFrame(useDatabase[0]);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(
                            null,
                            "Error starting application: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                "Error starting application: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
}