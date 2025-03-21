package com.example.suchna_sangam.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}

