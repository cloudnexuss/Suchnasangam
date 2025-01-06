package com.example.suchna_sangam.model;

public class SignupRequest {
    private String name;
    private String email;
    private String role;
    private String password;

    // Constructor
    public SignupRequest(String name, String email, String role, String password) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }



    public String getEmail() {
        return email;
    }



    public String getRole() {
        return role;
    }


    public String getPassword() {
        return password;
    }

}

