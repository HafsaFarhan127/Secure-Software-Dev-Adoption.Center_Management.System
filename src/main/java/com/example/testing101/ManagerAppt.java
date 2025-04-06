package com.example.testing101;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;

public class ManagerAppt implements Initializable {

    @FXML private TableView<AppointmentInfo> appointmentTable;
    @FXML private TableColumn<AppointmentInfo, String> petCol;
    @FXML private TableColumn<AppointmentInfo, String> customerCol;
    @FXML private TableColumn<AppointmentInfo, String> dateCol;

    @FXML private Label petNameLabel;
    @FXML private Label customerNameLabel;
    @FXML private Label statusLabel;

    @FXML private Button acceptButton;
    @FXML private Button rejectButton;

    private String username;
    private final ObservableList<AppointmentInfo> appointments = FXCollections.observableArrayList();
    private AppointmentInfo selectedAppointment;

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        petCol.setCellValueFactory(new PropertyValueFactory<>("petName"));
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("apptDate"));

        loadAppointments();
        appointmentTable.setItems(appointments);

        appointmentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedAppointment = newVal;
                petNameLabel.setText(newVal.getPetName());
                customerNameLabel.setText(newVal.getCustomerName());
                updateStatusLabelAndButtons();
            }
        });
    }

    private void loadAppointments() {
        String query = """
            SELECT 
                a.petId, a.customerID, a.userId, a.apptDate,
                p.name AS petName, p.dob,
                c.firstname, c.lastname
            FROM appointments a
            JOIN pet p ON a.petId = p.Id
            JOIN customers c ON a.customerID = c.customerID;
        """;

        try (Connection conn = DBUtils.establishConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int petId = rs.getInt("petId");
                int customerId = rs.getInt("customerID");
                int userId = rs.getInt("userId");
                String petName = rs.getString("petName");
                Date apptDateRaw = rs.getDate("apptDate");
                int age = apptDateRaw != null ? Period.between(apptDateRaw.toLocalDate(), LocalDate.now()).getYears() : 0;
                String customerName = rs.getString("firstname") + " " + rs.getString("lastname");
                String apptDate = apptDateRaw.toString();

                appointments.add(new AppointmentInfo(petId, customerId, userId, petName, age, customerName, apptDate));
            }

        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    private void updateStatusLabelAndButtons() {
        String query = "SELECT booked FROM adoptionhistory WHERE pet_Id = ? AND customerID = ? AND userId = ?";

        try (Connection conn = DBUtils.establishConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, selectedAppointment.getPetId());
            stmt.setInt(2, selectedAppointment.getCustomerId());
            stmt.setInt(3, selectedAppointment.getUserId());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int booked = rs.getInt("booked");
                if (booked == 1) {
                    statusLabel.setText("Accepted");
                } else {
                    statusLabel.setText("Rejected");
                }
                acceptButton.setDisable(true);
                rejectButton.setDisable(true);
                acceptButton.setOpacity(0.5);
                rejectButton.setOpacity(0.5);
            } else {
                statusLabel.setText("Pending");
                acceptButton.setDisable(false);
                rejectButton.setDisable(false);
                acceptButton.setOpacity(1);
                rejectButton.setOpacity(1);
            }

        } catch (SQLException e) {
            //e.printStackTrace();
            statusLabel.setText("Error");
        }
    }

    @FXML
    private void acceptAppointment(ActionEvent event) {
        if (selectedAppointment == null) return;

        try (Connection conn = DBUtils.establishConnection()) {
            String sql = "INSERT INTO adoptionhistory (pet_Id, userId, customerID, adopted, returned, booked) VALUES (?, ?, ?, NULL, NULL, 1)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, selectedAppointment.getPetId());
            stmt.setInt(2, selectedAppointment.getUserId());
            stmt.setInt(3, selectedAppointment.getCustomerId());
            stmt.executeUpdate();

            showAlert("Appointment accepted and added to history.");
            updateStatusLabelAndButtons();

        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    @FXML
    private void rejectAppointment(ActionEvent event) {
        if (selectedAppointment == null) return;

        try (Connection conn = DBUtils.establishConnection()) {
            String sql = "INSERT INTO adoptionhistory (pet_Id, userId, customerID, adopted, returned, booked) VALUES (?, ?, ?, NULL, NULL, 0)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, selectedAppointment.getPetId());
            stmt.setInt(2, selectedAppointment.getUserId());
            stmt.setInt(3, selectedAppointment.getCustomerId());
            stmt.executeUpdate();

            showAlert("Appointment rejected and added to history.");
            updateStatusLabelAndButtons();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void returnToMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("ManagerScreen.fxml"));
        Parent root = loader.load();
        Manager controller = loader.getController();
        controller.setUsername(this.username);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
