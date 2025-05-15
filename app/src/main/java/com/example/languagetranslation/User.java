package com.example.languagetranslation;

public class User {

    // Fields for user information
    private String name;
    private String email;
    private String password;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
        // No-argument constructor
    }

    // Constructor to initialize all fields
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getter and Setter methods for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter methods for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and Setter methods for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
