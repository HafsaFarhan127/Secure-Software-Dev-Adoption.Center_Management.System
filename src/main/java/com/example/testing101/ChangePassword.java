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

public class ChangePassword {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField newPasswordField;
    private Stage stage;
    private Scene scene;
    private Parent root;
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
        String newPassword = null;
        byte[] salt=AddUser.createSalt(); //we also need to create a new salt just to be safe
        try {
            newPassword = AddUser.generateHash(newPasswordField.getText(),"SHA-256",salt);  // i got too lazy to create a separate class for the hashing but  project better to compile all these functions in one relevant class name
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Connection con = DBUtils.establishConnection();
        String query = "UPDATE users SET password=?,salt=? WHERE userId =?;";
        try{
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, newPassword);
            statement.setString(2, Arrays.toString(salt));
            statement.setString(3, usernameField.getText());

            int result=statement.executeUpdate();

            if (result == 1) {
                showAlert("Success", "Password successfully changed");
            } else {
                //if the username does not exist,this error message gets shown so that an attacker can not figure out if a certain username legitimately exists or not.
                showAlert("Failure", "Failed to update password");
            }
            DBUtils.closeConnection(con, statement);
        }catch(Exception e){
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
