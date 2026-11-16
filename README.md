# Hospital Management System

A JavaFX desktop application for managing hospital patient records. It stores patient details in MySQL and displays them in a table.

## Features

- Register a patient with name, age, gender, phone, diagnosis, and admission date.
- View registered patients, ordered by admission date.
- Edit a patient by selecting **Edit**, changing the form, and clicking **Update**.
- Delete a patient from the table with a confirmation prompt.
- Validate required fields, age, and phone number; show status and database errors in the interface.

## Requirements and setup

- Java 22 and Maven
- MySQL with a `hospital_db` database and a `patients` table. The table must include `patient_id` (an auto-incrementing primary key), `name`, `age`, `gender`, `phone`, `diagnosis`, and `admission_date` columns.
- Configure the MySQL URL, username, and password in `DatabaseService.java` to match your local setup. The current defaults are `jdbc:mysql://localhost:3306/hospital_db`, user `root`, and an empty password.

Run the application from the project directory:

```bash
mvn javafx:run
```
