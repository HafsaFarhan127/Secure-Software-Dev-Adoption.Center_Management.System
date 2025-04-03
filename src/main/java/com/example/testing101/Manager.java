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

public class Manager {
    @FXML
    private Label welcomeLabel;
    @FXML
    private Button logOutButton;
    private String username;
    private Stage stage;

    public void registerPage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("addUser.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void registerPetPage(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("RegisterPet.fxml"));
        Parent root = loader.load();
        RegisterPet controller = loader.getController();
        controller.saveUsername(this.username);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void viewAllPetsAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("allPets.fxml"));
        Parent root = loader.load();
        AllPets controller = loader.getController();
        controller.setUsername(this.username);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void logOutAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("loginScreen.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void setUsername(String username){
        this.username = username;
        welcomeLabel.setText("Welcome, "+this.username);
    }
}
