package src;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Arrays;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserChangePassword {
    private Scene changePasswordScene;
    private PasswordField newPasswordField = new PasswordField();
    private Stage stage;
    private String username;



    public UserChangePassword(Stage primaryStage, String username){
        this.stage = primaryStage;
        this.username = username;
    }

    public void initializeComponents() {
        VBox changePasswordLayout = new VBox(10);
        changePasswordLayout.setPadding(new Insets(10));
        Button changePasswordButton = new Button("Change Password");
        Button logOutButton = new Button("Log Out");
        changePasswordButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                //changePassword();
                changePassword();
            }
        });

        logOutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                UserLogin login = new UserLogin(stage); //primary stage gets replaced with stage
                login.initializeComponents();
            }
        });

        changePasswordLayout.getChildren().addAll(new Label("Welcome " + username), new Label("New Password:"), newPasswordField, changePasswordButton,logOutButton);
        //this will link Layout to scene
        changePasswordScene = new Scene(changePasswordLayout, 300, 200);
        stage.setTitle("Change Password");
        stage.setScene(changePasswordScene);
        stage.show();
    }

    private void changePassword(){
        String newPassword = null;
        byte[] salt=UserSignup.createSalt(); //we also need to create a new salt just to be safe
        try {
            newPassword = UserSignup.generateHash(newPasswordField.getText(),"SHA-256",salt);  // i got too lazy to create a separate class for the hashing but  project better to compile all these functions in one relevant class name
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Connection con = DBUtils.establishConnection();
        String query = "UPDATE users SET password=?,salt=? WHERE username =?;";
        try{
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, newPassword);
            statement.setString(2, Arrays.toString(salt));
            statement.setString(3, username);

            int result=statement.executeUpdate();

            if (result == 1) {
                showAlert("Success", "Password successfully changed");
            } else {
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
