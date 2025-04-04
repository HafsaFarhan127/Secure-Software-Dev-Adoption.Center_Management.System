package com.example.testing101;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public class AddUser {
    @FXML private TextField petNameField;
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
    private boolean flag=false;
    private Stage stage;
    private Scene scene;
    private String username;
    private String hashAlgo="SHA-256";
    public void setUsername(String username){
        this.username = username;
    }
    public void goBackButton(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("ManagerScreen.fxml"));
        Parent root = fxmlLoader.load();
        Manager controller = fxmlLoader.getController();
        controller.setUsername(this.username);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void setRoleToManager(ActionEvent event){
        this.role = "manager";
        System.out.println("THE CURRENT ROLE SHOULD BE MANAGER AND IS: "+role);
    }
    public void setRoleToDE(ActionEvent event){
        this.role = "desk employee";
        System.out.println("THE CURRENT ROLE SHOULD DE AND IS: "+role);
    }
    public void registerUserAction(ActionEvent event) throws IOException {
        registerUser(event);
    }

    private void registerUser(ActionEvent event) throws IOException {
        Integer userId = Integer.valueOf(userIdField.getText());
        if (!InputValidationFunctions.isValidUserID(userId)) {
            errorLabelField.setText("Invalid User ID! Please enter a valid numeric User ID.");
            errorLabelField.setVisible(true); // Show on error
            return; // Stop processing if invalid
        }
        byte[] salt = createSalt();
        String password = passwordField.getText();
        String role = "";
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String dob = dobField.getText();
        String phone = phoneField.getText();
        String gender = genderField.getText();
        String firstPet = petNameField.getText().trim();


        // 3. Validate each field with if-else checks:
        if(firstName.isEmpty() || lastName.isEmpty() || dob.isEmpty() || phone.isEmpty() || gender.isEmpty() || password.isEmpty() || firstPet.isEmpty() ) {
            errorLabelField.setText("No empty fields!");
            errorLabelField.setVisible(true); // Show on error
            return;  // Stop processing if invalid
        }

        if (!InputValidationFunctions.isValidUserID(userId)) {
            errorLabelField.setText("Invalid ID! Must be 6 digits only and not be empty.");
            errorLabelField.setVisible(true); // Show on error
            return;  // Stop processing if invalid
        }

        // First Name check
        if (!InputValidationFunctions.isValidFirstName(firstName)) {
            errorLabelField.setText("Invalid First Name! Must contain letters only and not be empty.");
            errorLabelField.setVisible(true); // Show on error
            return;  // Stop processing if invalid
        }

        // Last Name check
        if (!InputValidationFunctions.isValidLastName(lastName)) {
            errorLabelField.setText("Invalid Last Name! Must contain letters only and not be empty.");
            errorLabelField.setVisible(true); // Show on error
            return;
        }

        // Date of Birth check & conversion
        String formattedDob = InputValidationFunctions.validateAndFormatDOB(dob);
        if (formattedDob == null) {
            errorLabelField.setText("Invalid Date of Birth! Format must be dd-mm-yyyy (e.g., 15-04-1995).");
            errorLabelField.setVisible(true); // Show on error
            return;
        }

        // Phone check (Qatari number)
        if (!InputValidationFunctions.isValidQatariPhoneNumber(phone)) {
            errorLabelField.setText("Invalid Phone Number! Must be a valid Qatari phone number with prefixes(+974 or 974)");
            errorLabelField.setVisible(true); // Show on error
            return;
        }

        if (!InputValidationFunctions.isValidPassword(password)) {
            errorLabelField.setText("Invalid password! Must be a valid password of min 8 length, at least 2 numbers, 2 letters, 1 special characters(!_)");
            errorLabelField.setVisible(true); // Show on error
            return;
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
            return;
        }
        if (gender.equalsIgnoreCase("male")){ //because the data base only accepts characeters
            gender = "m";
        }
        else{
            gender = "f";
        }

        //for role validation
        if (this.role == null) {
            errorLabelField.setText("Please select a role!");
            errorLabelField.setVisible(true);
            return;
        }
        /*else { this is taken care of at the start maybe
            RadioButton selectedRole = (RadioButton) roleToggleGroup.getSelectedToggle();
            role = selectedRole.getText();
        }*/

        Connection con = DBUtils.establishConnection();
        String query = "INSERT INTO users (userId,firstname,lastname,dob,phone,gender,role,password,salt,firstPet)  VALUES (?, ?, ?, ?, ?,?,?,?,?,?);";
        System.out.println("THE CURRENT ROLE IS"+this.role); // the role was left as an empty string so i am sending the attribute of the class instead of the variable
        System.out.println("THE CURRENT ROLE IS WITH OUT THIS"+role);
        try{
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, userId); //here we are binding the ? to the variable storing userInput to pass sql query
            statement.setString(2, firstName); //this is a way to protect against SQLi.
            statement.setString(3, lastName);
            statement.setString(4,formattedDob);  // need to fix this
            statement.setString(5, phone);
            statement.setString(6, gender);
            statement.setString(7, this.role);
            statement.setString(8, password);
            statement.setString(9, Arrays.toString(salt));  //typecasted it to strings because byte cant be put here as db has varchar value for salt
            statement.setString(10,firstPet );

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
        }catch(SQLException e) {
            e.printStackTrace();
            showAlert("Failure", "System be glitching",event);
            errorLabelField.setVisible(false);
        }catch(Exception e) {
            e.printStackTrace();
            showAlert("Failure", "Screen be glitching",event);
            errorLabelField.setVisible(false);
        }
    }
    private void showAlert(String title, String content,ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
        if (flag) { // Ensure 'flag' is set to true on successful registration
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("ManagerScreen.fxml")); // Remove "name:"
            Parent root = fxmlLoader.load();
            Manager controller = fxmlLoader.getController();
            controller.setUsername(SessionManager.getInstance().getUsername()); //using the global method i made to universally use the username
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
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
