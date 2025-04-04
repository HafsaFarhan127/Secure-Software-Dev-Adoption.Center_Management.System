package com.example.testing101;

public class PetInfo {
    private int id;
    private String name;
    private String gender;
    private String specie;
    private int age;
    private String healthStatus;
    private String adoptedStatus;
    private boolean availableStatus;
    private String dob;

    public PetInfo(int id,String name, String gender, String specie, int age,
                   String healthStatus, String adoptedStatus, boolean availableStatus, String dob) {
        this.id= id;
        this.name = name;
        this.gender = gender;
        this.specie = specie;
        this.age = age;
        this.healthStatus = healthStatus;
        this.adoptedStatus = adoptedStatus;
        this.availableStatus = availableStatus;
        this.dob = dob;
    }

    // Getters only (or use properties if needed by JavaFX)
    public int getId() {return this.id;};
    public String getName() { return this.name; }
    public String getGender() { return this.gender; }
    public String getSpecie() { return this.specie; }
    public int getAge() { return this.age; }
    public String getHealthStatus() { return this.healthStatus; }
    public String getAdoptedStatus() { return this.adoptedStatus; }
    public boolean getAvailableStatus() { return this.availableStatus; }
    public String getDob(){return this.dob;}
}

