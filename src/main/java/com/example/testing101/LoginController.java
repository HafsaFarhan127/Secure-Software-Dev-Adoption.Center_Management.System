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

public class LoginController {
    @FXML
    private Button loginButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    private Stage stage;
    private Scene scene;
    private Parent root;
    public void loginButtonAction(ActionEvent event){
        try{
            authenticate(event);
        }
        catch(Exception e){
            loginMessageLabel.setText("Invalid credentials");
        }
    }
    public void changePasswordButton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("changePassword.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /// this method authenticates the user and returns their role
    private void authenticate(ActionEvent event) throws NoSuchAlgorithmException {
        String username = usernameField.getText();
        String password= passwordField.getText();
        try {
            byte[] salt = getSalt(username);
            //String salt = "no salt";
            if (salt == null) {
                showAlert("Error", "Invalid password.");
                return;
            }
            String hashedPassword = AddUser.generateHash(password, "SHA-256", salt);
            Connection con = DBUtils.establishConnection();
            String role = "";
            String query = "SELECT * FROM user WHERE userId =? AND password=? ;";

            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, username); //here we are binding the ? to the variable storing userInput to pass sql query
            statement.setString(2, hashedPassword); //this is a way to protect against SQLi.
            //System.out.println(statement.toString());
            ResultSet rs = statement.executeQuery(); //this takes in no param it automatically runs the query cuz of code in line 52.
            //this is called prepared statements and it works because
            if (rs.next()) { // I need to do this to add acounts
                /*UserChangePassword changePassword = new UserChangePassword(stage);
                changePassword.initializeComponents();*/ //put these lines  to a seprate function for change password .THESELINES GET REPLACES WITH INITILIAZATION OF ADMIN OR EMPLOYEE SCREEN
                role=rs.getString("role");  //this is how you get a field from sql

                if (role.equalsIgnoreCase("desk employee")){
                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("EmployeeScreen.fxml"));
                    Parent root = fxmlLoader.load();
                    Employee controller = fxmlLoader.getController();
                    controller.setUsername(username);
                    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                }

                if (role.equalsIgnoreCase("manager")) {
                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("ManagerScreen.fxml"));
                    Parent root = fxmlLoader.load();
                    Manager controller = fxmlLoader.getController();
                    controller.setUsername(username);
                    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                }




            } else {
                showAlert("Authentication Failed", "Invalid username or password.");
                DBUtils.closeConnection(con, statement);
            }
        } catch (Exception e) {
            //We will still print the exception error in the console to help us in the development
            e.printStackTrace();
            //But we will remove the above line, and display an alert to the user when the app is deployed
            showAlert("Error", "Authentication failed due to a system error.");
        }
    }

    public static byte[] getSalt(String username) {
        Connection con = DBUtils.establishConnection();
        String query = "SELECT salt FROM user WHERE userId =?";

        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, username); //here we are binding the ? to the variable storing userInput to pass sql query
            //System.out.println(statement.toString());
            ResultSet rs = statement.executeQuery(); //this takes in no param it automatically runs the query cuz of code in line 52.
            //this is called prepared statements and it works because

            if (rs.next()) {
                String data=rs.getString("salt");
                return stringToByteArray(data);
                // Convert byte array to hex
            } else {
                throw new SQLException("Username not found");
            }
        } catch (SQLException e) {
            //We will still print the exception error in the console to help us in the development
            e.printStackTrace();
            //But we will remove the above line, and display an alert to the user when the app is deployed
            showAlert("Error", "Invalid username"); //error msg should it be less descriptive?
            return null;
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //if i used rs.getByte it does mathematical operations and changes the actual string to hex instead of just changing the format which is what i wanted,
    // so i just used slicing and regex to parse the string into a byte array.
    public static byte[] stringToByteArray(String s) {
        String[] nums = s.substring(1, s.length() - 1).split(",");
        byte[] arr = new byte[nums.length];
        for (int i = 0; i < nums.length; i++) {
            arr[i] = (byte) Integer.parseInt(nums[i].trim());
        }
        return arr;
    }


    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}