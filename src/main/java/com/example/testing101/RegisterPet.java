package com.example.testing101;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
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
    @FXML
    private RadioButton adoptedButton;
    @FXML
    private RadioButton notAdoptedButton;
    @FXML
    private RadioButton avaialbleButton;
    @FXML
    private RadioButton notAvaialbleButton;
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
        notAdoptedButton.setSelected(false);
    }
    public void notAdoptedOption(ActionEvent event){
        this.adoptionStatus = false;
        adoptedButton.setSelected(false);
    }
    public void availableOption(ActionEvent event){
        this.availablity = true;
        notAvaialbleButton.setSelected(false);
    }
    public void notAvailableOption(ActionEvent event){
        this.availablity = false;
        avaialbleButton.setSelected(false);
    }

    public void registerPetAction(ActionEvent event) throws SQLException, IOException {
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
        if (!InputValidationFunctions.isValidLastName(specie)){
            errorLabel.setText("Invalid pet specie");
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
        Connection con = DBUtils.establishConnection();
        String sql = "INSERT INTO pet (name, dob, gender, specie, picture, adoptionStatus, availability, healthStatus) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = con.prepareStatement(sql);
        Boolean adoption = true; //assuming its adopted
        Boolean available = true; // assuming its available
        if (!adoptionStatus){
            adoption = false;
        }
        if (!availablity){
            available = false;
        }
        stmt.setString(1, name);                                // from nameField
        stmt.setString(2, formattedDob);          // from dobField (format: yyyy-MM-dd)
        stmt.setString(3, gender);                              // from genderField
        stmt.setString(4, specie);                              // from specieField
        stmt.setString(5, null);                                // picture is not in the form yet
        stmt.setBoolean(6, adoption);
        stmt.setBoolean(7, available);
        stmt.setString(8, healthStatus.isEmpty() ? null : healthStatus); // from healthStatusField

        stmt.executeUpdate();
        showAlert("Pet added","Pet has been successfully added to the database",event);
    }
    private void showAlert(String title, String content,ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("ManagerScreen.fxml"));
        Parent root = loader.load();
        Manager controller = loader.getController();
        controller.setUsername(this.username);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
