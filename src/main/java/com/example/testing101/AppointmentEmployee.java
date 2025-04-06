package com.example.testing101;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class AppointmentEmployee {

    @FXML private TextField petIDField;
    @FXML private TextField customerIdField;
    @FXML private TextField phoneField;
    @FXML
    private TextField apptDateField;
    @FXML private Button registerButton;
    @FXML private Button backButton;
    @FXML private TextField apptTimeField;
    @FXML
    private Label errorLabelField;
    private boolean flag=false;
    private Stage stage;
    private Scene scene;
    private String userID=SessionManager.getInstance().getUserID(); //will be used for logging
    @FXML
    public void initialize() {
        errorLabelField.setVisible(false); // Hide by default
    }

    public void goBackButton(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("EmployeeScreen.fxml"));
        Parent root = fxmlLoader.load();
        Employee controller = fxmlLoader.getController();
        controller.setUsername(SessionManager.getInstance().getUsername());
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML private void bookApptAction(ActionEvent event) throws IOException, SQLException {

        // Validate all required fields are filled
        if (customerIdField.getText().trim().isEmpty() ||
                petIDField.getText().trim().isEmpty() ||
                apptDateField.getText().trim().isEmpty() ||
                apptTimeField.getText().trim().isEmpty() ||
                phoneField.getText().trim().isEmpty()) {

            errorLabelField.setText("Fields cannot be empty!");
            errorLabelField.setVisible(true);
            return;
        }

        Integer customerId = Integer.valueOf(customerIdField.getText());
        Integer PetID =  Integer.valueOf(petIDField.getText());
        String apptDate = apptDateField.getText();
        String apptTime = apptTimeField.getText();
        String phone = phoneField.getText();


        //to validate ID fields
        if (!InputValidationFunctions.isValidOther_customerID(customerId)) {
            errorLabelField.setText("Invalid ID!");
            errorLabelField.setVisible(true); // Show on error
            return ;
        }

        if (!InputValidationFunctions.isValidOther_petID(PetID)) {
            errorLabelField.setText("Invalid ID!");
            errorLabelField.setVisible(true); // Show on error
            return ;
        }


        // Date of Appt check & conversion
        String formattedApptDate = InputValidationFunctions.isValidApptDate(apptDate);
        if (formattedApptDate == null) {
            errorLabelField.setText("Invalid Date!");
            errorLabelField.setVisible(true); // Show on error
            return ;
        }

        // Time check from 9AM to 5PM
        if (!InputValidationFunctions.isTimeValid(apptTime)) {
            errorLabelField.setText("Invalid! 9AM-5PM only (e.g., 14:00 or 02:00 PM/AM).");
            errorLabelField.setVisible(true); // Show on error
            return ;
        }


        // Phone check (Qatari number)
        if (!InputValidationFunctions.isValidQatariPhoneNumber(phone)) {
            errorLabelField.setText("Invalid Contact! Enter valid Qatari format(+974)");
            errorLabelField.setVisible(true); // Show on error
            return ;
        }

        Connection con = DBUtils.establishConnection();
        String query = "INSERT INTO appointments (petId,customerID,userId,apptTime,apptDate,contactNo)  VALUES (?, ?, ?, ?, ?,?);";
        try{
            PreparedStatement statement = con.prepareStatement(query);
             //here we are binding the ? to the variable storing userInput to pass sql query
            statement.setInt(1, PetID);
            statement.setInt(2, customerId);
            statement.setInt(3, Integer.parseInt(userID));
            statement.setString(4, apptTime); //this is a way to protect against SQLi.
            statement.setString(5, formattedApptDate);
            statement.setString(6,phone);


            //System.out.println(statement.toString());
            int rs = statement.executeUpdate(); //for insert or updating we use executeUpdate

            if (rs==1) {
                flag=true;
                //here we do 1 cuz we use execute update and it returns an int value, also we use this or updating the db.
                showAlert("Success", "You have been added successfully!",event);

            } else {
                showAlert("Failure", "Failed to register user",event);
            }
            DBUtils.closeConnection(con, statement);

        }catch(Exception e) {
           //e.printStackTrace();
            showAlert("Failure", "Failed to register user",event);
            errorLabelField.setVisible(false);
            return ;
        }



    }






    private  void showAlert(String title, String content,ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();

        if (flag) { // Ensure 'flag' is set to true on successful registration
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("EmployeeScreen.fxml")); // Remove "name:"
            Parent root = fxmlLoader.load();
            Employee controller = fxmlLoader.getController();
            controller.setUsername(SessionManager.getInstance().getUsername()); //using the global method i made to universally use the username
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }
}
