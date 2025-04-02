package com.example.testing101;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AddCustomer {
    @FXML
    private TextField firstNameField;
    @FXML
    private Label errorLabelField;
    @FXML
    private Button registerButton;
    @FXML
    private TextField customerIdField; //here we add @FXML when we set the scope to private else if its public you wouldnt need to
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField dobField;
    @FXML
    private TextField phoneField;
    private Stage stage;
    private Scene scene;

    public void setGendertoFemale(ActionEvent actionEvent) {
    }

    public void setGendertoMale(ActionEvent actionEvent) {
    }

    public void goBackButton(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("EmployeeScreen.fxml"));
        Parent root = fxmlLoader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void registerCustomerAction(ActionEvent actionEvent) {
    }
}
