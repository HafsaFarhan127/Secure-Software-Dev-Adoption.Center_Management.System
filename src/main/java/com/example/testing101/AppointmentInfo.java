package com.example.testing101;



public class AppointmentInfo {
    private final int petId;
    private final int customerId;
    private final int userId;
    private final String petName;
    private final Integer petAge;
    private final String customerName;
    private final String apptDate;

    public AppointmentInfo(int petId, int customerId, int userId, String petName, int petAge, String customerName, String apptDate) {
        this.petId = petId;
        this.customerId = customerId;
        this.userId = userId;
        this.petName = petName;
        this.petAge = petAge;
        this.customerName = customerName;
        this.apptDate = apptDate;
    }
    public int getPetId() {
        return petId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getUserId() {
        return userId;
    }

    public String getPetName() {
        return petName;
    }

    public int getPetAge() {
        return petAge;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getApptDate() {
        return apptDate;
    }
}

