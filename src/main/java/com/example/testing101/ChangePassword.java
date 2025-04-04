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
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import static com.example.testing101.LoginController.showForcedAlert;

public class ChangePassword {
    @FXML private Label errorLabelField;
    @FXML private TextField petNameField;
    @FXML private PasswordField confirmPasswordID;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField newPasswordField;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private int invalidCount=0;

    public void changePasswordButton(ActionEvent event){
                changePassword();
    }
    public void backToLoginButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("loginScreen.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void changePassword(){

        if(invalidCount>=3){
            showForcedAlert(
                    "Security Alert",
                    "You must wait 60 seconds before proceeding."
            );
            invalidCount=0;
            return;
        }

        if (usernameField.getText().trim().isEmpty() || newPasswordField.getText().trim().isEmpty()||confirmPasswordID.getText().trim().isEmpty()) {
            errorLabelField.setText("Fields cannot be empty!");
            errorLabelField.setVisible(true);
            return;
        }
        if (!InputValidationFunctions.isValidPassword(newPasswordField.getText())) {
            errorLabelField.setText("Invalid password! Must be a valid password of min 8 length, at least 2 numbers, 2 letters, 1 special characters(!_)");
            return;
        }
        if (!newPasswordField.getText().equals(confirmPasswordID.getText())) {
            errorLabelField.setText("Passwords do not match!");
            errorLabelField.setVisible(true);
            return;
        }
        String newPassword = null;
        byte[] salt=AddUser.createSalt(); //we also need to create a new salt just to be safe
        try {
            newPassword = AddUser.generateHash(newPasswordField.getText(),"SHA-256",salt);  // i got too lazy to create a separate class for the hashing but  project better to compile all these functions in one relevant class name
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Connection con = DBUtils.establishConnection();
        String query = "UPDATE users SET password=?,salt=? WHERE userId =? AND firstPet=?;";
        try{
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, newPassword);
            statement.setString(2, Arrays.toString(salt));
            statement.setString(3, usernameField.getText());
            statement.setString(4, petNameField.getText().trim());

            int result=statement.executeUpdate();

            if (result == 1) {
                invalidCount=0;
                showAlert("Success", "Password successfully changed");
            } else {
                invalidCount++;
                //if the username does not exist,this error message gets shown so that an attacker can not figure out if a certain username legitimately exists or not.
                showAlert("Failure", "Failed to update password");
            }
            DBUtils.closeConnection(con, statement);
        }catch(Exception e){
            invalidCount++;
            e.printStackTrace();
            showAlert("Database Error", "Failed to connect to the database.");
        }
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
