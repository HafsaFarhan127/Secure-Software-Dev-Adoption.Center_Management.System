package com.example.testing101;

public class MedicineRecord {
    private final String medicineName;
    private final String dosage;
    private final String date;

    public MedicineRecord(String medicineName, String dosage, String date) {
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.date = date;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public String getDosage() {
        return dosage;
    }

    public String getDate() {
        return date;
    }
}
