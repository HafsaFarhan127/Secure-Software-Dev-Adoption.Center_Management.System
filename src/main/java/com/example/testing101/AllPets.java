package com.example.testing101;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;

public class AllPets implements Initializable {

    @FXML private TableView<PetInfo> allPets;
    @FXML private TableColumn<PetInfo, String> nameCol;
    @FXML private TableColumn<PetInfo, String> genderCol;
    @FXML private TableColumn<PetInfo, String> specieCol;
    @FXML private TableColumn<PetInfo, Integer> ageCol;
    @FXML private Label errorLabel;
    @FXML private TextField nameField;
    @FXML private TextField dobField;
    @FXML private TextField specieField;
    @FXML private TextField healthStatusField;
    @FXML private CheckBox availabilityBox;
    @FXML private CheckBox adoptedBox;
    private String username;
    @FXML
    private void updatePetAction(ActionEvent event) {
        if (selectedPetId == -1) {
            System.out.println("No pet selected.");
            return;
        }
        //insuring the case where some of the fields are empty
        String name = (nameField.getText() != null) ? nameField.getText().trim() : "";
        String dob = (dobField.getText() != null) ? dobField.getText().trim() : "";
        String specie = (specieField.getText() != null) ? specieField.getText().trim() : "";
        String healthStatus = (healthStatusField.getText() != null) ? healthStatusField.getText().trim() : "";
        Boolean adoptionStatus = adoptedBox.isSelected() ? true : false;
        Boolean availability = availabilityBox.isSelected() ? true :false;
        if (!InputValidationFunctions.isValidLastName(name)){
            errorLabel.setText("Invalid pet name");
            return;
        }
        if (!InputValidationFunctions.isValidSpecie(specie)){
            errorLabel.setText("Invalid pet specie");
            return;
        }
        String formattedDob = null;
        if (!dob.isEmpty()){
            formattedDob = InputValidationFunctions.validateAndFormatDOB(dob);
            if (formattedDob == null){
                errorLabel.setText("Incorrect Date format");
                return;
            }
        }

        try (Connection conn = DBUtils.establishConnection()) {
            String query = "UPDATE pet SET name = ?, dob = ?, specie = ?, healthStatus = ?, "
                    + "adoptionStatus = ?, availability = ? WHERE Id = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, dob.isEmpty() ? null : dob);
            stmt.setString(3, specie);
            stmt.setString(4, healthStatus.isEmpty() ? null : healthStatus);
            stmt.setBoolean(5, adoptionStatus);
            stmt.setBoolean(6, availability);
            stmt.setInt(7, selectedPetId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Update Successful");
                alert.setHeaderText(null);
                alert.setContentText("Pet information updated in the database.");
                alert.showAndWait();

                petList.clear();
                loadPetData(); // Refresh table
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int selectedPetId = -1; // stores the ID of the selected pet
    private final ObservableList<PetInfo> petList = FXCollections.observableArrayList();

    public void setUsername(String username) {
        this.username = username;
    }

    public void goBackAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("ManagerScreen.fxml"));
        Parent root = loader.load();
        Manager controller = loader.getController();
        controller.setUsername(this.username);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        specieCol.setCellValueFactory(new PropertyValueFactory<>("specie"));
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));

        loadPetData();
        allPets.setItems(petList);

        allPets.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });
    }

    private void loadPetData() {
        String query = "SELECT * FROM pet";

        try (Connection conn = DBUtils.establishConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("Id");
                String name = rs.getString("name");
                String gender = rs.getString("gender");
                String specie = rs.getString("specie");
                String healthStatus = rs.getString("healthStatus");
                Boolean adoptionStatus = rs.getBoolean("adoptionStatus");
                Boolean availability = rs.getBoolean("availability");

                Date dobDate = rs.getDate("dob");
                String dobStr = (dobDate != null) ? dobDate.toString() : "";
                int age = (dobDate != null)
                        ? Period.between(dobDate.toLocalDate(), LocalDate.now()).getYears()
                        : 0;
                String fullGender = "female";
                if (gender.equals("m")){
                    fullGender="male";
                }
                PetInfo pet = new PetInfo(id, name, fullGender, specie, age, healthStatus, adoptionStatus, availability, dobStr);
                petList.add(pet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateForm(PetInfo pet) {
        selectedPetId = pet.getId(); // Save pet ID
        nameField.setText(pet.getName());
        dobField.setText(pet.getDob());
        specieField.setText(pet.getSpecie());
        healthStatusField.setText(pet.getHealthStatus());
        availabilityBox.setSelected(pet.getAvailableStatus());
        adoptedBox.setSelected(pet.getAdoptedStatus());
    }
}
