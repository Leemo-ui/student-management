# student-management

## Command Line Options

- `--gui`: Start with graphical user interface (default)
- `--console`: Start in console mode
- `--db`: Use database storage
- `--file`: Use file storage (default)
- `--help`: Display help information

## Usage Guide

### Adding a Student

1. Navigate to the "Students" tab
2. Fill in the student ID, first name, last name, and email
3. Click "Add Student"

### Adding a Lecturer

1. Navigate to the "Lecturers" tab
2. Fill in the lecturer ID, first name, last name, and email
3. Click "Add Lecturer"

### Adding a Course

1. Navigate to the "Courses" tab
2. Fill in the course code and course name
3. Click "Add Course"

### Registering a Student for a Course

1. Navigate to the "Registration" tab
2. Select the student and course from the dropdown menus
3. Click "Register for Course"

### Assigning a Lecturer to a Course

1. Navigate to the "Assign Lecturer" tab
2. Select the lecturer and course from the dropdown menus
3. Click "Assign Lecturer"

### Searching

1. Navigate to the "Search" tab
2. Select the search type
3. Enter the search term
4. Click "Search"

## Project Structure

## Database Schema

The system uses the following database tables:

- `students`: Stores student information
- `lecturers`: Stores lecturer information
- `courses`: Stores course information
- `programmes`: Stores programme information
- `student_course`: Maps students to courses
- `student_programme`: Maps students to programmes
- `programme_course`: Maps programmes to courses

## Troubleshooting

### Database Connection Issues

If you encounter database connection issues:

1. Verify MySQL server is running
2. Check that the database `student_system` exists
3. Ensure the username and password in `DatabaseStudentSystem.java` match your MySQL credentials
4. Make sure the MySQL JDBC driver is in your classpath

### Java Environment Issues

If you encounter Java-related issues:

1. Verify that JAVA_HOME is set correctly
2. Ensure you're using JDK 11 or higher
3. Check that Maven is installed and configured properly

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
