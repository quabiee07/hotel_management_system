package com.example.hotel_management_system;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;


public class HospitalPane extends Application {
    private final DatabaseService databaseService = new DatabaseService();

    private final TextField nameField = new TextField();
    private final TextField ageField = new TextField();
    private final ComboBox<String> genderBox = new ComboBox<>();
    private final TextField phoneField = new TextField();
    private final TextField diagnosisField = new TextField();
    private final DatePicker admissionDatePicker = new DatePicker();
    private final Label statusLabel = new Label();
    Alert success = new Alert(Alert.AlertType.INFORMATION);
    // ObservableList to hold patient data
    private ObservableList<Patient> patients = FXCollections.observableArrayList();

    // TableView for displaying patients
    private TableView<Patient> tableView;

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        Label heading = new Label("Hospital Management System");
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Label subtitle = new Label("Register as a patient in our system");
        VBox header = new VBox(5, heading, subtitle);

        header.setPadding(new Insets(0, 0, 20, 0));
        root.setTop(header);
        fetchPatients();


        tableView = new TableView<>();
        setTableView();


        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(14);
        form.setPadding(new Insets(20));


        nameField.setPromptText("Full name");
        ageField.setPromptText("Age in years");
        diagnosisField.setPromptText("What's your diagnosis");
        phoneField.setPromptText("Enter phone number");
        genderBox.getItems().addAll("Female", "Male", "Non-binary", "Prefer not to say");
        genderBox.setPromptText("Select gender");
        admissionDatePicker.setPromptText("Select admission date");


        addRow(form, 0, "Patient name:", nameField);
        addRow(form, 1, "Age:", ageField);
        addRow(form, 2, "Gender:", genderBox);
        addRow(form, 3, "Phone:", phoneField);
        addRow(form, 4, "Diagnosis:", diagnosisField);
        addRow(form, 5, "Admission Date:", admissionDatePicker);


        ColumnConstraints labels = new ColumnConstraints(200);
        ColumnConstraints fields = new ColumnConstraints();
        fields.setHgrow(Priority.ALWAYS);

        form.getColumnConstraints().addAll(labels, fields);
        Button addButton = new Button("Add");
        addButton.setDefaultButton(true);
        addButton.setOnAction(e -> registerPatient());
        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> {
        });
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
        });
        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> clearForm());


        HBox buttons = new HBox(10, addButton, updateButton, deleteButton, clearButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox content = new VBox(12, form, buttons, statusLabel);
        HBox hBox = new HBox(50, content, tableView);
        content.setMaxWidth(1200);
        root.setCenter(hBox);
        root.setBottom(new Label("JavaFX • JDBC • MySQL"));


        stage.setTitle("Hospital Management System");
        stage.setScene(new Scene(root, 1000, 550));
        stage.show();
    }

    private void addRow(GridPane grid, int row, String text, Control control) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold;");
        control.setMaxWidth(Double.MAX_VALUE);
        grid.add(label, 0, row);
        grid.add(control, 1, row);
    }

    private void setTableView() {
        TableColumn<Patient, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().name()));

        TableColumn<Patient, Integer> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().age()));

        TableColumn<Patient, String> genderColumn = new TableColumn<>("Gender");
        genderColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().gender()));

        TableColumn<Patient, String> phoneColumn = new TableColumn<>("Phone Number");
        phoneColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().phone()));

        TableColumn<Patient, String> diagnosisColumn = new TableColumn<>("Diagnosis");
        diagnosisColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().diagnosis()));

        TableColumn<Patient, LocalDate> admissionDateColumn = new TableColumn<>("Admission Date");
        admissionDateColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().admissionDate()));

        phoneColumn.setMinWidth(100);
        admissionDateColumn.setMinWidth(100);

        tableView.getColumns().addAll(nameColumn, ageColumn, phoneColumn, diagnosisColumn, admissionDateColumn);
        tableView.setItems(patients);
        tableView.setMaxWidth(Double.MAX_VALUE);
    }

    private void fetchPatients() {
        try {
            patients.setAll(databaseService.getAllPatients());
        } catch (SQLException ex) {
            setStatus("Could not load patients: " + ex.getMessage(), true);
        }
    }

    private void registerPatient() {

        int age, ageData;
        ageData = Integer.parseInt(ageField.getText().trim());
        Patient patient = new Patient(nameField.getText().trim(), ageData, genderBox.getValue(), phoneField.getText().trim(), diagnosisField.getText().trim(), admissionDatePicker.getValue());
        String name = patient.name();
        String ageValue = String.valueOf(patient.age());
        String gender = patient.gender();
        String phone = patient.phone();
        String diagnosis = patient.diagnosis();
        LocalDate admissionDate = patient.admissionDate();


        if (name.isEmpty() || gender == null || phone.isEmpty() || diagnosis.isEmpty() || admissionDate == null) {
            setStatus("Please complete all fields.", true);
            return;
        }


        try {
            age = Integer.parseInt(ageValue);
        } catch (NumberFormatException ex) {
            setStatus("Age must be a whole number.", true);
            return;
        }

        if (age < 16 || age > 100) {
            setStatus("Age must be between 16 and 100.", true);
            return;
        }

        if (!phone.matches("^[0-9]{10}$")) {
            setStatus("Enter a valid phone number.", true);
            return;
        }

        try {
            if (databaseService.registerPatient(name, age, gender, phone, diagnosis, admissionDate)) {
                showAlert();
                clearFields();
                fetchPatients();
            } else {
                setStatus("No record was inserted.", true);
            }
        } catch (SQLException ex) {
            setStatus("Database error: " + ex.getMessage(), true);
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        clearFields();
        setStatus("Form cleared.", false);
    }

    private void clearFields() {
        nameField.clear();
        ageField.clear();
        genderBox.getSelectionModel().clearSelection();
        phoneField.clear();
        diagnosisField.clear();
        admissionDatePicker.getEditor().clear();
    }

    private void setStatus(String message, boolean error) {
        statusLabel.setText(message);
        statusLabel.setStyle(error
                ? "-fx-text-fill: #b00020;"
                : "-fx-text-fill: #167545;");

    }

    private void showAlert() {
        success.setTitle("Information");
        success.setHeaderText("Status");

        success.setContentText("Patient registered successfully!");
        success.show();
    }
}
