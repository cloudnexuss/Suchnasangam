package com.example.suchna_sangam.model;

public class User {
    private String id;
    private String name;
    private String email;
    private String districtId;
    private String role;
    private String password;
    private String createdAt;
    private String circle;
    private String lastLogin;

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getDistrictId() {
        return this.districtId;
    }

    public String getRole() {
        return this.role;
    }

    public String getPassword() {
        return this.password;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public String getCircle() {
        return this.circle;
    }

    public String getLastLogin() {
        return this.lastLogin;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setDistrictId(final String districtId) {
        this.districtId = districtId;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setCreatedAt(final String createdAt) {
        this.createdAt = createdAt;
    }

    public void setCircle(final String circle) {
        this.circle = circle;
    }

    public void setLastLogin(final String lastLogin) {
        this.lastLogin = lastLogin;
    }
}
