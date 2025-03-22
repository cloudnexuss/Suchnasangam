package com.example.suchna_sangam.model;

public class SignupRequest {
    private String name;
    private String email;
    private String role;
    private String password;
    private String district;
    private String circle;

    public SignupRequest(String name, String email, String role, String password, String district, String circle) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.password = password;
        this.district = district;
        this.circle = circle;
    }

    public String getName() {
        return this.name;
    }

    public String getDistrict() {
        return this.district;
    }

    public String getEmail() {
        return this.email;
    }

    public String getRole() {
        return this.role;
    }

    public String getPassword() {
        return this.password;
    }

    public String getCircle() {
        return this.circle;
    }
}

