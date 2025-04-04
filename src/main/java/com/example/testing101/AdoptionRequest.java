package com.example.testing101;

import java.sql.Date;
import java.time.LocalDate;

public class AdoptionRequest {

    private int petId;
    private int customerId;
    private int userId;
    private Date Adopted;
    private Date returnedDate;
    private Boolean booked; // "Pending", "Approved", "Rejected"

    // Constructor
    public AdoptionRequest(int petId, int customerId, int userId,
                           Date Adopted, Date returnedDate,
                           Boolean booked) {
        this.petId = petId;
        this.customerId = customerId;
        this.userId = userId;
        this.Adopted = Adopted;
        this.returnedDate = returnedDate;
        this.booked = booked;
    }

    public int getUserId() {
        return userId;
    }

    public int getPetId() {
        return petId;
    }
    public int getCustomerId() { return customerId; }
    public Boolean getBooked() { return booked; }
    public Date getAdopted() { return Adopted; }
    public Date getReturnedDate() { return returnedDate; }
}
