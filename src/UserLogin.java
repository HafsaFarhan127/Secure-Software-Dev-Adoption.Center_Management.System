package src;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserLogin {
    private Scene loginScene;
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Stage stage;
    public static byte[] salt;  //changed this to static because a lot of methods will be called from here even in chnage password

    public UserLogin(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void initializeComponents() {
        VBox loginLayout = new VBox(5); //reduing thisc number made both the new label and button show.
        loginLayout.setPadding(new Insets(10));
        Button loginButton = new Button("Sign In");
        Button signUpButton = new Button("Sign Up"); //new button to sign up if user dont exist, will use event handler for this to create a new scene and action
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    authenticate();
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        signUpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                //here ill initialize the usersignup class and set that as scene as soon as signup button created
                UserSignup userSignup = new UserSignup(stage);
                userSignup.initializeComponents();

            }
        });
        loginLayout.getChildren().addAll(new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                loginButton,new Label("OR"),signUpButton);  //here i added a label to show OR and then i associated the sign up button i created.

        loginScene = new Scene(loginLayout, 300, 200);
        stage.setTitle("User Login");
        stage.setScene(loginScene);
        stage.show();
    }

    /// this method authenticates the user and returns their role
    private String authenticate() throws NoSuchAlgorithmException {
        String username = usernameField.getText();
        String password = UserSignup.generateHash(passwordField.getText(),"SHA-256",getSalt(username));
        Connection con = DBUtils.establishConnection();
        String role = "";
        String query = "SELECT * FROM users WHERE username =? AND password=? ;";

        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, username); //here we are binding the ? to the variable storing userInput to pass sql query
            statement.setString(2, password); //this is a way to protect against SQLi.
            //System.out.println(statement.toString());
            ResultSet rs = statement.executeQuery(); //this takes in no param it automatically runs the query cuz of code in line 52.
    //this is called prepared statements and it works because
            if (rs.next()) {
                UserChangePassword changePassword = new UserChangePassword(stage, username);
                changePassword.initializeComponents();
                 role=rs.getString("role");  //this is how you get a field from sql
                System.out.println(role);
            } else {
                showAlert("Authentication Failed", "Invalid username or password.");
            }
            DBUtils.closeConnection(con, statement);
        } catch (Exception e) {
            //We will still print the exception error in the console to help us in the development
            e.printStackTrace();
            //But we will remove the above line, and display an alert to the user when the app is deployed
            showAlert("Database Error", "Failed to connect to the database.");
        }
        return role;
        }

    public static byte[] getSalt(String username) {
        Connection con = DBUtils.establishConnection();
        String query = "SELECT salt FROM users WHERE username =?";

        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, username); //here we are binding the ? to the variable storing userInput to pass sql query
            //System.out.println(statement.toString());
            ResultSet rs = statement.executeQuery(); //this takes in no param it automatically runs the query cuz of code in line 52.
            //this is called prepared statements and it works because

            if (rs.next()) {
                String data=rs.getString("salt");
                byte[] salt = stringToByteArray(data); // Convert String to byte array
                return salt; // Convert byte array to hex
            } else {
            }
            DBUtils.closeConnection(con, statement);
        } catch (Exception e) {
            //We will still print the exception error in the console to help us in the development
            e.printStackTrace();
            //But we will remove the above line, and display an alert to the user when the app is deployed
        }
        return salt;
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


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
