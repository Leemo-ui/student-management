CREATE DATABASE IF NOT EXISTS student_system;
USE student_system;

CREATE TABLE IF NOT EXISTS students (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS lecturers (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    department VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS programmes (
    code VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS courses (
    code VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    lecturer_id VARCHAR(20),
    FOREIGN KEY (lecturer_id) REFERENCES lecturers(id)
);

CREATE TABLE IF NOT EXISTS student_programme (
    student_id VARCHAR(20),
    programme_code VARCHAR(20),
    PRIMARY KEY (student_id),
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (programme_code) REFERENCES programmes(code)
);

CREATE TABLE IF NOT EXISTS student_course (
    student_id VARCHAR(20),
    course_code VARCHAR(20),
    score DOUBLE,
    PRIMARY KEY (student_id, course_code),
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (course_code) REFERENCES courses(code)
);
