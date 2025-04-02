package com.example.demo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;

public class AddUser {
    @FXML
    private TextField userIdField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private String role;
    @FXML
    private TextField dobField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField genderField;
    @FXML
    private Button registerButton;
    @FXML
    private Label errorLabelField;
    private String hashAlgo="SHA-256";
    public void setRoleToManager(ActionEvent event){
        this.role = "manager";
    }
    public void setRoleToDE(ActionEvent event){
        this.role = "desk employee";
    }
    public void registerUserAction(ActionEvent event){
        registerUser();
    }

    private Boolean registerUser(){
        Integer userId = Integer.valueOf(userIdField.getText());
        byte[] salt=createSalt();
        String password = passwordField.getText();
        String role = "";
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String dob = dobField.getText();
        String phone = phoneField.getText();
        String gender = genderField.getText();

        // 3. Validate each field with if-else checks:

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

        if (!InputValidationFunctions.isValidPassword(password)) {
            errorLabelField.setText("Invalid password! Must be a valid password of min 8 length, at least 2 numbers, 2 letters, 1 special characters(!_)");
            errorLabelField.setVisible(true); // Show on error
            return false;
        } else{
            try {
                password = generateHash(password,hashAlgo,salt);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        // Gender check
        if (!InputValidationFunctions.isValidGender(gender)) {
            errorLabelField.setText("Invalid Gender! Must be 'male' or 'female'.");
            errorLabelField.setVisible(true); // Show on error
            return false;
        }
        if (gender.equalsIgnoreCase("male")){ //because the data base only accepts characeters
            gender = "m";
        }
        else{
            gender = "f";
        }

        //for role validation
        if (this.role == "") {
            errorLabelField.setText("Please select a role!");
            return false;
        }
        /*else { this is taken care of at the start maybe
            RadioButton selectedRole = (RadioButton) roleToggleGroup.getSelectedToggle();
            role = selectedRole.getText();
        }*/

        Connection con = DBUtils.establishConnection();
        String query = "INSERT INTO user (userId,firstname,lastname,dob,phone,gender,role,password,salt)  VALUES (?, ?, ?, ?, ?,?,?,?,?);";
        try{
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, userId); //here we are binding the ? to the variable storing userInput to pass sql query
            statement.setString(2, firstName); //this is a way to protect against SQLi.
            statement.setString(3, lastName);
            statement.setString(4,formattedDob);  // need to fix this
            statement.setString(5, phone);
            statement.setString(6, gender);
            statement.setString(7, role);
            statement.setString(8, password);
            statement.setString(9, Arrays.toString(salt));  //typecasted it to strings because byte cant be put here as db has varchar value for salt

            //System.out.println(statement.toString());
            int rs = statement.executeUpdate(); //for insert or updating we use executeUpdate

            if (rs==1) {
                //here we do 1 cuz we use execute update and it returns an int value, also we use this or updating the db.
                showAlert("Success", "You have been added successfully!");
            } else {
                showAlert("Failure", "Failed to register user");
            }
            DBUtils.closeConnection(con, statement);
            return true;
        }catch(Exception e) {
            e.printStackTrace();
            showAlert("Failure", "Failed to register user");
            errorLabelField.setVisible(false);
            return false;
        }
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public static   String generateHash(String password, String algorithm, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.reset();
        digest.update(salt);
        byte[] hash = digest.digest(password.getBytes());
        return bytesToStringHex(hash);
    }
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToStringHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    public static byte[] createSalt() {
        byte[] bytes = new byte[20];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return bytes;
    }

}
