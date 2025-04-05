package com.example.testing101;

import java.sql.Date;
import java.time.LocalDate;

public final class  AdoptionRequest {

    private int petId;
    private int customerId;
    private int userId;
    private Date adopted;
    private Boolean booked;
    private String petName;          // Added
    private String customerFirstName; // Added

    // Updated constructor
    public AdoptionRequest(int petId, int customerId, int userId, Date adopted,
                           Date returnedDate, Boolean booked, String petName, String customerFirstName) {
        this.petId = petId;
        this.customerId = customerId;
        this.userId = userId;
        this.adopted = adopted;
        this.booked = booked;
        this.petName = petName;           // Initialize new fields
        this.customerFirstName = customerFirstName;
    }

    // Add getters for new fields
    public int getPetId() { return petId; }
    public int getCustomerId() { return customerId; }
    public int getUserId() { return userId; }
    public Date getAdopted() { return adopted; }
    public Boolean getBooked() { return booked; }
    public String getPetName() { return petName; }
    public String getCustomerFirstName() { return customerFirstName; }
}
