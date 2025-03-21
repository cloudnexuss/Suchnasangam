package com.example.suchna_sangam.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    private String message;
    private String publishedOn;
    private Map<String, Boolean> seenBy; // ✅ String keys

    // Getters and Setters
}
