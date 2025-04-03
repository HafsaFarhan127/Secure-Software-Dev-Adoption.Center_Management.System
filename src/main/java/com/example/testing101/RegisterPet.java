package com.example.testing101;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javafx.scene.control.Label;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.IOException;

public class RegisterPet {
    @FXML
    private Label errorLabel;
    @FXML
    private TextField nameField;
    @FXML
    private TextField dobField;
    @FXML
    private TextField genderField;
    @FXML
    private TextField specieField;
    @FXML
    private TextField healthStatusField;

    private String username;
    private Boolean adoptionStatus;
    private Boolean availablity;

    public void CancelOption(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("ManagerScreen.fxml"));
        Parent root = loader.load();
        Manager controller = loader.getController();
        controller.setUsername(this.username);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void saveUsername(String username){
        this.username = username;
    }
    public void adoptedOption(ActionEvent event){
        this.adoptionStatus = true;
    }
    public void notAdoptedOption(ActionEvent event){
        this.adoptionStatus = false;
    }
    public void availableOption(ActionEvent event){
        this.availablity = true;
    }
    public void notAvailableOption(ActionEvent event){
        this.availablity = false;
    }

    public void registerPetAction(ActionEvent event) throws SQLException {
        String name = nameField.getText().trim();
        String dob = dobField.getText().trim();  // Optional
        String gender = genderField.getText().trim();
        String specie = specieField.getText().trim();
        String healthStatus = healthStatusField.getText().trim();
        if (name.isEmpty() || gender.isEmpty() || specie.isEmpty()
                || adoptionStatus == null || availablity == null) {

            // Display error (make sure errorLabel exists and is properly referenced)
            if (errorLabel != null) {
                errorLabel.setText("Please fill all required fields and select statuses.");
                return;
            }
        }
        if (!InputValidationFunctions.isValidLastName(name)){
            errorLabel.setText("Invalid pet name");
            return;
        }
        // ensuring the gender is the correct value
        if (!InputValidationFunctions.isValidGender(gender)){
            errorLabel.setText("Gender has to be male or female!");
            return;
        }
        if (gender.equalsIgnoreCase("male")){ //because the data base only accepts characeters
            gender = "m";
        }
        else{
            gender = "f";
        }
        //date format check if its there
        String formattedDob = null;
        if (!dob.isEmpty()){
            formattedDob = InputValidationFunctions.validateAndFormatDOB(dob);
            if (formattedDob == null){
                errorLabel.setText("Incorrect Date format");
                return;
            }
        }
        // If you reach this point, validation passed
        System.out.println("Validation successful!");
        System.out.println("Name: " + name);
        System.out.println("DOB: " + dob);
        System.out.println("Gender: " + gender);
        System.out.println("Specie: " + specie);
        System.out.println("Health Status: " + healthStatus);
        System.out.println("Adopted: " + adoptionStatus);
        System.out.println("Available: " + availablity);
        Connection con = DBUtils.establishConnection();
        String sql = "INSERT INTO pet (name, dob, gender, specie, picture, adoptionStatus, availability, healthStatus) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = con.prepareStatement(sql);
        String adoption = "adopted"; //assuming its adopted
        String available = "available"; // assuming its available
        if (!adoptionStatus){
            adoption = "not adopted";
        }
        if (!availablity){
            available = "not available";
        }
        stmt.setString(1, name);                                // from nameField
        stmt.setString(2, formattedDob);          // from dobField (format: yyyy-MM-dd)
        stmt.setString(3, gender);                              // from genderField
        stmt.setString(4, specie);                              // from specieField
        stmt.setString(5, null);                                // picture is not in the form yet
        stmt.setString(6, adoption);
        stmt.setString(7, available);
        stmt.setString(8, healthStatus.isEmpty() ? null : healthStatus); // from healthStatusField

        stmt.executeUpdate();
        return;
    }
}
