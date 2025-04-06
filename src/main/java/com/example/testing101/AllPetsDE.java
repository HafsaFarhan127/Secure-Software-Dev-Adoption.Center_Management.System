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

public final class AllPetsDE implements Initializable {

    @FXML private TableView<PetInfo> petTable;
    @FXML private TableColumn<PetInfo, String> nameCol;
    @FXML private TableColumn<PetInfo, Integer> ageCol;
    @FXML private TableColumn<PetInfo, String> genderCol;
    @FXML private TableColumn<PetInfo, String> specieCol;
    private String username;
    private final ObservableList<PetInfo> petList = FXCollections.observableArrayList();
    public void setUsername(String username) {
        this.username = username;
    }
    public void goBackAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("EmployeeScreen.fxml"));
        Parent root = loader.load();
        //Manager controller = loader.getController();
        //controller.setUsername(this.username);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        specieCol.setCellValueFactory(new PropertyValueFactory<>("specie"));
        loadAvailablePets();
        petTable.setItems(petList);
    }

    private void loadAvailablePets() {
        String query = "SELECT * FROM pet WHERE adoptionStatus = 0 AND availability = 1";

        try (Connection conn = DBUtils.establishConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("Id");
                String name = rs.getString("name");
                String gender = rs.getString("gender");
                String specie = rs.getString("specie");

                Date dobDate = rs.getDate("dob");
                int age = (dobDate != null)
                        ? Period.between(dobDate.toLocalDate(), LocalDate.now()).getYears()
                        : 0;

                // Convert gender abbreviation to full string
                String fullGender = gender != null && gender.equalsIgnoreCase("m") ? "Male" : "Female";

                //changing here the default to be false for availability
                PetInfo pet = new PetInfo(id, name, fullGender, specie, age, null, false, false, dobDate != null ? dobDate.toString() : "");
                petList.add(pet);
            }

        } catch (SQLException e) {
           // e.printStackTrace();
        }
    }

}

