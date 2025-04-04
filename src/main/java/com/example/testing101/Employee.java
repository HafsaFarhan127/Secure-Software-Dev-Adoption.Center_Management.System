package com.example.testing101;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.desktop.UserSessionListener;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Employee {
    @FXML private VBox requestsVBox;
    @FXML private Button registerCustomerButton;
    @FXML private Button viewPetsButton;
    @FXML
    private Button bookAptButton;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Button LogOutButton;
    private Stage stage;


    //from here is the dynami stuff
    @FXML
    public void initialize() {
        setUsername(SessionManager.getInstance().getUsername());
        loadAdoptionRequests(); // Load data when screen opens
    }

    public static List<AdoptionRequest> fetchAdoptionRequests() {
        List<AdoptionRequest> requests = new ArrayList<>();
        Connection con = DBUtils.establishConnection();;

        try {

            String query =
                    "SELECT ah.*, p.name AS petName, c.firstname AS customerFirstName " +
                            "FROM adoptionhistory ah " +
                            "INNER JOIN pet p ON ah.pet_Id = p.Id " +
                            "INNER JOIN customers c ON ah.customerID = c.customerID " +
                            "WHERE ah.booked = ? AND ah.Adopted IS NULL;";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setBoolean(1, true); // Bind parameter
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                requests.add(new AdoptionRequest(
                        rs.getInt("pet_Id"),
                        rs.getInt("customerID"),
                        rs.getInt("userId"),
                        rs.getDate("Adopted"),
                        null,
                        rs.getBoolean("booked"),
                        rs.getString("petName"),          // Added
                        rs.getString("customerFirstName") // Added
                ));
            }
            DBUtils.closeConnection(con, statement);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return requests;
    }

    private void loadAdoptionRequests() {
        requestsVBox.getChildren().clear(); // Clear existing entries

        List<AdoptionRequest> requests = fetchAdoptionRequests();
        for (AdoptionRequest request : requests) {
            HBox entry = createRequestEntry(request);
            requestsVBox.getChildren().add(entry);
        }
    }

    private HBox createRequestEntry(AdoptionRequest request) {
        HBox hbox = new HBox(10);
        hbox.setStyle("-fx-background-color: #FFD1DC; -fx-padding: 10;");

        // Request Details Label
        Label details = new Label(
                String.format("Pet: %s | Customer: %s | User ID: %d",
                        request.getPetName(),          // Use pet name
                        request.getCustomerFirstName(), // Use customer first name
                        request.getUserId())
        );



        // Approve Button
        Button approveBtn = new Button("Approve");
        DropShadow greenShadow = new DropShadow();
        greenShadow.setOffsetX(1.0);
        greenShadow.setOffsetY(1.0);
        greenShadow.setRadius(0.0);
        greenShadow.setWidth(0.0);
        greenShadow.setHeight(0.0);
        greenShadow.setColor(Color.rgb(0, 128, 0)); // Dark green color (RGB: 0,128,0)

        approveBtn.setEffect(greenShadow);
        approveBtn.setStyle("-fx-background-color: #50C878;");
        approveBtn.setOnAction(e -> {
            try {
                handleApproval(request, true);
                e.consume();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Reject Button
        Button rejectBtn = new Button("Reject");
        // Create the DropShadow effect with maroon color

// Apply the effect to your reject button
        DropShadow maroonShadow = new DropShadow();
        maroonShadow.setOffsetX(1.0);
        maroonShadow.setOffsetY(1.0);
        maroonShadow.setRadius(0.0);
        maroonShadow.setWidth(0.0);
        maroonShadow.setHeight(0.0);
        maroonShadow.setColor(Color.rgb(128, 0, 0)); // Maroon color (RGB: 128,0,0)

        rejectBtn.setEffect(maroonShadow);
        rejectBtn.setStyle("-fx-background-color: #B22222;");
        rejectBtn.setOnAction(e -> {
            try {
                handleApproval(request, false);
                e.consume();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        hbox.getChildren().addAll(details, approveBtn, rejectBtn);
        return hbox;
    }

    private void handleApproval(AdoptionRequest request, boolean isApproved) throws IOException {
        Connection con =  DBUtils.establishConnection();
        //if Approved i run this query1 if not then i set adoptionStatus to false availability to yes
        String query1 = "UPDATE pet SET adoptionStatus = ? ,availability=? WHERE Id = ?;";
        String query2="UPDATE  adoptionhistory SET Adopted= ? , booked= ? WHERE pet_Id=? AND userId=? AND customerID=? ;";


        try {
            PreparedStatement statement1 = con.prepareStatement(query1);
            PreparedStatement statement2 = con.prepareStatement(query2);
            if (isApproved) {
                LocalDate currentDate = LocalDate.now();
                statement1.setBoolean(1, true);
                statement1.setBoolean(2, false);
                //these are for query2
                statement2.setDate(1, Date.valueOf(currentDate));
                System.out.println(request.getAdopted());
                statement2.setBoolean(2, true);
                statement2.setInt(3, request.getPetId());
                statement2.setInt(4, request.getUserId());
                statement2.setInt(5, request.getCustomerId());

            }else{  statement1.setBoolean(1, false);
                statement1.setBoolean(2, true);
                //these are for query2
                statement2.setNull(1, Types.DATE); //makes it null
                statement2.setBoolean(2, false);
                statement2.setInt(3, request.getPetId());
                statement2.setInt(4, request.getUserId());
                statement2.setInt(5, request.getCustomerId());
            }

            statement1.setInt(3, request.getPetId()); // Bind parameter
            int rowsUpdated = statement1.executeUpdate();
            statement2.executeUpdate();
            if (rowsUpdated > 0) {
                loadAdoptionRequests(); // Refresh the list
            }
        } catch(Exception e) {
            e.printStackTrace();
            showAlert("Failure", "Failed to work");

        }
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
        welcomeLabel.setText("Welcome, "+username);
    }


    public void bookApt(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("bookAppt.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void viewPetsButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("allPetsDE.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void registerCustomer(ActionEvent actionEvent) throws IOException{
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("addCustomer.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }


    private  void showAlert(String title, String content) throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();

    }

}
