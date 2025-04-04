package com.example.testing101;

import javafx.beans.property.*;

public class AppointmentInfo {
    private final int petId;
    private final int customerId;
    private final int userId;
    private final StringProperty petName;
    private final IntegerProperty petAge;
    private final StringProperty customerName;
    private final StringProperty apptDate;

    public AppointmentInfo(int petId, int customerId, int userId, String petName, int petAge, String customerName, String apptDate) {
        this.petId = petId;
        this.customerId = customerId;
        this.userId = userId;
        this.petName = new SimpleStringProperty(petName);
        this.petAge = new SimpleIntegerProperty(petAge);
        this.customerName = new SimpleStringProperty(customerName);
        this.apptDate = new SimpleStringProperty(apptDate);
    }

    public int getPetId() { return petId; }
    public int getCustomerId() { return customerId; }
    public int getUserId() { return userId; }

    public String getPetName() { return petName.get(); }
    public int getPetAge() { return petAge.get(); }
    public String getCustomerName() { return customerName.get(); }
    public String getApptDate() { return apptDate.get(); }

    public StringProperty petNameProperty() { return petName; }
    public StringProperty customerNameProperty() { return customerName; }
    public StringProperty apptDateProperty() { return apptDate; }
}

