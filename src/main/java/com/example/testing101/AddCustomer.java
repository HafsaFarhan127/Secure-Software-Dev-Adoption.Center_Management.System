package com.example.testing101;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;

public class AddCustomer {
    @FXML private Button backButton;
    private String gender;
    @FXML private RadioButton maleRadioButton;
    @FXML private RadioButton femaleRadioButton;
    @FXML
    private TextField firstNameField;
    @FXML
    private Label errorLabelField;
    @FXML
    private Button registerButton;
     //here we add @FXML when we set the scope to private else if its public you wouldnt need to
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField dobField;
    @FXML
    private TextField phoneField;
    private boolean flag=false;
    private Stage stage;
    private Scene scene;


    @FXML
    public void initialize() {
        ToggleGroup genderToggleGroup = new ToggleGroup();
        femaleRadioButton.setToggleGroup(genderToggleGroup);
        maleRadioButton.setToggleGroup(genderToggleGroup);
    }

    public void setGendertoFemale(ActionEvent actionEvent) {
        gender="F";
    }

    public void setGendertoMale(ActionEvent actionEvent) {
        gender="M";
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

    @FXML private boolean registerCustomerAction(ActionEvent event) throws IOException {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String dob = dobField.getText();
        String phone = phoneField.getText();

        // 3. Validate each field with if-else checks:
        if(firstName.isEmpty() || lastName.isEmpty() || dob.isEmpty() || phone.isEmpty() || gender.isEmpty() ) {
            errorLabelField.setText("Fields can not be empty!");
            errorLabelField.setVisible(true); // Show on error
            return false;
        }

        // First Name check
        if (!InputValidationFunctions.isValidFirstName(firstName)) {
            errorLabelField.setText("Invalid First Name! Must contain letters only and not be empty.");
            errorLabelField.setVisible(true); // Show on error
            return false;  // Stop processing if invalid
        }

        // Last Name check
        if (!InputValidationFunctions.isValidLastName(lastName)) {
            errorLabelField.setText("Invalid Last Name! Must contain letters only and not be empty.");
            errorLabelField.setVisible(true); // Show on error
            return false;
        }

        // Date of Birth check & conversion
        String formattedDob = InputValidationFunctions.validateAndFormatDOB(dob);
        if (formattedDob == null) {
            errorLabelField.setText("Invalid Date of Birth! Format must be dd-mm-yyyy (e.g., 15-04-1995).");
            errorLabelField.setVisible(true); // Show on error
            return false;
        }

        // Phone check (Qatari number)
        if (!InputValidationFunctions.isValidQatariPhoneNumber(phone)) {
            errorLabelField.setText("Invalid Phone Number! Must be a valid Qatari phone number with prefixes(+974 or 974)");
            errorLabelField.setVisible(true); // Show on error
            return false;
        }

        Connection con = DBUtils.establishConnection();
        String query = "INSERT INTO customers (gender,date_of_birth,contactNo,firstname,lastname)  VALUES (?, ?, ?, ?, ?);";
        try{
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, gender); //this is a way to protect against SQLi.
            statement.setString(2, formattedDob);
            statement.setString(3,phone);
            statement.setString(4, firstName);
            statement.setString(5, lastName);

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
            return true;
        }catch(Exception e) {
            e.printStackTrace();
            showAlert("Failure", "Failed to register user",event);
            errorLabelField.setVisible(false);
            return false;
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

