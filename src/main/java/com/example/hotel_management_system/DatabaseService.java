package com.example.hotel_management_system;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hospital_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String INSERT_PATIENTS = "INSERT INTO patients " + "(name, age, gender, phone, diagnosis, admission_date) " + "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String DELETE_PATIENTS = "DELETE FROM patients " + "WHERE patient_id = ?";
    private static final String UPDATE_PATIENT = "UPDATE patients SET name = ?, age = ?, gender = ?, phone = ?, diagnosis = ?, admission_date = ? WHERE patient_id = ?";
    private static final String GET_PATIENTS = "SELECT patient_id, name, age, gender, phone, diagnosis, admission_date "
            + "FROM patients ORDER BY admission_date DESC";


    public boolean registerPatient(String name, int age, String gender, String phone, String diagnosis, LocalDate admissionDate) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(INSERT_PATIENTS)) {
            statement.setString(1, name);
            statement.setInt(2, age);
            statement.setString(3, gender);
            statement.setString(4, phone);
            statement.setString(5, diagnosis);
            statement.setDate(6, Date.valueOf(admissionDate));
            return statement.executeUpdate() == 1;
        }
    }

    public List<Patient> getAllPatients() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(GET_PATIENTS);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                int id = result.getInt("patient_id");
                String name = result.getString("name");
                int age = result.getInt("age");
                String gender = result.getString("gender");
                String phone = result.getString("phone");
                String diagnosis = result.getString("diagnosis");
                LocalDate admissionDate = result.getDate("admission_date").toLocalDate();

                patients.add(new Patient(id, name, age, gender, phone, diagnosis, admissionDate));
            }
        }
        return List.copyOf(patients);
    }

    public void deletePatient(Patient patient) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(DELETE_PATIENTS)) {
            statement.setInt(1, patient.patient_id());
            statement.executeUpdate();
        }
    }

    public boolean updatePatient(Patient patient) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(UPDATE_PATIENT)) {
            statement.setString(1, patient.name());
            statement.setInt(2, patient.age());
            statement.setString(3, patient.gender());
            statement.setString(4, patient.phone());
            statement.setString(5, patient.diagnosis());
            statement.setDate(6, Date.valueOf(patient.admissionDate()));
            statement.setInt(7, patient.patient_id());
            return statement.executeUpdate() == 1;
        }
    }


}
