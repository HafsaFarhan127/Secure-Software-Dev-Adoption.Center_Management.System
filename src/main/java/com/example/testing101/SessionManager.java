package com.example.testing101;

public class SessionManager {
    private static SessionManager instance;
    private String username;

    private SessionManager() {} // Private constructor

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
