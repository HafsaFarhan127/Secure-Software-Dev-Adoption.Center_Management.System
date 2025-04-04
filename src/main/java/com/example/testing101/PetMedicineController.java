package com.example.testing101;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class PetMedicineController {

    @FXML private TableView<MedicineRecord> medicineTable;
    @FXML private TableColumn<MedicineRecord, String> medicineCol;
    @FXML private TableColumn<MedicineRecord, String> dosageCol;
    @FXML private TableColumn<MedicineRecord, String> dateCol;

    @FXML private TextField medicineField;
    @FXML private TextField dosageField;
    @FXML private TextField dateField;
    @FXML private Label errorLabel;

    private final ObservableList<MedicineRecord> medicineList = FXCollections.observableArrayList();

    // Set by the previous screen when opening this one
    private int petId;

    public void setPetId(int id) {
        this.petId = id;
        System.out.println(this.petId);
        loadMedicineRecords();
    }

    @FXML
    private void initialize() {
        medicineCol.setCellValueFactory(new PropertyValueFactory<>("medicineName"));
        dosageCol.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        medicineTable.setItems(medicineList);
    }


    private void loadMedicineRecords() {
        medicineList.clear();
        String query = """
            SELECT m.name, phm.Dosage, phm.apptDate
            FROM pet_has_medicine phm
            JOIN medicine m ON m.medicineId = phm.medicineId
            WHERE phm.petId = ?
        """;

        try (Connection conn = DBUtils.establishConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, petId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                String dosage = rs.getString("Dosage");
                String date = rs.getDate("apptDate").toString();

                medicineList.add(new MedicineRecord(name, dosage, date));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addMedicineAction(ActionEvent event) {
        String medicineName = medicineField.getText().trim();
        String dosage = dosageField.getText().trim();
        String dateStr = dateField.getText().trim();

        if (medicineName.isEmpty() || dosage.isEmpty() || dateStr.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }

        try {
            // Validate date format (yyyy-MM-dd)
            //LocalDate date = LocalDate.parse(dateStr);
            String formattedDate = null;
            if (!dateStr.isEmpty()){
                formattedDate = InputValidationFunctions.validateAndFormatDOB(dateStr);
                if (formattedDate == null){
                    errorLabel.setText("Incorrect Date format");
                    return;
                }
            }
            int medicineId = getMedicineIdByName(medicineName);
            if (medicineId == -1) {
                errorLabel.setText("Medicine not found in database.");
                return;
            }

            try (Connection conn = DBUtils.establishConnection()) {
                String sql = "INSERT INTO pet_has_medicine (petId, medicineId, Dosage, apptDate) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, petId);
                stmt.setInt(2, medicineId);
                stmt.setString(3, dosage);
                stmt.setDate(4, Date.valueOf(formattedDate));
                stmt.executeUpdate();

                errorLabel.setText("Medicine added successfully!");
                loadMedicineRecords(); // refresh table
                medicineField.clear();
                dosageField.clear();
                dateField.clear();
            }

        } catch (Exception e) {
            errorLabel.setText("Invalid date format. Use yyyy-MM-dd.");
        }
    }

    private int getMedicineIdByName(String name) throws SQLException {
        String query = "SELECT medicineId FROM medicine WHERE name = ?";
        try (Connection conn = DBUtils.establishConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("medicineId");
            }
        }
        return -1; // not found
    }

    @FXML
    private void returnToPrevious(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("AllPets.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
