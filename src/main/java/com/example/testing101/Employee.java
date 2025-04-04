package com.example.testing101;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        loadAdoptionRequests(); // Load data when screen opens
    }

    public static List<AdoptionRequest> fetchAdoptionRequests() {
        List<AdoptionRequest> requests = new ArrayList<>();
        Connection con = DBUtils.establishConnection();;

        try {

            String query = "SELECT * FROM adoptionhistory WHERE booked = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setBoolean(1, true); // Bind parameter
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                requests.add(new AdoptionRequest(  //here adoptionrequest now has its varaibles set and i can use the getters to get these values
                        rs.getInt("pet_Id"),
                        rs.getInt("customerID"),
                        rs.getInt("userId"),
                        rs.getDate("Adopted"),
                        null, // Assuming "returnedDate" is unused here
                        rs.getBoolean("booked")
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
                String.format("Pet ID: %d | Customer ID: %d | User ID: %s",
                        request.getPetId(),
                        request.getCustomerId(),
                        request.getUserId())
        );

        // Approve Button
        Button approveBtn = new Button("Approve");
        approveBtn.setStyle("-fx-background-color: #90EE90;");
        approveBtn.setOnAction(e -> {
            try {
                handleApproval(request, true);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Reject Button
        Button rejectBtn = new Button("Reject");
        rejectBtn.setStyle("-fx-background-color: #FFB6C1;");
        rejectBtn.setOnAction(e -> {
            try {
                handleApproval(request, false);
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
        String query2="UPDATE  adoptionhistory SET Adopted=? , booked=? WHERE pet_Id=? , userId=? , customerID=? ;";


        try {
            PreparedStatement statement1 = con.prepareStatement(query1);
            PreparedStatement statement2 = con.prepareStatement(query2);
            if (isApproved) {
                statement1.setBoolean(1, true);
                statement1.setBoolean(2, false);
                //these are for query2
                statement2.setDate(1,request.getAdopted());
                statement2.setBoolean(2, true);
                statement2.setInt(3, request.getPetId());
                statement2.setInt(4, request.getUserId());
                statement2.setInt(5, request.getCustomerId());

            }else{  statement1.setBoolean(1, false);
                statement1.setBoolean(2, true);
                //these are for query2
                statement2.setDate(1,null);
                statement2.setBoolean(2, false);
                statement2.setInt(3, request.getPetId());
                statement2.setInt(4, request.getUserId());
                statement2.setInt(5, request.getCustomerId());
            }

            statement1.setInt(3, request.getPetId()); // Bind parameter
            int rowsUpdated = statement1.executeUpdate();
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
