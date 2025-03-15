
import java.sql.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;


public class UserSignup {

    //try removing this and see what happens

    public UserSignup(Stage primaryStage){
        this.stage = primaryStage;

    }


    private Scene UserSignUpScene;
    private Stage stage;
    private PasswordField PasswordField = new PasswordField();
    private TextField usernameField = new TextField();
    private TextField roleField = new TextField();
    private TextField firstNameField = new TextField();
    private TextField lastNameField = new TextField();
    private String hashAlgo="SHA-256";


        public void initializeComponents() {
            VBox UserSignUPLayout = new VBox(5); //here when i reduce ,the stage should fit
            UserSignUPLayout.setPadding(new Insets(10));
            Button RegisterUserButton = new Button("Register");
            RegisterUserButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event){
                    //addUser to db;
                    registerUser();
                    UserChangePassword changePassword = new UserChangePassword(stage, usernameField.getText());  //this line redirects the user to the change password scene.
                    changePassword.initializeComponents();
                }
            });



            UserSignUPLayout.getChildren().addAll(new Label("Welcome !"), new Label("Enter your username:"), usernameField,new Label("Enter your password:"),PasswordField,new Label("Enter your role:"),roleField,new Label("Enter your First Name:"),firstNameField,new Label("Enter your Last Name:"),lastNameField, RegisterUserButton);
                    //this line takes all the user inputs and then uses them as params to send the sql query.

            UserSignUpScene = new Scene(UserSignUPLayout, 300,Region.USE_COMPUTED_SIZE); //(stage,width,length:use computed size auto calculates the length with content inside)
            //this generates the new scene ? in a new program run remove this and try what happens
            stage.setTitle("User Sign Up");
            stage.setScene(UserSignUpScene);
            stage.show();
        }

        //for hashing im making the main change here and then for the check just add an additional if condition.
        private void registerUser(){

            //this is all within the same scope still so its like js front end we just get the values from the textfields itself
            String username = usernameField.getText();
            byte[] salt=createSalt();
            String password = null;
            try {
                password = generateHash(PasswordField.getText(),hashAlgo,salt);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            String role = roleField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();

            Connection con = DBUtils.establishConnection();
            String query = "INSERT INTO users (username,role,firstname,lastname,salt,password)  VALUES (?, ?, ?, ?, ?,?);";
            try{
                    PreparedStatement statement = con.prepareStatement(query);
                    statement.setString(1, username); //here we are binding the ? to the variable storing userInput to pass sql query
                    statement.setString(2, role); //this is a way to protect against SQLi.
                    statement.setString(3, firstName);
                    statement.setString(4, lastName);
                    statement.setString(5, Arrays.toString(salt));  //typecasted it to strings because byte cant be put here as db has varchar value for salt
                    statement.setString(6, password);

                //System.out.println(statement.toString());
                    int rs = statement.executeUpdate(); //for insert or updating we use executeUpdate

                if (rs==1) {
                    //here we do 1 cuz we use execute update and it returns an int value, also we use this or updating the db.
                    showAlert("Success", "You have been added successfully!");
                } else {
                    showAlert("Failure", "Failed to register user");
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

